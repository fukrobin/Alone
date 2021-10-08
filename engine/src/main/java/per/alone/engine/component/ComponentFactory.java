package per.alone.engine.component;

import per.alone.engine.core.EngineComponent;

/**
 * 访问引擎所有功能组件的接口，此接口只提供了基本的获取功能，
 * 注册、动态刷新等功能需要使用指定的子类
 *
 * @author fkrobin
 * @date 2021/10/6 15:32
 */
public interface ComponentFactory {

    /**
     * 获得指定类型的组件
     *
     * @param requiredType 所需类型
     * @return {@link T} EngineComponent
     * @throws ComponentException 组件异常
     */
    <T extends EngineComponent> T getComponent(Class<T> requiredType) throws ComponentException;

    /**
     * 绝大部分组件都与 native 资源相关，因此需要手动挡进行资源释放，
     * ComponentFactory 负责在关闭时调用 {@link EngineComponent#close()}
     */
    void cleanup();
}
