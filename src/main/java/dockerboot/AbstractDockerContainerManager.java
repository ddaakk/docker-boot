package dockerboot;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import dockerboot.autoconfigure.ContainerProperties.LifecycleMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract base class for managing Docker containers within a Spring application context.
 * This class provides a framework for Docker container lifecycle management, implementing
 * both custom container operations and Spring's SmartLifecycle interface for automatic
 * container management during application startup and shutdown.
 *
 * Features:
 * - Automatic container lifecycle management (creation, starting, stopping, removal)
 * - Integration with Spring's application lifecycle
 * - Thread-safe container state management
 * - Automatic image pulling if not available locally
 * - Cleanup of existing containers with the same name
 */
public abstract class AbstractDockerContainerManager implements DockerResourceManager, SmartLifecycle {

    /** Logger instance specific to the implementing class */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Docker client instance for interacting with Docker daemon */
    protected final DockerClient dockerClient;

    /** Thread-safe flag indicating whether the container is running */
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);

    /** Thread-safe reference to the current container ID */
    protected final AtomicReference<String> containerId = new AtomicReference<>();

    /**
     * Constructs a new container manager with the specified Docker client.
     *
     * @param dockerClient the Docker client to use for container operations
     */
    protected AbstractDockerContainerManager(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    /**
     * Creates and starts a new container.
     * This method orchestrates the complete container initialization process:
     * 1. Removes any existing container with the same name
     * 2. Ensures the required image exists locally (downloads if needed)
     * 3. Creates a new container with the specified configuration
     * 4. Starts the container
     *
     * @return The ID of the created and started container
     * @throws RuntimeException if any step of the container creation or startup process fails
     */
    @Override
    public String createAndStart() {
        try {
            String containerName = getContainerName();
            removeExistingContainer(containerName);
            ensureImageExists();

            CreateContainerResponse container = dockerClient.createContainerCmd(getImageName())
                    .withName(containerName)
                    .withExposedPorts(getExposedPorts())
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            logger.info("{} container started. Container ID: {}", getContainerType(), container.getId());
            containerId.set(container.getId());
            return container.getId();
        } catch (Exception e) {
            logger.error("Failed to create and start {} container", getContainerType(), e);
            throw new RuntimeException("Cannot start " + getContainerType() + " container", e);
        }
    }

    /**
     * Removes any existing container with the specified name.
     * This method is called before creating a new container to ensure no naming conflicts.
     * If a container with the specified name exists, it will be forcefully removed.
     *
     * @param containerName Name of the container to remove
     */
    protected void removeExistingContainer(String containerName) {
        try {
            dockerClient.inspectContainerCmd(containerName).exec();
            logger.info("Existing {} container found. Removing...", getContainerType());
            dockerClient.removeContainerCmd(containerName).withForce(true).exec();
            logger.info("Existing {} container removed", getContainerType());
        } catch (NotFoundException e) {
            logger.info("No existing {} container found", getContainerType());
        }
    }

    /**
     * Ensures that the required Docker image exists locally.
     * If the image is not found locally, it will be downloaded from the configured registry.
     * This method is called before creating a new container to ensure the required image is available.
     *
     * @throws RuntimeException if the image download is interrupted
     */
    protected void ensureImageExists() {
        String imageName = getImageName();
        boolean imageExists = dockerClient.listImagesCmd()
                .exec()
                .stream()
                .anyMatch(image -> image.getRepoTags() != null && image.getRepoTags()[0].equals(imageName));

        if (!imageExists) {
            logger.info("Image {} not found locally. Downloading from Docker Hub...", imageName);
            try {
                dockerClient.pullImageCmd(imageName).start().awaitCompletion();
                logger.info("Image {} download completed", imageName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Image download was interrupted", e);
            }
        }
    }

    /**
     * Stops a running container.
     * This method sends a stop signal to the container and waits for it to stop.
     *
     * @param resourceId ID of the container to stop
     * @throws RuntimeException if the container stop operation fails
     */
    @Override
    public void stop(String resourceId) {
        try {
            dockerClient.stopContainerCmd(resourceId).exec();
            logger.info("{} container stopped. Container ID: {}", getContainerType(), resourceId);
        } catch (Exception e) {
            logger.error("Failed to stop {} container. Container ID: {}", getContainerType(), resourceId, e);
            throw new RuntimeException("Cannot stop " + getContainerType() + " container", e);
        }
    }

    /**
     * Removes a container.
     * This method removes the specified container from Docker.
     *
     * @param resourceId ID of the container to remove
     * @throws RuntimeException if the container removal fails
     */
    @Override
    public void remove(String resourceId) {
        try {
            dockerClient.removeContainerCmd(resourceId).exec();
            logger.info("{} container removed. Container ID: {}", getContainerType(), resourceId);
        } catch (Exception e) {
            logger.error("Failed to remove {} container. Container ID: {}", getContainerType(), resourceId, e);
            throw new RuntimeException("Cannot remove " + getContainerType() + " container", e);
        }
    }

    /**
     * Implementation of SmartLifecycle.start().
     * Creates and starts the container when the Spring application context starts.
     */
    @Override
    public void start() {
        if (getLifecycleMode() == LifecycleMode.NONE) {
            logger.info("Lifecycle mode is NONE: Skipping start of {} container", getContainerType());
            return;
        }

        logger.info("Spring application starting: Running {} container", getContainerType());
        createAndStart();
        isRunning.set(true);
    }


    /**
     * Implementation of SmartLifecycle.stop().
     * Stops and removes the container when the Spring application context stops.
     */
    @Override
    public void stop() {
        LifecycleMode mode = getLifecycleMode();

        if (mode == LifecycleMode.NONE || mode == LifecycleMode.START_ONLY) {
            logger.info("Lifecycle mode is {}: Skipping stop of {} container",
                    mode, getContainerType());
            isRunning.set(false);
            return;
        }

        logger.info("Spring application stopping: Cleaning up {} container", getContainerType());
        if (containerId.get() != null) {
            stop(containerId.get());
            remove(containerId.get());
        }
        isRunning.set(false);
    }

    /**
     * Event Listener to handle Docker container-related events.
     * Custom application events can trigger container actions.
     */
    @EventListener
    public void handleDockerEvent(DockerContainerEvent event) {
        logger.info("Received DockerContainerEvent: {}", event);
        switch (event.getAction()) {
            case START:
                createAndStart();
                break;
            case STOP:
                if (containerId.get() != null) {
                    stop(containerId.get());
                }
                break;
            case REMOVE:
                if (containerId.get() != null) {
                    remove(containerId.get());
                }
                break;
            default:
                logger.warn("Unknown action received in DockerContainerEvent: {}", event.getAction());
        }
    }

    /**
     * Implementation of SmartLifecycle.isRunning().
     * @return true if the container is running, false otherwise
     */
    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Implementation of SmartLifecycle.getPhase().
     * @return the phase value of 0 indicating no special startup/shutdown ordering
     */
    @Override
    public int getPhase() {
        return 0;
    }

    /**
     * Gets the name to be used for the container.
     * @return the container name
     */
    protected abstract String getContainerName();

    /**
     * Gets the Docker image name to be used for the container.
     * @return the image name including tag if specified
     */
    protected abstract String getImageName();

    /**
     * Gets the type or category name of the container for logging purposes.
     * @return the container type string
     */
    protected abstract String getContainerType();

    /**
     * Gets the array of ports to be exposed by the container.
     * @return array of ExposedPort objects representing container ports to expose
     */
    protected abstract ExposedPort[] getExposedPorts();

    /**
     * Gets the lifecycle mode for this container.
     * @return the configured lifecycle mode
     */
    protected abstract LifecycleMode getLifecycleMode();
}