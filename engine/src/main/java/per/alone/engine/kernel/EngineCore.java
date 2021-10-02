package per.alone.engine.kernel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import per.alone.engine.context.EngineContext;
import per.alone.engine.context.EngineContextEvent;
import per.alone.engine.context.EngineContextListener;
import per.alone.engine.renderer.CompositeRenderer;
import per.alone.engine.renderer.RendererComponent;
import per.alone.engine.util.CpuTimer;
import per.alone.engine.util.GPUTimer;
import per.alone.engine.util.Timer;
import per.alone.engine.util.Utils;
import per.alone.stage.Window;
import per.alone.stage.WindowManager;

import java.util.*;

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

    private List<EngineComponent> engineComponents;

    private List<RendererComponent> rendererComponents;

    private List<EngineContextListener<?>> engineContextListeners;

    private EngineContext engineContext;

    private boolean running;

    private int fps = 180;

    private int ups = 30;

    private CompositeRenderer compositeRenderer = new CompositeRenderer();

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
        postPreparedEngineContextEvent();
    }

    /**
     * 发送引起上下文以准备好的事件到所有的 EngineComponent
     */
    private void postPreparedEngineContextEvent() {
        EngineContextEvent engineContextEvent = new EngineContextEvent(engineContext,
                                                                       EngineContextEvent.PREPARED_ENGINE_CONTEXT);
        for (EngineContextListener<? extends EngineContextEvent> listener : getEngineContextListeners()) {
            listener.onEngineContextEvent(engineContextEvent);
        }
    }

    @SuppressWarnings("unchecked")
    protected EngineCore(Window window, boolean vsync) {
        super(EngineCore.class.getSimpleName());
        this.timer    = new Timer();
        this.cpuTimer = new CpuTimer();
        this.gpuTimer = new GPUTimer();

        setEngineComponents(loadComponent(EngineComponent.class));
        setRendererComponents(loadComponent(RendererComponent.class));
        setEngineContextListeners(loadComponent(EngineContextListener.class));

        this.window = window;
    }

    private List<EngineContextListener<? extends EngineContextEvent>> getEngineContextListeners() {
        return engineContextListeners;
    }

    /**
     * 每次更新调用所有更新脚本
     */
    protected void update(float interval) {
        for (EngineComponent engineComponent : engineComponents) {
            engineComponent.update(engineContext);
        }

        window.setResized(false);
    }

    private <T> void setEngineContextListeners(Collection<? extends EngineContextListener<?>> engineContextListeners) {
        this.engineContextListeners = new ArrayList<>(engineContextListeners);
    }

    private void render() {
        gpuTimer.startTimer();

        applyRendererComponent();

        gpuTimer.endTimer();
    }

    /**
     * 调用所有的渲染组件的 render() 方法进行渲染
     *
     * @see RendererComponent
     */
    private void applyRendererComponent() {
        for (RendererComponent rendererComponent : getRendererComponents()) {
            rendererComponent.render(window, engineContext);
        }
    }

    private List<RendererComponent> getRendererComponents() {
        return rendererComponents;
    }

    private void setRendererComponents(Collection<? extends RendererComponent> rendererComponents) {
        this.rendererComponents = new ArrayList<>(rendererComponents);
    }

    private void loop(Window window) {
        float interval = 1f / ups;
        timer.init(interval);
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

    public List<EngineComponent> getEngineComponents() {
        return Collections.unmodifiableList(engineComponents);
    }

    private void setEngineComponents(Collection<? extends EngineComponent> engineComponents) {
        this.engineComponents = new ArrayList<>(engineComponents);
    }

    public void addEngineComponents(EngineComponent... engineComponents) {
        this.engineComponents.addAll(Arrays.asList(engineComponents));
    }

    public void addEngineComponents(List<EngineComponent> engineComponents) {
        this.engineComponents.addAll(engineComponents);
    }

    public void addRendererComponents(RendererComponent... rendererComponents) {
        this.rendererComponents.addAll(Arrays.asList(rendererComponents));
    }

    public void addRendererComponents(List<RendererComponent> rendererComponents) {
        this.rendererComponents.addAll(rendererComponents);
    }

    public void addEngineContextListeners(
            EngineContextListener<? extends EngineContextEvent>... engineContextListeners) {
        this.engineContextListeners.addAll(Arrays.asList(engineContextListeners));
    }

    public void addEngineContextListeners(
            List<EngineContextListener<? extends EngineContextEvent>> engineContextListeners) {
        this.engineContextListeners.addAll(engineContextListeners);
    }

    @SuppressWarnings("unchecked")
    private <T> Collection<T> loadComponent(Class<T> componentType) {
        // TODO: 2021/9/29 load component from config file or annotation
        return Arrays.asList((T) compositeRenderer);
    }
}
