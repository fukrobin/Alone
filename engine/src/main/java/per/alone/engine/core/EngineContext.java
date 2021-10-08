package per.alone.engine.core;

import jakarta.enterprise.inject.AmbiguousResolutionException;
import jakarta.enterprise.inject.Any;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.inject.WeldInstance;
import per.alone.engine.component.ComponentFactory;
import per.alone.engine.component.NoSuchComponentException;
import per.alone.engine.component.NoUniqueComponentException;
import per.alone.engine.renderer.RendererManager;
import per.alone.event.EventListenerManager;
import per.alone.event.EventQueue;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ComponentFactory 的基本实现，未提供动态刷新功能，
 * 因此在 EngineContext 加载完成后再注册新的 Bean 实际上会被忽略掉，
 * 触发按名称获取组件
 *
 * @author fkobin
 * @date 2020/4/4 21:24
 **/
@Slf4j
public final class EngineContext implements ComponentFactory {

    private final EventQueue eventQueue;

    private final EventListenerManager eventListenerManager;

    private final DebugInfo debugInfo;

    private RendererManager rendererManager;

    private boolean running;

    ///////////////////////////////
    /// Weld
    ///////////////////////////////

    private Weld weld;

    private WeldContainer container;

    public EngineContext() {
        eventListenerManager = new EventListenerManager();
        eventQueue           = new EventQueue(eventListenerManager);
        debugInfo            = DebugInfo.getInstance();
    }

    /**
     * 加载所有的组件。也是 CDI 容器的初始化阶段
     */
    void load() {
        if (container == null) {
            container = weld.initialize();
        }

        this.rendererManager = container.select(RendererManager.class).get();
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public EventListenerManager getEventHandlerManager() {
        return eventListenerManager;
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    ///////////////////////////////
    /// Component
    ///////////////////////////////

    /**
     * 获取指定类型的渲染器组件。
     * 请明确指定确定类型
     *
     * @param requiredType 所需类型
     * @return {@link EngineComponent}
     */
    @Override
    public <T extends EngineComponent> T getComponent(Class<T> requiredType) {
        try {
            return container.select(requiredType).get();
        } catch (AmbiguousResolutionException e) {
            throw new NoUniqueComponentException(requiredType);
        } catch (RuntimeException e) {
            throw new NoSuchComponentException(requiredType);
        }
    }

    /**
     * 获取指定类型的所有组件
     *
     * @param requiredType 所需类型
     * @return {@link List<EngineComponent>}
     */
    public <T extends EngineComponent> List<T> getComponents(Class<T> requiredType) {
        return container.select(requiredType, Any.Literal.INSTANCE)
                        .handlersStream()
                        .map(WeldInstance.Handler::get)
                        .collect(Collectors.toList());
    }

    public boolean isRunning() {
        return running;
    }

    void setRunning(boolean running) {
        this.running = running;
    }

    public Weld getWeld() {
        return weld;
    }

    void setWeld(Weld weld) {
        this.weld = weld;
    }

    public WeldContainer getContainer() {
        return container;
    }

    void setContainer(WeldContainer container) {
        this.container = container;
    }

    public RendererManager getRendererManager() {
        return rendererManager;
    }

    void setRendererManager(RendererManager rendererManager) {
        this.rendererManager = rendererManager;
    }

    @Override
    public void cleanup() {
        container.select(EngineComponent.class, Any.Literal.INSTANCE)
                 .handlersStream()
                 .map(WeldInstance.Handler::get)
                 .forEach(engineComponent -> {
                     try {
                         engineComponent.close();
                     } catch (Exception e) {
                         log.error("Renderer[{}] exception occurred during close:", engineComponent.getName(), e);
                     }
                 });
        container.shutdown();
    }
}
