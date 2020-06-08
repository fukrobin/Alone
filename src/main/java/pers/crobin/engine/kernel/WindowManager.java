package pers.crobin.engine.kernel;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @Date 2020/5/9 10:16
 **/
public
class WindowManager {
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

    private static List<Window> windows  = new LinkedList<>();
    private static List<Window> toRemove = new LinkedList<>();

    private static long mainThread = -1;

    public static void init() {
        if (mainThread > -1) {
            return;
        }

        mainThread = Thread.currentThread().getId();
    }

    public static Window generateWindow(int width, int height, String title, boolean vsync) {
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

        long windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        Window      window  = new Window(windowId, width, height, title, vsync);
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowId, (vidMode.width() - window.width) / 2, (vidMode.height() - window.height) / 2);
        glfwSetWindowSizeLimits(windowId, 854, 480, GLFW_DONT_CARE, GLFW_DONT_CARE);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetFramebufferSize(windowId, w, h);
            window.frameBufferWidth  = w.get(0);
            window.frameBufferHeight = h.get(0);

            glfwGetWindowSize(windowId, w, h);
            window.width      = w.get(0);
            window.height     = h.get(0);
            window.pixelRadio = (float) window.frameBufferWidth / window.width;
        }
        windows.add(window);

        return window;
    }

    public static void createWindow(Window window) {
        window.setContext();

        window.created = true;
    }

    public static boolean isEmpty() {
        return windows.isEmpty();
    }

    public static void update() {
        while (!TASKS.isEmpty()) {
            TASKS.poll().run();
        }
        toRemove.clear();
        windows.forEach(window -> {
            if (window.isDestroy()) {
                window.shutdown();
                toRemove.add(window);
            }
        });

//        glfwPollEvents();
        toRemove.forEach(window -> windows.remove(window));
    }

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
