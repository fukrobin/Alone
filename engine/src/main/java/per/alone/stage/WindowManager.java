package per.alone.stage;

import org.lwjgl.system.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/9 10:16
 **/
public class WindowManager {
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

    private static final List<Window> WINDOWS = new LinkedList<>();

    private static final List<Window> WINDOWS_TO_REMOVE = new LinkedList<>();

    private static long mainThread = -1;

    public static void init() {
        if (mainThread > -1) {
            return;
        }

        mainThread = Thread.currentThread().getId();
    }

    public static Window createWindow(int width, int height, String title, boolean vsync) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
//        glfwWindowHint(GLFW_SAMPLES, 4);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        Window window = new Window(width, height, title, vsync);
        window.alignCenter();
        window.setWindowAndFrameBufferSize();

        WINDOWS.add(window);

        return window;
    }

    /**
     * 在当前线程中绑定窗口上下文，window 只能从绑定线程中访问
     *
     * @param window 窗口
     */
    public static void bindWindowContext(Window window) {
        window.setContext();

        window.setCreated(true);
    }

    public static boolean isEmpty() {
        return WINDOWS.isEmpty();
    }

    public static void update() {
        while (!TASKS.isEmpty()) {
            TASKS.poll().run();
        }
        WINDOWS_TO_REMOVE.clear();
        WINDOWS.forEach(window -> {
            if (window.isDestroy()) {
                window.shutdown();
                WINDOWS_TO_REMOVE.add(window);
            }
        });

        // glfwPollEvents();
        WINDOWS_TO_REMOVE.forEach(WINDOWS::remove);
    }

    /**
     * 异步提交一个任务，任务将在稍后有空时运行
     *
     * @param runnable Runnable
     */
    public static void submit(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (Thread.currentThread().getId() == mainThread) {
            runnable.run();
        } else {
            TASKS.add(runnable);
        }
    }
}
