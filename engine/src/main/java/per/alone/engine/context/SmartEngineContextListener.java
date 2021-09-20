package per.alone.engine.context;

import per.alone.event.EventType;

/**
 * 扩展了 EngineContextListener，添加了获取支持的事件类型
 *
 * @author fkrobin
 * @date 2021/9/20 17:53
 */
public interface SmartEngineContextListener extends EngineContextListener<EngineContextEvent> {

    /**
     * 确定此侦听器是否实际支持给定的事件类型
     *
     * @param eventType 事件类型
     * @return boolean 如果此类可以处理 eventType 类型的事件，则返回 true，否则返回 false
     */
    boolean supportsEventType(EventType<? extends EngineContextEvent> eventType);

}
