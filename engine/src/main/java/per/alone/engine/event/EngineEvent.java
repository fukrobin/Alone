package per.alone.engine.event;

import per.alone.engine.core.EngineContext;
import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * 所有引擎事件的父类，包括组件、上下文等相关事件都应该继承此事件
 *
 * @author fkrobin
 * @date 2021/10/7 22:22
 */
public class EngineEvent extends Event {

    /**
     * 代表任何类型的引擎事件
     */
    public static final EventType<EngineEvent> ANY =
            new EventType<>(EventType.ROOT, "ENGINE");

    /**
     * 当引擎初始化完成将会触发此类型的事件
     */
    public static final EventType<EngineEvent> ENGINE_INITIALIZE =
            new EventType<>(EngineEvent.ANY, "ENGINE_INITIALIZE");

    /**
     * 当引擎初始化完成后，调用 start方法时将会触发此类型的事件
     */
    public static final EventType<EngineEvent> ENGINE_START =
            new EventType<>(EngineEvent.ANY, "ENGINE_START");

    /**
     * 当引擎关闭时将会出发此类型的事件
     */
    public static final EventType<EngineEvent> ENGINE_SHUTDOWN =
            new EventType<>(EngineEvent.ANY, "ENGINE_SHUTDOWN");

    public EngineEvent(EventType<? extends EngineEvent> eventType) {
        super(eventType);
    }

    public EngineEvent(Object source, EventType<? extends Event> eventType) {
        super(source, eventType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends EngineEvent> getEventType() {
        return (EventType<EngineEvent>) super.getEventType();
    }

    @Override
    public EngineContext getSource() {
        return (EngineContext) super.getSource();
    }

}
