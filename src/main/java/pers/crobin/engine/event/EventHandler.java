package pers.crobin.engine.event;

public interface EventHandler<T extends IEvent> {
    /**
     * Invoked when a specific event of the type for which this handler is
     * registered happens.
     *
     * @param event the event which occurred
     */
    void handle(T event);
}
