package pers.crobin.engine.kernel;

import pers.crobin.engine.event.EventManager;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @Date 2020/5/9 22:05
 **/
public class EngineThread {
    private static final ThreadLocal<Window>       WINDOW        = new ThreadLocal<>();
    private static final ThreadLocal<EventManager> EVENT_MANAGER = new ThreadLocal<>();
    private static final ThreadLocal<DebugInfo>    DEBUG_INFO    = new ThreadLocal<>();
    private static final ThreadLocal<Linker>       LINKER        = new ThreadLocal<>();

    public static Window getThreadWindow() {
        return WINDOW.get();
    }

    public static void setThreadWindow(Window window) {
        EngineThread.WINDOW.set(window);
    }

    public static EventManager getEventManager() {
        return EVENT_MANAGER.get();
    }

    public static void setEventManager(EventManager eventManager) {
        EngineThread.EVENT_MANAGER.set(eventManager);
    }

    public static DebugInfo getDebugInfo() {
        return DEBUG_INFO.get();
    }

    public static void setDebugInfo(DebugInfo debugInfo) {
        DEBUG_INFO.set(debugInfo);
    }

    public static Linker getLinker() {
        return LINKER.get();
    }

    public static void setLinker(Linker linker) {
        LINKER.set(linker);
    }

    public static void remove() {
        WINDOW.remove();
        EVENT_MANAGER.remove();
        DEBUG_INFO.remove();
        LINKER.remove();
    }
}
