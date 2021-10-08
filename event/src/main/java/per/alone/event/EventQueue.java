package per.alone.event;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * 事件模块的核心。接收来自任意位置的事件，并在每次渲染循环触发所有事件，
 * 所有的事件处理器（如果有的话）都会收到事件
 * //TODO 事件队列应该在所有其他引擎组件处理完成后（渲染或更新？）再触发事件
 *
 * @author fkrobin
 * @date 2021/9/17 18:02
 */
public class EventQueue {

    private final Queue<Event> eventQueue;

    private final EventDispatcher dispatcher;

    public EventQueue(EventDispatcher dispatcher) {
        this.eventQueue = new ArrayDeque<>();
        this.dispatcher = dispatcher;
    }

    /**
     * 发布事件，只有当事件可以被 dispatch 时才会真的加入队列
     *
     * @param event 事件
     */
    public void postEvent(Event event) {
        if (dispatcher.canDispatch(event.getEventType())) {
            eventQueue.add(event);
        }
    }

    public void fire() {
        Iterator<? extends Event> iterator = eventQueue.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            dispatcher.dispatchEvent(event);
            iterator.remove();
        }
    }

    public void fire(Event event) {
        dispatcher.dispatchEvent(event);
    }
}
