package per.alone.event;

import java.util.EventObject;

/**
 * 事件基类，每个事件斗鱼关联了一个事件类型，触发事件时，将智慧通知订阅了指定事件类型的观察者
 *
 * @author fkrobin
 * @date 2021/9/16 15:52
 */
public class Event extends EventObject {
    /**
     * 所有事件类型的父事件类型
     */
    public static EventType<Event> ANY = EventType.ROOT;

    protected EventType<? extends Event> eventType;

    protected Object source;

    public Event(EventType<? extends Event> eventType) {
        this(null, eventType);
    }

    public Event(Object source, EventType<? extends Event> eventType) {
        super(source);
        this.eventType = eventType;
    }

    public EventType<? extends Event> getEventType() {
        return eventType;
    }
}
