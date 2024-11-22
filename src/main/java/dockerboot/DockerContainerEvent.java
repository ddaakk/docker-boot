package dockerboot;

import org.springframework.context.ApplicationEvent;

/**
 * Event class for Docker container actions in a Spring application context.
 * Represents actions like START, STOP, and REMOVE for Docker containers.
 */
public class DockerContainerEvent extends ApplicationEvent {

    /**
     * Enum representing the type of action to be performed on the Docker container.
     */
    public enum Action {
        START, // Start the Docker container
        STOP,  // Stop the Docker container
        REMOVE // Remove the Docker container
    }

    private final Action action;

    /**
     * Constructs a new DockerContainerEvent.
     *
     * @param source the object that triggered the event
     * @param action the action to be performed on the Docker container
     */
    public DockerContainerEvent(Object source, Action action) {
        super(source);
        this.action = action;
    }

    /**
     * Gets the action associated with this event.
     *
     * @return the action to be performed on the Docker container
     */
    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "DockerContainerEvent{" +
                "action=" + action +
                ", source=" + source +
                '}';
    }
}