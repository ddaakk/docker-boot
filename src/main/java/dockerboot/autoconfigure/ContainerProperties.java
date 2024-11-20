package dockerboot.autoconfigure;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties class for Docker container settings.
 * This class provides a comprehensive set of properties to configure Docker containers
 * through Spring Boot's configuration mechanism.
 */
public class ContainerProperties {
    /** The name of the Docker image to be used for the container */
    private String imageName;

    /** The name to be assigned to the container when it's created */
    private String containerName;

    /** The type/category of the container for identification purposes */
    private String containerType;

    /**
     * Map of port mappings where:
     * - Key: Container port
     * - Value: Host port
     */
    private Map<Integer, Integer> ports = new HashMap<>();

    /** Flag to determine if this container configuration should be used */
    private boolean enabled = true;

    /**
     * Map of environment variables where:
     * - Key: Environment variable name
     * - Value: Environment variable value
     */
    private Map<String, String> environment = new HashMap<>();

    /** Command to be run in the container, overriding the image's CMD instruction */
    private String[] command;

    /** Entrypoint for the container, overriding the image's ENTRYPOINT instruction */
    private String[] entrypoint;

    /**
     * Map of labels to be applied to the container where:
     * - Key: Label name
     * - Value: Label value
     */
    private Map<String, String> labels = new HashMap<>();

    /**
     * Map of volume mappings where:
     * - Key: Host path
     * - Value: Container path
     */
    private Map<String, String> volumes = new HashMap<>();

    /**
     * Gets the Docker image name.
     * @return the image name including tag if specified (e.g., "nginx:latest")
     */
    public String getImageName() { return imageName; }

    /**
     * Sets the Docker image name.
     * @param imageName the name of the image to use
     */
    public void setImageName(String imageName) { this.imageName = imageName; }

    /**
     * Gets the container name.
     * @return the name to be assigned to the container
     */
    public String getContainerName() { return containerName; }

    /**
     * Sets the container name.
     * @param containerName the name to assign to the container
     */
    public void setContainerName(String containerName) { this.containerName = containerName; }

    /**
     * Gets the container type.
     * @return the type/category of the container
     */
    public String getContainerType() { return containerType; }

    /**
     * Sets the container type.
     * @param containerType the type/category to assign to the container
     */
    public void setContainerType(String containerType) { this.containerType = containerType; }

    /**
     * Gets the port mappings.
     * @return map of container port to host port mappings
     */
    public Map<Integer, Integer> getPorts() { return ports; }

    /**
     * Sets the port mappings.
     * @param ports map of container port to host port mappings
     */
    public void setPorts(Map<Integer, Integer> ports) { this.ports = ports; }

    /**
     * Checks if this container configuration is enabled.
     * @return true if the container should be created and started, false otherwise
     */
    public boolean isEnabled() { return enabled; }

    /**
     * Sets whether this container configuration is enabled.
     * @param enabled true to enable container creation and startup, false to disable
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    /**
     * Gets the environment variables.
     * @return map of environment variable definitions
     */
    public Map<String, String> getEnvironment() { return environment; }

    /**
     * Sets the environment variables.
     * @param environment map of environment variable names to values
     */
    public void setEnvironment(Map<String, String> environment) { this.environment = environment; }

    /**
     * Gets the command to be executed in the container.
     * @return array of command and its arguments
     */
    public String[] getCommand() { return command; }

    /**
     * Sets the command to be executed in the container.
     * @param command array of command and its arguments
     */
    public void setCommand(String[] command) { this.command = command; }

    /**
     * Gets the container entrypoint.
     * @return array of entrypoint command and its arguments
     */
    public String[] getEntrypoint() { return entrypoint; }

    /**
     * Sets the container entrypoint.
     * @param entrypoint array of entrypoint command and its arguments
     */
    public void setEntrypoint(String[] entrypoint) { this.entrypoint = entrypoint; }

    /**
     * Gets the container labels.
     * @return map of label names to values
     */
    public Map<String, String> getLabels() { return labels; }

    /**
     * Sets the container labels.
     * @param labels map of label names to values
     */
    public void setLabels(Map<String, String> labels) { this.labels = labels; }

    /**
     * Gets the volume mappings.
     * @return map of host paths to container paths
     */
    public Map<String, String> getVolumes() { return volumes; }

    /**
     * Sets the volume mappings.
     * @param volumes map of host paths to container paths
     */
    public void setVolumes(Map<String, String> volumes) { this.volumes = volumes; }
}