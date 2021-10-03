package per.alone.engine.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import per.alone.engine.EngineConfigLoader;
import per.alone.engine.renderer.RendererComponent;
import per.alone.engine.renderer.RendererManager;
import per.alone.engine.util.CpuTimer;
import per.alone.engine.util.GPUTimer;
import per.alone.engine.util.Timer;
import per.alone.engine.util.Utils;
import per.alone.stage.Window;
import per.alone.stage.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private List<EngineContextListener<?>> engineContextListeners;

    private EngineContext engineContext;

    private boolean running;

    private int fps = 180;

    private int ups = 30;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected EngineCore(Window window) {
        super(EngineCore.class.getSimpleName());
        this.timer    = new Timer();
        this.cpuTimer = new CpuTimer();
        this.gpuTimer = new GPUTimer();
        this.window   = window;

        setEngineContextListeners((Collection) loadComponent(EngineContextListener.class));
    }

    protected void start(Window window, EngineContext engineContext) {

    }

    private void initialize() {
        gpuTimer.initTimer();

        createEngineContext();
        prepareEngineContext();

        Utils.submitTask(DebugInfo::getInstance);
    }

    private void createEngineContext() {
        engineContext = new EngineContext();
    }

    /**
     * 准备 EngineContext，并触发 PreparedEngineContextEvent，
     * 组件可以通过监听此事件完成一些准备工作
     */
    private void prepareEngineContext() {
        engineContext.registerComponents(loadComponent(EngineComponent.class));
        engineContext.registerComponents(loadComponent(RendererComponent.class));

        postPreparedEngineContextEvent();
    }

    /**
     * 发送引起上下文以准备好的事件到所有的 EngineComponent
     */
    private void postPreparedEngineContextEvent() {
        EngineContextEvent engineContextEvent = new EngineContextEvent(engineContext,
                                                                       EngineContextEvent.PREPARED_ENGINE_CONTEXT);
        for (EngineContextListener<? extends EngineContextEvent> listener : getEngineContextListeners()) {
            if (listener instanceof RendererManager) {
                engineContext.setRendererManager((RendererManager) listener);
            }
            listener.onEngineContextEvent(engineContextEvent);
        }
    }

    private List<EngineContextListener<? extends EngineContextEvent>> getEngineContextListeners() {
        return engineContextListeners;
    }

    private void setEngineContextListeners(Collection<? extends EngineContextListener<?>> engineContextListeners) {
        this.engineContextListeners = new ArrayList<>(engineContextListeners);
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
        engineContext.getRendererManager().render(window, engineContext);
    }

    private void loop(Window window) {
        float interval = 1f / ups;
        timer.init(interval);
        engineContext.setRunning(running);
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
        engineContext.setRunning(running);
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

        engineContext.close();

        Utils.cleanUp();
    }

    private <T> Collection<T> loadComponent(Class<T> componentType) {
        // TODO: 2021/9/29 load component from config file or annotation
        return EngineConfigLoader.loadClass(componentType, null);
    }
}
