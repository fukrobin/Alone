package per.alone.engine.core;

import per.alone.event.AloneEventListener;
import per.alone.event.EventType;

import java.util.Collections;
import java.util.List;

/**
 * 扩展了 EngineContextListener，添加了获取支持的事件类型
 *
 * @author fkrobin
 * @date 2021/9/20 17:53
 */
public interface EngineContextListener extends AloneEventListener<EngineContextEvent> {

    @Override
    default void onAloneEvent(EngineContextEvent event) {
        onEngineContextEvent(event);
    }

    /**
     * 默认支持任何 EngineContextEvent 的事件类型，
     * 实现可以根据需要进行更加具体的限制
     *
     * @return 支持任何 EngineContextEvent 的事件类型
     */
    @Override
    default List<EventType<EngineContextEvent>> supportsEventTypes() {
        return Collections.singletonList(EngineContextEvent.ANY);
    }

    default void onEngineContextEvent(EngineContextEvent engineContextEvent) {
        EventType<EngineContextEvent> eventType = engineContextEvent.getEventType();
        if (eventType.equals(EngineContextEvent.ENGINE_CONTEXT_PREPARED)) {
            onEngineContextPrepared(engineContextEvent);
        } else if (eventType.equals(EngineContextEvent.ENGINE_CONTEXT_LOADED)) {
            onEngineContextLoaded(engineContextEvent);
        }
    }

    default void onEngineContextPrepared(EngineContextEvent engineContextEvent) {

    }

    default void onEngineContextLoaded(EngineContextEvent engineContextEvent) {

    }
}
