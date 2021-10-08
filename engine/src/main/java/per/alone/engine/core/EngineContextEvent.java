package per.alone.engine.core;

import per.alone.engine.event.EngineEvent;
import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * 引擎上下文事件，当在准备好引擎上下文时、更新时、引擎销毁时触发
 * TODO 待完善
 *
 * @author fkrobin
 * @date 2021/9/19 22:56
 */
public class EngineContextEvent extends EngineEvent {

    /**
     * 代表任何类型的引擎事件
     */
    public static final EventType<EngineContextEvent> ANY =
            new EventType<>(EventType.ROOT, "ENGINE_CONTEXT");

    /**
     * 当引擎上下文准备好后将会触发此类型的事件。此时引擎的组件还未加载
     */
    public static final EventType<EngineContextEvent> ENGINE_CONTEXT_PREPARED =
            new EventType<>(EngineContextEvent.ANY, "ENGINE_CONTEXT_PREPARED");

    /**
     * 当引擎上下文加载完成所有的组件后
     */
    public static final EventType<EngineContextEvent> ENGINE_CONTEXT_LOADED =
            new EventType<>(EngineContextEvent.ANY, "ENGINE_CONTEXT_LOADED");

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
        return super.getSource();
    }
}
