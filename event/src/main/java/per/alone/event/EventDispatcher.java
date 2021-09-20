package per.alone.event;

/**
 * 事件调度器
 *
 * @author fkrobin
 * @date 2021/9/18 16:28
 */
public interface EventDispatcher {

    boolean canDispatch(EventType<? extends Event> eventType);

    /**
     * 调度事件
     *
     * @param event 事件
     */
    void dispatchEvent(Event event);
}
