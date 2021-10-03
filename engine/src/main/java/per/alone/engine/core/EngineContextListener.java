package per.alone.engine.core;

import java.util.EventListener;

/**
 * 监听 EngineContextEvent
 * 基于观察者设计模式的标准java.util.EventListener接口
 *
 * @author fkrobin
 * @date 2021/9/19 23:19
 */
public interface EngineContextListener<E extends EngineContextEvent> extends EventListener {

    /**
     * 处理一个引擎上下文事件，可以通过{@link EngineContextEvent#getEventType()}
     * 判断发生的事件的具体类型
     *
     * @param engineContextEvent 引擎上下文事件
     */
    void onEngineContextEvent(EngineContextEvent engineContextEvent);
}
