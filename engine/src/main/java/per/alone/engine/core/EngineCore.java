package per.alone.engine.core;

import jakarta.enterprise.inject.Any;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.inject.WeldInstance;
import per.alone.engine.renderer.RendererComponent;
import per.alone.engine.renderer.RendererManager;
import per.alone.engine.util.CpuTimer;
import per.alone.engine.util.GPUTimer;
import per.alone.engine.util.Timer;
import per.alone.engine.util.Utils;
import per.alone.event.EventListenerManager;
import per.alone.event.EventQueue;
import per.alone.stage.Window;
import per.alone.stage.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/8 19:18
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
class EngineCore extends Thread {
    private final Timer timer;

    private final GPUTimer gpuTimer;

    private final CpuTimer cpuTimer;

    private final Window window;

    private List<? extends EngineComponent> engineComponents;

    private EngineContext engineContext;

    private RendererManager rendererManager;

    private EventQueue eventQueue;

    private boolean running;

    private int fps = 180;

    private int ups = 30;

    protected EngineCore(Window window) {
        super(EngineCore.class.getSimpleName());
        this.timer    = new Timer();
        this.cpuTimer = new CpuTimer();
        this.gpuTimer = new GPUTimer();
        this.window   = window;
    }

    protected void start(Window window, EngineContext engineContext) {

    }

    private void initialize() {
        gpuTimer.initTimer();

        createEngineContext();

        eventQueue = engineContext.getEventQueue();

        prepareEngineContext();
        setEngineComponents(loadComponent(EngineComponent.class));

        Utils.submitTask(DebugInfo::getInstance);
    }

    private void createEngineContext() {
        engineContext = new EngineContextFactory().build();
    }

    /**
     * 准备 EngineContext，并触发 PreparedEngineContextEvent，
     * 组件可以通过监听此事件完成一些准备工作。
     */
    private void prepareEngineContext() {
        postEngineContextPreparedEvent();

        engineContext.load();
        registerEngineEventListeners();

        postEngineContextLoadedEvent();
    }

    /**
     * 发送 EngineContextEvent.ENGINE_CONTEXT_PREPARED 事件
     */
    private void postEngineContextPreparedEvent() {
        EngineContextEvent engineContextEvent = new EngineContextEvent(engineContext,
                                                                       EngineContextEvent.ENGINE_CONTEXT_PREPARED);

        eventQueue.fire(engineContextEvent);
    }

    /**
     * 加载与引擎及上下文相关的事件监听器，并添加到 EventListenerManager 中
     */
    private void registerEngineEventListeners() {
        WeldContainer container = engineContext.getContainer();
        EventListenerManager eventHandlerManager = engineContext.getEventHandlerManager();

        loadEngineContextEventListener(container).forEach(eventHandlerManager::addEventListener);
    }

    private Stream<EngineContextListener> loadEngineContextEventListener(WeldContainer container) {
        return container.select(EngineContextListener.class, Any.Literal.INSTANCE)
                        .handlersStream()
                        .map(WeldInstance.Handler::get);
    }

    /**
     * 发送 EngineContextEvent.ENGINE_CONTEXT_LOADED 事件
     */
    private void postEngineContextLoadedEvent() {
        EngineContextEvent engineContextEvent = new EngineContextEvent(engineContext,
                                                                       EngineContextEvent.ENGINE_CONTEXT_LOADED);

        eventQueue.fire(engineContextEvent);
    }

    /**
     * 每次更新调用所有更新脚本
     */
    protected void update(float interval) {
        for (EngineComponent engineComponent : getEngineComponents()) {
            engineComponent.update(engineContext);
        }

        timer.update();
        window.setResized(false);
    }

    /**
     * 获取引擎中的所有组件，执行 update 生命周期
     *
     * @return 引擎中的所有组件
     */
    private Collection<EngineComponent> getEngineComponents() {
        return engineContext.getComponents(EngineComponent.class);
    }

    private void setEngineComponents(Collection<? extends EngineComponent> engineComponents) {
        this.engineComponents = new ArrayList<>(engineComponents);
    }

    private void render() {
        gpuTimer.startTimer();

        applyRendererComponent();

        gpuTimer.endTimer();
    }

    /**
     * 将渲染委托给 Renderer Manager
     *
     * @see RendererComponent
     * @see RendererManager
     */
    private void applyRendererComponent() {
        if (rendererManager == null) {
            rendererManager = engineContext.getComponent(RendererManager.class);
        }
        rendererManager.render(window, engineContext);
    }

    private void loop(Window window) {
        float interval = 1f / ups;
        timer.init(interval);
        engineContext.setRunning(true);
        while (running) {
            timer.tick();

            preProcessFrame();
            while (timer.needUpdate()) {
                if (window.isCloseRequested()) {
                    shutDownEngine();
                }

                update(interval);
            }

            render();
            postProcessFrame();
        }
    }

    private void shutDownEngine() {
        if (!running) {
            return;
        }
        running = false;
        engineContext.setRunning(false);
    }

    private void preProcessFrame() {
        cpuTimer.startTimer();
    }

    private void postProcessFrame() {
        cpuTimer.endTimer();

        DebugInfo debugInfo = engineContext.getDebugInfo();
        debugInfo.addGameDebugInfo("gpu.time", String.format("Gpu time %.3fms", gpuTimer.getAverageTime() / 1000000.d));
        debugInfo.addGameDebugInfo("cpu.time", String.format("Cpu time %.3fms", cpuTimer.getAverageTime() / 1000000.d));

        window.swapBuffers();
    }

    private void beforeLoop() {
        running = true;
    }

    @Override
    public void run() {
        try {
            log.info("{} thread is running.", Thread.currentThread().getName());
            WindowManager.bindWindowContext(window);
            initialize();
            start(window, engineContext);
            beforeLoop();
            log.info("Engine core start game loop.");
            loop(window);
        } catch (Exception e) {
            // 捕捉所有运行时的错误
            errCallback(e);
        } finally {
            cleanup();
            window.cleanupContext();
            log.info("Engine Core quit.");
        }
    }

    protected void errCallback(Exception e) {
        log.error("The per.fkrobin.engine encountered a fatal error!\n", e);
    }

    protected void cleanup() {
        log.debug("٩(๑❛ᴗ❛๑)۶ ==> Release all necessary resources and memory.");

        engineContext.cleanup();

        Utils.cleanUp();
    }

    /**
     * 从 Weld 容器中加载组件，为了管理所有的组件，因此使用 {@link Any}
     * 限定符
     *
     * <p>
     * 这里指的组件不一定是 EngineComponent，可能是各种监听器等预定义功能部件
     * </p>
     *
     * @param componentType 组件类型
     * @return {@link Collection}<{@link T}>
     */
    private <T> Collection<T> loadComponent(Class<T> componentType) {
        return engineContext.getContainer()
                            .select(componentType, Any.Literal.INSTANCE)
                            .handlersStream()
                            .map(WeldInstance.Handler::get)
                            .collect(Collectors.toList());
    }
}
