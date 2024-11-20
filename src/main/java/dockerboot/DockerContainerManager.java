package dockerboot;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import dockerboot.autoconfigure.ContainerProperties;

/**
 * Concrete implementation of AbstractDockerContainerManager that manages Docker containers
 * based on provided configuration properties.
 *
 * This class serves as a bridge between the abstract container management functionality
 * and the specific configuration provided through Spring Boot properties.
 *
 * Example usage:
 * <pre>
 * DockerContainerManager manager = new DockerContainerManager(
 *     "redis",
 *     dockerClient,
 *     containerProperties
 * );
 * </pre>
 */
public class DockerContainerManager extends AbstractDockerContainerManager {

    /**
     * Unique identifier for this container instance.
     * Used for logging and bean naming purposes.
     */
    private final String containerKey;

    /**
     * Configuration properties for this container instance.
     * Contains all the necessary settings for container creation and management.
     */
    private final ContainerProperties properties;

    /**
     * Constructs a new DockerContainerManager instance.
     *
     * @param containerKey unique identifier for this container instance
     * @param dockerClient the Docker client to use for container operations
     * @param properties configuration properties for this container
     */
    public DockerContainerManager(String containerKey,
                                  DockerClient dockerClient,
                                  ContainerProperties properties) {
        super(dockerClient);
        this.containerKey = containerKey;
        this.properties = properties;
    }

    /**
     * Gets the configured container name from properties.
     *
     * @return the name to be assigned to the Docker container
     */
    @Override
    protected String getContainerName() {
        return properties.getContainerName();
    }

    /**
     * Gets the configured Docker image name from properties.
     *
     * @return the full image name including tag if specified
     */
    @Override
    protected String getImageName() {
        return properties.getImageName();
    }

    /**
     * Gets the container type, which is the same as the container key.
     * Used primarily for logging and identification purposes.
     *
     * @return the container type/key
     */
    @Override
    protected String getContainerType() {
        return containerKey;
    }

    /**
     * Creates an array of ExposedPort objects from the configured port mappings.
     * This method transforms the port configuration from properties into Docker-Java
     * API compatible format.
     *
     * @return array of ExposedPort objects representing container ports to expose
     */
    @Override
    protected ExposedPort[] getExposedPorts() {
        return properties.getPorts().keySet().stream()
                .map(ExposedPort::new)
                .toArray(ExposedPort[]::new);
    }
}