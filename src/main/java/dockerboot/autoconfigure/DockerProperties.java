package dockerboot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Docker client and containers.
 * This class provides configuration options for Docker daemon connection, registry access,
 * and container definitions through Spring Boot's externalized configuration mechanism.
 *
 * Example YAML configuration:
 * <pre>
 * docker:
 *   host: tcp://192.168.99.100:2376
 *   tls-verify: true
 *   registry-url: https://registry.example.com
 *   registry-username: username
 *   registry-password: password
 *   containers:
 *     redis:
 *       image-name: redis:latest
 *       container-name: my-redis
 *       ports:
 *         6379: 6379
 * </pre>
 */
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {

    /**
     * Docker daemon host URL.
     * Examples:
     * - unix:///var/run/docker.sock (Unix socket)
     * - tcp://192.168.99.100:2376 (TCP with TLS)
     * - tcp://localhost:2375 (TCP without TLS)
     */
    private String host = "unix:///var/run/docker.sock";

    /**
     * Whether to verify TLS certificates when connecting to Docker daemon.
     * Should be set to true when using TLS for secure communication with Docker daemon.
     */
    private boolean tlsVerify = false;

    /**
     * Docker registry URL where images will be pulled from.
     * Examples:
     * - https://index.docker.io/v1/ (Docker Hub)
     * - https://registry.example.com (Private registry)
     */
    private String registryUrl = "https://index.docker.io/v1/";

    /**
     * Username for authenticating with the Docker registry.
     * Required if pulling images from a private registry or Docker Hub private repositories.
     */
    private String registryUsername;

    /**
     * Password for authenticating with the Docker registry.
     * Required if pulling images from a private registry or Docker Hub private repositories.
     */
    private String registryPassword;

    /**
     * Map of container configurations where:
     * - Key: Unique identifier for the container configuration
     * - Value: Container-specific configuration properties
     *
     * Each entry in this map will result in a managed Docker container.
     */
    private Map<String, ContainerProperties> containers = new HashMap<>();

    /**
     * Gets the Docker daemon host URL.
     * @return the configured Docker host URL
     */
    public String getHost() { return host; }

    /**
     * Sets the Docker daemon host URL.
     * @param host the Docker host URL to use
     */
    public void setHost(String host) { this.host = host; }

    /**
     * Checks if TLS verification is enabled.
     * @return true if TLS verification is enabled, false otherwise
     */
    public boolean isTlsVerify() { return tlsVerify; }

    /**
     * Sets whether TLS verification should be enabled.
     * @param tlsVerify true to enable TLS verification, false to disable
     */
    public void setTlsVerify(boolean tlsVerify) { this.tlsVerify = tlsVerify; }

    /**
     * Gets the Docker registry URL.
     * @return the configured registry URL
     */
    public String getRegistryUrl() { return registryUrl; }

    /**
     * Sets the Docker registry URL.
     * @param registryUrl the registry URL to use
     */
    public void setRegistryUrl(String registryUrl) { this.registryUrl = registryUrl; }

    /**
     * Gets the Docker registry username.
     * @return the configured registry username
     */
    public String getRegistryUsername() { return registryUsername; }

    /**
     * Sets the Docker registry username.
     * @param registryUsername the registry username to use
     */
    public void setRegistryUsername(String registryUsername) { this.registryUsername = registryUsername; }

    /**
     * Gets the Docker registry password.
     * @return the configured registry password
     */
    public String getRegistryPassword() { return registryPassword; }

    /**
     * Sets the Docker registry password.
     * @param registryPassword the registry password to use
     */
    public void setRegistryPassword(String registryPassword) { this.registryPassword = registryPassword; }

    /**
     * Gets the container configurations map.
     * @return map of container configurations
     */
    public Map<String, ContainerProperties> getContainers() { return containers; }

    /**
     * Sets the container configurations map.
     * @param containers map of container configurations to use
     */
    public void setContainers(Map<String, ContainerProperties> containers) { this.containers = containers; }
}