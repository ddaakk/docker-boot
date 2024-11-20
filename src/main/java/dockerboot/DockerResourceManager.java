package dockerboot;

/**
 * Interface defining core lifecycle management operations for Docker resources.
 * This interface provides a standard contract for managing Docker resources such as
 * containers, volumes, networks, etc. Implementations should handle the specific
 * details of managing each type of resource while adhering to this common interface.
 *
 * Key features:
 * - Resource lifecycle management (create, start, stop, remove)
 * - Consistent error handling through RuntimeExceptions
 * - Support for various Docker resource types
 *
 * Example usage:
 * <pre>
 * DockerResourceManager manager = new ContainerManager();
 * String resourceId = manager.createAndStart();
 * try {
 *     // Use the resource...
 * } finally {
 *     manager.stop(resourceId);
 *     manager.remove(resourceId);
 * }
 * </pre>
 */
public interface DockerResourceManager {

    /**
     * Creates and starts a Docker resource in a single operation.
     * This method encapsulates the complete initialization process for a Docker resource,
     * handling both the creation and startup phases. Implementations should ensure that
     * the resource is fully operational before returning.
     *
     * The method should:
     * 1. Create the resource with the configured settings
     * 2. Initialize any required dependencies
     * 3. Start the resource
     * 4. Verify the resource is running correctly
     *
     * @return The unique identifier of the created resource
     * @throws RuntimeException if any part of the creation or startup process fails,
     *         including but not limited to:
     *         - Resource creation failures
     *         - Network connectivity issues
     *         - Resource configuration problems
     *         - Startup timeout issues
     */
    String createAndStart();

    /**
     * Gracefully stops a running Docker resource.
     * This method should handle the orderly shutdown of the resource, ensuring that
     * any ongoing operations are completed or properly terminated before stopping.
     * Implementations should include appropriate timeout handling and cleanup operations.
     *
     * The method should:
     * 1. Validate the resource still exists
     * 2. Initiate graceful shutdown
     * 3. Wait for the resource to stop
     * 4. Perform any necessary cleanup
     *
     * @param resourceId The unique identifier of the resource to stop
     * @throws RuntimeException if the stop operation fails, including cases such as:
     *         - Resource not found
     *         - Permission issues
     *         - Timeout during stop operation
     *         - Resource already stopped
     */
    void stop(String resourceId);

    /**
     * Permanently removes a Docker resource from the system.
     * This method should handle the complete removal of the resource and any associated
     * artifacts. Implementations should ensure that the resource is stopped before
     * attempting removal and handle cleanup of any dependent resources.
     *
     * The method should:
     * 1. Verify the resource exists
     * 2. Ensure the resource is stopped
     * 3. Remove the resource and any associated artifacts
     * 4. Clean up any related resources or dependencies
     *
     * @param resourceId The unique identifier of the resource to remove
     * @throws RuntimeException if the removal operation fails, including cases such as:
     *         - Resource not found
     *         - Permission issues
     *         - Resource still in use
     *         - Dependent resources exist
     */
    void remove(String resourceId);
}