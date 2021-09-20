package per.alone.event;

public interface EventHandler<T extends Event> {
    /**
     * Invoked when a specific per.fkrobin.per.fkrobin.event of the type for which this handler is
     * registered happens.
     *
     * @param event the per.fkrobin.per.fkrobin.event which occurred
     */
    void handle(T event);
}
