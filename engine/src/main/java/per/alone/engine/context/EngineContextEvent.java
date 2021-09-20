package per.alone.engine.context;

import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * 引擎上下文事件，当在准备好引擎上下文时、更新时、引擎销毁时触发
 * TODO 待完善
 *
 * @author fkrobin
 * @date 2021/9/19 22:56
 */
public class EngineContextEvent extends Event {

    /**
     * 代表任何类型的引擎事件
     */
    public static final EventType<EngineContextEvent> ANY =
            new EventType<>(EventType.ROOT, "ENGINE_CONTEXT");

    /**
     * 当引擎上下文准备好后将会触发此类型的事件
     */
    public static final EventType<EngineContextEvent> PREPARED_ENGINE_CONTEXT =
            new EventType<>(EngineContextEvent.ANY, "PREPARED_ENGINE_CONTEXT");

    public EngineContextEvent(EngineContext source, EventType<? extends Event> eventType) {
        super(source, eventType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<EngineContextEvent> getEventType() {
        return (EventType<EngineContextEvent>) super.getEventType();
    }

    @Override
    public EngineContext getSource() {
        return (EngineContext) super.getSource();
    }
}
