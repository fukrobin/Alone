package per.alone.engine.core;

import lombok.extern.slf4j.Slf4j;
import per.alone.engine.renderer.RendererComponent;
import per.alone.engine.renderer.RendererManager;
import per.alone.event.EventHandlerManager;
import per.alone.event.EventQueue;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fkobin
 * @date 2020/4/4 21:24
 **/
@Slf4j
public final class EngineContext implements Closeable {

    private final Map<String, EngineComponent> engineComponents = new HashMap<>(16);

    private final Map<Class<?>, String[]> allComponentNamesByType = new ConcurrentHashMap<>(16);

    private final Map<Class<?>, List<?>> allComponent = new ConcurrentHashMap<>(16);

    private final List<String> componentClassNames = new ArrayList<>(64);

    private final EventQueue eventQueue;

    private final EventHandlerManager eventHandlerManager;

    private final DebugInfo debugInfo;

    private RendererManager rendererManager;

    private boolean running;

    public EngineContext() {
        eventHandlerManager = new EventHandlerManager();
        eventQueue          = new EventQueue(eventHandlerManager);
        debugInfo           = DebugInfo.getInstance();
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public EventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    ///////////////////////////////
    /// Component
    ///////////////////////////////

    public void registerComponent(EngineComponent engineComponent) {
        registerComponent(engineComponent.getClass().getName(), engineComponent);
    }

    /**
     * 注册一个引擎组件，如果组件同时也是渲染器组件，将会注册 RendererComponent
     *
     * @param name            组件名称，在整个引擎声明周期内，不同的组件之间的 name 应该唯一
     * @param engineComponent {@link EngineComponent}
     */
    public void registerComponent(String name, EngineComponent engineComponent) {
        assert name != null && name.length() > 0 : "Engine component name cannot be null or empty";
        Objects.requireNonNull(engineComponent);
        if (engineComponents.containsKey(name)) {
            throw new IllegalArgumentException("Engine component [" + name + "] has already register");
        }

        engineComponents.put(name, engineComponent);

        if (rendererManager != null && engineComponent instanceof RendererComponent) {
            rendererManager.addRenderer((RendererComponent) engineComponent);
        }

        componentClassNames.add(engineComponent.getClass().getName());
    }

    public void registerComponents(EngineComponent... engineComponents) {
        for (EngineComponent component : engineComponents) {
            registerComponent(component);
        }
    }

    public void registerComponents(Collection<? extends EngineComponent> engineComponents) {
        for (EngineComponent component : engineComponents) {
            registerComponent(component);
        }
    }

    public EngineComponent getComponent(String name) {
        return engineComponents.get(name);
    }

    /**
     * 获取指定类型的渲染器组件。请注意，目前并不支持多个候选类型，
     * 请明确指定确定类型
     *
     * @param requiredType 所需类型
     * @return {@link EngineComponent}
     */
    @SuppressWarnings("unchecked")
    public <T extends EngineComponent> T getComponent(Class<T> requiredType) {
        String[] candidateNames = getNamesForType(requiredType);
        String candidateName;
        if (candidateNames.length > 1) {
            candidateName = determinePrimaryCandidate(candidateNames, requiredType);
        } else {
            candidateName = candidateNames[0];
        }
        return (T) getComponent(candidateName);
    }

    private <T extends EngineComponent> String determinePrimaryCandidate(String[] candidateNames,
                                                                         Class<T> requiredType) {
        throw new UnsupportedOperationException("目前尚不支持同类型的多个候选组件，请明确指定需要获取的组件 Class");
    }

    /**
     * 获取指定类型的所有组件
     *
     * @param requiredType 所需类型
     * @return {@link List<EngineComponent>}
     */
    @SuppressWarnings("unchecked")
    public <T extends EngineComponent> List<T> getComponents(Class<T> requiredType) {
        if (allComponent.containsKey(requiredType)) {
            return (List<T>) allComponent.get(requiredType);
        }

        String[] candidateNames = getNamesForType(requiredType);
        List<T> result = new ArrayList<>();
        for (String candidateName : candidateNames) {
            result.add((T) getComponent(candidateName));
        }
        List<T> ts = Collections.unmodifiableList(result);
        allComponent.put(requiredType, ts);
        return ts;
    }

    public String[] getNamesForType(Class<?> type) {
        Map<Class<?>, String[]> cache = this.allComponentNamesByType;
        String[] resolvedNames = cache.get(type);
        if (resolvedNames != null) {
            return resolvedNames;
        }

        List<String> result = new ArrayList<>();
        for (String componentClassName : componentClassNames) {
            try {
                if (type.isAssignableFrom(Class.forName(componentClassName))) {
                    result.add(componentClassName);
                }
            } catch (ClassNotFoundException ignored) {

            }
        }
        String[] array = result.toArray(new String[0]);
        cache.put(type, array);
        return array;
    }

    ///////////////////////////////
    /// State
    ///////////////////////////////

    public boolean isRunning() {
        return running;
    }

    void setRunning(boolean running) {
        this.running = running;
    }

    public RendererManager getRendererManager() {
        return rendererManager;
    }

    void setRendererManager(RendererManager rendererManager) {
        this.rendererManager = rendererManager;
    }

    @Override
    public void close() {
        for (EngineComponent component : engineComponents.values()) {
            try {
                component.close();
            } catch (Exception e) {
                log.error("Renderer[{}] exception occurred during close:", component.getName(), e);
            }
        }
    }
}
