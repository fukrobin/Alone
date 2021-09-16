package pers.crobin.engine.kernel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.crobin.engine.event.ActionEvent;
import pers.crobin.engine.event.EventManager;
import pers.crobin.engine.event.KeyEvent;
import pers.crobin.engine.event.MouseEvent;
import pers.crobin.engine.global.GlobalVariable;
import pers.crobin.engine.util.CpuTimer;
import pers.crobin.engine.util.GPUTimer;
import pers.crobin.engine.util.Timer;
import pers.crobin.engine.util.Utils;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/8 19:18
 **/
@Data
@EqualsAndHashCode(callSuper = true)
class EngineCore extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngineCore.class.getSimpleName());

    private final Timer timer;

    private final GPUTimer gpuTimer;

    private final CpuTimer cpuTimer;

    private final Window window;

    private final EngineManager engineManager;

    private boolean running = false;

    private int fps = 180;

    private int ups = 30;

    protected EngineCore(int width, int height, String title, boolean verticalSync) {
        super(EngineCore.class.getSimpleName());
        timer         = new Timer();
        cpuTimer      = new CpuTimer();
        gpuTimer      = new GPUTimer();
        engineManager = new EngineManager();
        window        = WindowManager.createWindow(width, height, title, verticalSync);
    }

    protected void start(Window window, EngineManager engineManager) {

    }

    private void setThreadStaticVar() {
        EngineThread.setThreadWindow(window);
        EngineThread.setEventManager(new EventManager());
        EngineThread.setDebugInfo(DebugInfo.getInstance());
        EngineThread.setLinker(engineManager);
    }

    private void initialize() {
        gpuTimer.initTimer();
        EventManager eventManager = EngineThread.getEventManager();
        eventManager.addEvent(window.getKeyEvent());
        eventManager.addEvent(window.getMouseEvent());
        eventManager.addEvent(window.getActionEvent());
        eventManager.register(event -> EngineThread.getDebugInfo().updateEngineDebugInfo(), KeyEvent.class);
        eventManager.register(null, MouseEvent.class);
        eventManager.register(null, ActionEvent.class);
        engineManager.init();
        Utils.submitTask(DebugInfo::getInstance);
    }

    /**
     * 每次更新调用所有更新脚本
     */
    protected void update(float interval) {
        EventManager eventManager = EngineThread.getEventManager();
        eventManager.post();
        KeyEvent keyEvent = window.getKeyEvent();
        if (keyEvent.isFired()) {
            eventManager.postEventToActiveGui(keyEvent, engineManager.getGuiManager().getActiveGui());
        }

        ActionEvent actionEvent = window.getActionEvent();
        if (actionEvent.isFired()) {
            eventManager.postEventToActiveGui(actionEvent, engineManager.getGuiManager().getActiveGui());
        }

        eventManager.update();
        window.setResized(false);
    }

    private void render() {
        gpuTimer.startTimer();

        engineManager.getRendererManager().render(window);

        gpuTimer.endTimer();
    }

    private void loop(Window window) {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / ups;

        timer.init();
        running = true;
        while (running) {
            elapsedTime                  = timer.getElapsedTime();
            accumulator += elapsedTime;
            GlobalVariable.FRAME_ELAPSED = elapsedTime;

            beforeOnceLoop();
            while (accumulator >= interval) {
                if (window.isCloseRequested()) {
                    shutDownEngine();
                }

                update(interval);

                accumulator -= interval;
            }

            render();
            endOnceLoop();
        }
    }

    private void shutDownEngine() {
        if (!running) {
            return;
        }
        running = false;
    }

    private void beforeOnceLoop() {
        cpuTimer.startTimer();
    }

    private void endOnceLoop() {
        cpuTimer.endTimer();

        DebugInfo debugInfo = EngineThread.getDebugInfo();
        debugInfo.addGameDebugInfo("gpu.time", String.format("Gpu time %.3fms", gpuTimer.getAverageTime() / 1000000.d));
        debugInfo.addGameDebugInfo("cpu.time", String.format("Cpu time %.3fms", cpuTimer.getAverageTime() / 1000000.d));
        window.updateDisplay(fps);
    }

    private void beforeLoop() {
        engineManager.getGuiManager().start();
    }

    @Override
    public void run() {
        try {
            LOGGER.info("{} thread is running.", Thread.currentThread().getName());
            WindowManager.bindWindowContext(window);
            setThreadStaticVar();
            initialize();
            start(window, engineManager);
            beforeLoop();
            LOGGER.info("Engine core start game loop.");
            loop(window);
        } catch (Exception e) {
            // 捕捉所有运行时的错误
            errCallback(e);
        } finally {
            cleanup();
            window.cleanupContext();
            EngineThread.remove();
            LOGGER.info("Engine Core quit.");
        }
    }

    protected void errCallback(Exception e) {
        LOGGER.error("The engine encountered a fatal error!\n", e);
    }

    protected void cleanup() {
        LOGGER.debug("٩(๑❛ᴗ❛๑)۶ ==> Release all necessary resources and memory.");

        engineManager.cleanup();

        Utils.cleanUp();
    }
}
