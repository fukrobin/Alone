package per.alone.stage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import per.alone.event.EventQueue;
import per.alone.event.EventType;
import per.alone.stage.input.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author fkobin
 * @date 2020/4/4 19:30
 **/
@SuppressWarnings("unused")
@Getter
@Setter
@Slf4j
public class Window {
    public static final int MIN_WIDTH = 854;

    public static final int MIN_HEIGHT = 580;

    /**
     * 窗口id
     */
    private final long windowId;

    //////////////////////////////
    ///// 状态值
    //////////////////////////////

    /**
     * 窗口宽度
     */
    private int width;

    /**
     * 窗口高度
     */
    private int height;

    /**
     * 帧缓冲宽度
     */
    private int frameBufferWidth;

    /**
     * 帧缓冲高度
     */
    private int frameBufferHeight;

    /**
     * Window 在屏幕上的 x 轴位置
     */
    private double xPos;

    /**
     * Window 在屏幕上的 y 轴位置
     */
    private double yPos;

    private final GLFWWindowPosCallbackI defaultWindowPosCallback = (window, xPos, yPos) -> {
        this.xPos = xPos;
        this.yPos = yPos;
    };

    /**
     * 像素比例
     */
    private float pixelRadio = 1;

    /**
     * 是否开启垂直同步
     */
    private boolean vsync;

    /**
     * 窗口尺寸是否改变（上一次更新到此次）
     */
    private boolean resized = true;

    private final GLFWWindowSizeCallbackI defaultWindowSizeCallback = (window, width, height) -> {
        if (width == 0 || height == 0 || this.frameBufferWidth == 0 || this.frameBufferHeight == 0) {
            return;
        }
        this.pixelRadio = this.frameBufferWidth <= width ? 1 : (float) this.frameBufferWidth / width;
        this.width      = width;
        this.height     = height;
        this.resized    = true;
    };

    private final GLFWFramebufferSizeCallbackI defaultFramebufferSizeCallback = (window, width, height) -> {
        if (width == 0 || height == 0 || this.width == 0 || this.height == 0) {
            return;
        }

        this.pixelRadio        = width <= this.width ? 1 : (float) width / this.width;
        this.frameBufferHeight = height;
        this.frameBufferWidth  = width;
        this.resized           = true;
    };

    /**
     * 窗口是否可见
     */
    private boolean visible = false;

    /**
     * 窗口是否最小化
     */
    private boolean iconified = false;

    private final GLFWWindowIconifyCallbackI defaultWindowIconifyCallback = (window, iconified) -> {
        this.iconified = iconified;
        this.resized   = true;
    };

    /**
     * 窗口是否聚焦
     */
    private boolean focused;

    private final GLFWWindowFocusCallbackI defaultWindowFocusCallback = (window, focused) -> this.focused = focused;

    /**
     * 窗口是否最大化
     */
    private boolean maximized;

    private final GLFWWindowMaximizeCallbackI defaultWindowMaximizeCallback = (window, maximized) -> {
        this.maximized = maximized;
        this.resized   = true;
    };

    /**
     * 窗口是否全屏
     */
    private boolean fullScreen;

    /**
     * 窗口是否已正常创建
     */
    private boolean created = false;

    /**
     * 窗口是否销毁
     */
    private boolean destroy = false;

    /**
     * 窗口标题
     */
    private String title;

    private org.lwjgl.system.Callback debugMsgCallback;

    /**
     * 窗口是否是调试模式
     */
    private boolean debugMode;

    private boolean hiddenCursor;

    private final GLFWKeyCallbackI defaultKeyCallback = (window, key, scancode, action, mods) -> {
        if (action == GLFW_RELEASE && key == GLFW_KEY_F12) {
            debugMode = !debugMode;
        }

        if (key == GLFW_KEY_ESCAPE && !hiddenCursor) {
            resetCursor();
        }
    };

    private boolean inWindow;

    private final GLFWCursorEnterCallbackI defaultCursorEnterCallback = (window, entered) -> inWindow = entered;

    private EventQueue eventQueue;

    private int fps;

    private final GLFWWindowCloseCallbackI defaultWindowCloseCallback = window -> close();

    public Window(int width, int height, String title) {
        this(width, height, title, true);
    }

    public Window(int width, int height, String title, boolean vsync) {
        long windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        this.windowId = windowId;
        this.width    = Math.max(width, Window.MIN_WIDTH);
        this.height   = Math.max(height, Window.MIN_HEIGHT);
        this.title    = title;
        this.vsync    = vsync;
        this.fps      = 180;

        // setWindow();
    }

    /**
     * 设置 Window 居中对齐
     */
    public void alignCenter() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            glfwSetWindowPos(windowId,
                             (vidMode.width() - getWidth()) / 2,
                             (vidMode.height() - getHeight()) / 2);
        }
    }

    //////////////////////////////
    ///// 窗口参数-杂项
    //////////////////////////////

    /**
     * 设置 Window 的 最小宽、高，帧缓冲宽、高，并设置像素比
     */
    void setWindowAndFrameBufferSize() {
        glfwSetWindowSizeLimits(windowId, 854, 480, GLFW_DONT_CARE, GLFW_DONT_CARE);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetFramebufferSize(windowId, w, h);
            setFrameBufferWidth(w.get(0));
            setFrameBufferHeight(h.get(0));

            glfwGetWindowSize(windowId, w, h);
            setWidth(w.get(0));
            setHeight(h.get(0));

            setPixelRadio((float) getFrameBufferWidth() / getWidth());
        }
    }

    public void resetViewport() {
        GL11.glViewport(0, 0, (int) (width * pixelRadio), (int) (height * pixelRadio));
    }

    public void setViewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    public void setSize(int width, int height) {
        WindowManager.submit(() -> glfwSetWindowSize(windowId, width, height));
    }

    /**
     * 隐藏鼠标，但不限制鼠标的移动
     */
    public void hiddenCursor() {
        // TODO: 2021/9/17 将鼠标隐藏作为状态值存到 MouseEvent
        WindowManager.submit(() -> {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            hiddenCursor = true;
        });
    }

    /**
     * 禁用鼠标，此方法将会隐藏鼠标并且禁止鼠标移动，但会提供一个虚拟的鼠标位置以供使用，适合于游戏相机移动。
     */
    public void disableCursor() {
        WindowManager.submit(() -> {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            hiddenCursor = true;
        });
    }

    /**
     * 重设鼠标状态为正常：鼠标可见并随意移动
     */
    public void resetCursor() {
        WindowManager.submit(() -> {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            hiddenCursor = true;
        });
    }

    //////////////////////////////
    ///// Destroy Window
    //////////////////////////////

    public void setTitle(String title) {
        WindowManager.submit(() -> {
            this.title = title;
            glfwSetWindowTitle(windowId, title);
        });
    }

    public void setCenter() {
        WindowManager.submit(() -> {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode != null) {
                glfwSetWindowPos(windowId, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
            }
        });
    }

    //////////////////////////////
    ///// GLFW事件部分
    //////////////////////////////

    public void setVerticalSync(boolean vsync) {
        if (this.vsync != vsync) {
            glfwSwapInterval(vsync ? 1 : 0);
        }

        this.vsync = vsync;
    }

    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            WindowManager.submit(() -> {
                if (visible) {
                    glfwShowWindow(windowId);
                } else {
                    glfwHideWindow(windowId);
                }
                this.visible = visible;
            });
        }
    }

    public float aspect() {
        return (float) width / (float) height;
    }

    public void show() {
        setVisible(true);
        focus();
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowId);
    }

    public void focus() {
        WindowManager.submit(() -> glfwFocusWindow(windowId));
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
        // TODO: 2021/9/21 限制 fps，考虑使用比 ClientSync 更节省 CPU 的方法
    }

    public void close() {
        glfwSetWindowShouldClose(windowId, true);
    }

    public void shutdown() {
        if (!created) {
            return;
        }

        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
        created = false;
    }

    public void cleanupContext() {
        if (debugMsgCallback != null) {
            debugMsgCallback.free();
        }
        glfwMakeContextCurrent(NULL);
        GL.setCapabilities(null);
        this.destroy = true;
    }

    public void addWindowCloseCallback(GLFWWindowCloseCallbackI callbackI) {
        WindowCloseCallback.INSTANCE.addCallback(callbackI);
    }

    public void addWindowSizeCallback(GLFWWindowSizeCallbackI callbackI) {
        WindowSizeCallback.INSTANCE.addCallback(callbackI);
    }

    public void addFrameBufferSizeCallback(GLFWFramebufferSizeCallbackI callbackI) {
        FramebufferSizeCallback.INSTANCE.addCallback(callbackI);
    }

    public void addWindowFocusCallback(GLFWWindowFocusCallbackI callbackI) {
        WindowFocusCallback.INSTANCE.addCallback(callbackI);
    }

    public void addWindowIconifyCallback(GLFWWindowIconifyCallbackI callbackI) {
        WindowIconifyCallback.INSTANCE.addCallback(callbackI);
    }

    public void addWindowMaximizeCallback(GLFWWindowMaximizeCallbackI callbackI) {
        WindowMaximizeCallback.INSTANCE.addCallback(callbackI);
    }

    public void addKeyCallback(GLFWKeyCallbackI callbackI) {
        KeyCallback.INSTANCE.addCallback(callbackI);
    }

    public void addMouseButtonCallback(GLFWMouseButtonCallbackI callbackI) {
        MouseButtonCallback.INSTANCE.addCallback(callbackI);
    }

    public void addCursorPosCallback(GLFWCursorPosCallbackI callbackI) {
        CursorPosCallback.INSTANCE.addCallback(callbackI);
    }

    public void addCharCallback(GLFWCharCallbackI callbackI) {
        CharCallback.INSTANCE.addCallback(callbackI);
    }

    public void addCursorEnterCallback(GLFWCursorEnterCallbackI callbackI) {
        CursorEnterCallback.INSTANCE.addCallback(callbackI);
    }

    private void setWindowIcon() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageData = STBImage.stbi_load("runtime/texture/windows_icon.png", w, h, channels, 4);
            if (imageData != null) {
                GLFWImage images = GLFWImage.mallocStack(stack);
                images.set(w.get(0), h.get(0), imageData);

                GLFWImage.Buffer buffer = GLFWImage.mallocStack(1, stack);
                buffer.put(0, images);

                glfwSetWindowIcon(windowId, buffer);
                STBImage.stbi_image_free(imageData);
            }
        }
    }

    private void setCursorIcon() {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer w        = stack.mallocInt(1);
//            IntBuffer h        = stack.mallocInt(1);
//            IntBuffer channels = stack.mallocInt(1);
//
//            ByteBuffer imageData = STBImage.stbi_load("runtime/texture/cursor.png", w, h, channels, 4);
//            if (imageData != null) {
//                GLFWImage images = GLFWImage.mallocStack(stack);
//                images.set(w.get(0), h.get(0), imageData);
//
//                long glfwCursor = glfwCreateCursor(images, 0, 0);
//                glfwSetCursor(windowId, glfwCursor);
//                STBImage.stbi_image_free(imageData);
//            }
//        }
    }

    /**
     * 初始化GLFW
     */
    private void setWindow() {
        addDefaultCallBacks();
        setWindowIcon();
        setCursorIcon();
    }

    /**
     * 设置默认的 GLFW 事件回调
     */
    private void addDefaultCallBacks() {
        glfwSetWindowSizeCallback(windowId, WindowSizeCallback.INSTANCE);
        glfwSetFramebufferSizeCallback(windowId, FramebufferSizeCallback.INSTANCE);
        glfwSetWindowFocusCallback(windowId, WindowFocusCallback.INSTANCE);
        glfwSetWindowIconifyCallback(windowId, WindowIconifyCallback.INSTANCE);
        glfwSetWindowMaximizeCallback(windowId, WindowMaximizeCallback.INSTANCE);
        glfwSetKeyCallback(windowId, KeyCallback.INSTANCE);
        glfwSetMouseButtonCallback(windowId, MouseButtonCallback.INSTANCE);
        glfwSetCursorPosCallback(windowId, CursorPosCallback.INSTANCE);
        glfwSetCursorEnterCallback(windowId, CursorEnterCallback.INSTANCE);
        glfwSetCharCallback(windowId, CharCallback.INSTANCE);

        addWindowSizeCallback(defaultWindowSizeCallback);
        addFrameBufferSizeCallback(defaultFramebufferSizeCallback);
        addWindowFocusCallback(defaultWindowFocusCallback);
        addWindowIconifyCallback(defaultWindowIconifyCallback);
        addWindowMaximizeCallback(defaultWindowMaximizeCallback);
        addKeyCallback(defaultKeyCallback);
        addCursorEnterCallback(defaultCursorEnterCallback);
        addWindowCloseCallback(defaultWindowCloseCallback);
    }

    private void addWindowEventProducerCallback() {
        if (eventQueue == null) {
            return;
        }
        addWindowSizeCallback((window, width, height) -> eventQueue.postEvent(
                buildWindowEvent(WindowEvent.WINDOW_SIZE_CHANGE)));

        addFrameBufferSizeCallback((window, width, height) -> eventQueue.postEvent(
                buildWindowEvent(WindowEvent.WINDOW_FRAMEBUFFER_SIZE_CHANGE)));

        addWindowFocusCallback((window, focused) -> eventQueue.postEvent(
                buildWindowEvent(WindowEvent.WINDOW_FOCUS)));

        addWindowIconifyCallback((window, iconified) -> eventQueue.postEvent(
                buildWindowEvent(WindowEvent.WINDOW_ICONIFY)));

        addWindowMaximizeCallback((window, maximized) -> eventQueue.postEvent(
                buildWindowEvent(WindowEvent.WINDOW_MAXIMIZE)));
    }

    private void addKeyEventProducesCallback() {
        addKeyCallback((window, key, scancode, action, mods) -> eventQueue.postEvent(buildKeyEvent(key, action, mods)));
        addCharCallback((window, codepoint) -> eventQueue.postEvent(buildCharEvent(codepoint)));
    }

    private void addMouseEventProducerCallback() {
        MouseData mouseData = new MouseData();
        addMouseButtonCallback((window, button, action, mods) -> {
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    mouseData.setLeftButtonDown(action == GLFW_PRESS);
                    break;
                case GLFW_MOUSE_BUTTON_MIDDLE:
                    mouseData.setMiddleButtonDown(action == GLFW_PRESS);
                    break;
                case GLFW_MOUSE_BUTTON_RIGHT:
                    mouseData.setRightButtonDown(action == GLFW_PRESS);
                    break;
            }
            mouseData.setButton(MouseButton.fromCode(button));
            mouseData.setShiftDown(isShiftDown(mods));
            mouseData.setControlDown(isCtrlDown(mods));
            mouseData.setAltDown(isAltDown(mods));
            eventQueue.postEvent(
                    buildMouseEvent(action == GLFW_PRESS ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED,
                                    mouseData));
        });
        addCursorPosCallback((window, xPos, yPos) -> {
            mouseData.setX(xPos);
            mouseData.setY(yPos);
            eventQueue.postEvent(buildMouseEvent(MouseEvent.MOUSE_MOVED, mouseData));
        });
    }

    public void setContext() {
        glfwMakeContextCurrent(windowId);
        GLCapabilities caps = GL.createCapabilities();
        debugMsgCallback = GLUtil.setupDebugMessageCallback();

        if (caps.OpenGL43) {
            GL43.glDebugMessageControl(
                    GL43.GL_DEBUG_SOURCE_API,
                    GL43.GL_DEBUG_TYPE_OTHER,
                    GL43.GL_DEBUG_SEVERITY_NOTIFICATION,
                    (IntBuffer) null,
                    false);
        } else if (caps.GL_KHR_debug) {
            KHRDebug.glDebugMessageControl(
                    KHRDebug.GL_DEBUG_SOURCE_API,
                    KHRDebug.GL_DEBUG_TYPE_OTHER,
                    KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
                    (IntBuffer) null,
                    false);
        } else if (caps.GL_ARB_debug_output) {
            glDebugMessageControlARB(
                    GL_DEBUG_SOURCE_API_ARB,
                    GL_DEBUG_TYPE_OTHER_ARB,
                    GL_DEBUG_SEVERITY_LOW_ARB,
                    (IntBuffer) null,
                    false);
        }

        glfwSwapInterval(vsync ? 1 : 0);
    }

    private WindowEvent buildWindowEvent(EventType<? super WindowEvent> eventType) {
        WindowData source = new WindowData(
                width, height, frameBufferWidth, frameBufferHeight, pixelRadio, vsync, resized, visible,
                iconified, focused, maximized, fullScreen, title, debugMode, hiddenCursor
        );
        return new WindowEvent(source, eventType);
    }

    private KeyEvent buildKeyEvent(int key, int action, int mods) {
        EventType<KeyEvent> eventType = action == GLFW_PRESS ? KeyEvent.KEY_PRESSED : (
                action == GLFW_RELEASE ? KeyEvent.KEY_RELEASED : KeyEvent.KEY_REPEAT
        );
        return new KeyEvent(eventType, KeyCode.fromCode(key), isShiftDown(action), isCtrlDown(mods), isAltDown(mods));
    }

    private CharInputEvent buildCharEvent(int codepoint) {
        return new CharInputEvent(CharInputEvent.ANY, codepoint);
    }

    private MouseEvent buildMouseEvent(EventType<? extends MouseEvent> event, MouseData data) {
        return new MouseEvent(event,
                              data.x, data.y,
                              data.x + this.xPos, data.y + this.yPos,
                              data.button,
                              data.shiftDown, data.controlDown, data.altDown,
                              data.leftButtonDown, data.middleButtonDown, data.rightButtonDown, hiddenCursor, inWindow);
    }

    private boolean isShiftDown(int mods) {
        return (mods & GLFW_MOD_SHIFT) > 0;
    }

    private boolean isCtrlDown(int mods) {
        return (mods & GLFW_MOD_CONTROL) > 0;
    }

    private boolean isAltDown(int mods) {
        return (mods & GLFW_MOD_ALT) > 0;
    }

    private static abstract class Callback<T> {

        protected List<T> callbacks = new LinkedList<>();

        public void addCallback(T callback) {
            if (callback != null) {
                callbacks.add(callback);
            }
        }

        public void removeCallback(T callback) {
            if (callback != null) {
                callbacks.remove(callback);
            }
        }
    }

    private static class WindowCloseCallback extends Callback<GLFWWindowCloseCallbackI> implements GLFWWindowCloseCallbackI {

        static final WindowCloseCallback INSTANCE = new WindowCloseCallback();

        @Override
        public void invoke(long window) {
            callbacks.forEach(callbackI -> callbackI.invoke(window));
        }
    }

    private static class WindowSizeCallback extends Callback<GLFWWindowSizeCallbackI> implements GLFWWindowSizeCallbackI {
        static final WindowSizeCallback INSTANCE = new WindowSizeCallback();

        @Override
        public void invoke(long window, int width, int height) {
            callbacks.forEach(callbackI -> callbackI.invoke(window, width, height));
        }
    }

    private static class WindowFocusCallback extends Callback<GLFWWindowFocusCallbackI> implements GLFWWindowFocusCallbackI {

        public static final WindowFocusCallback INSTANCE = new WindowFocusCallback();

        @Override
        public void invoke(long window, boolean focused) {
            for (GLFWWindowFocusCallbackI callback : callbacks) {
                callback.invoke(window, focused);
            }
        }
    }

    private static class KeyCallback extends Callback<GLFWKeyCallbackI> implements GLFWKeyCallbackI {

        public static final KeyCallback INSTANCE = new KeyCallback();

        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            for (GLFWKeyCallbackI callback : callbacks) {
                callback.invoke(window, key, scancode, action, mods);
            }
        }
    }

    private static class CharCallback extends Callback<GLFWCharCallbackI> implements GLFWCharCallbackI {

        public static final CharCallback INSTANCE = new CharCallback();

        @Override
        public void invoke(long window, int codepoint) {
            for (GLFWCharCallbackI callback : callbacks) {
                callback.invoke(window, codepoint);
            }
        }
    }

    private static class MouseButtonCallback extends Callback<GLFWMouseButtonCallbackI> implements GLFWMouseButtonCallbackI {
        public static final MouseButtonCallback INSTANCE = new MouseButtonCallback();

        @Override
        public void invoke(long window, int button, int action, int mods) {
            for (GLFWMouseButtonCallbackI callback : callbacks) {
                callback.invoke(window, button, action, mods);
            }
        }
    }

    private static class CursorPosCallback extends Callback<GLFWCursorPosCallbackI> implements GLFWCursorPosCallbackI {
        public static final CursorPosCallback INSTANCE = new CursorPosCallback();

        @Override
        public void invoke(long window, double xPos, double yPos) {
            for (GLFWCursorPosCallbackI callback : callbacks) {
                callback.invoke(window, xPos, yPos);
            }
        }
    }

    private static class ScrollCallback extends Callback<GLFWScrollCallbackI> implements GLFWScrollCallbackI {
        public static final ScrollCallback INSTANCE = new ScrollCallback();

        @Override
        public void invoke(long window, double xOffset, double yOffset) {
            for (GLFWScrollCallbackI callback : callbacks) {
                callback.invoke(window, xOffset, yOffset);
            }
        }
    }

    private static class WindowIconifyCallback extends Callback<GLFWWindowIconifyCallbackI> implements GLFWWindowIconifyCallbackI {
        public static final WindowIconifyCallback INSTANCE = new WindowIconifyCallback();

        @Override
        public void invoke(long window, boolean iconified) {
            for (GLFWWindowIconifyCallbackI callback : callbacks) {
                callback.invoke(window, iconified);
            }
        }
    }

    private static class FramebufferSizeCallback extends Callback<GLFWFramebufferSizeCallbackI> implements GLFWFramebufferSizeCallbackI {
        public static final FramebufferSizeCallback INSTANCE = new FramebufferSizeCallback();

        @Override
        public void invoke(long window, int width, int height) {
            for (GLFWFramebufferSizeCallbackI callback : callbacks) {
                callback.invoke(window, width, height);
            }
        }
    }

    private static class CursorEnterCallback extends Callback<GLFWCursorEnterCallbackI> implements GLFWCursorEnterCallbackI {
        public static final CursorEnterCallback INSTANCE = new CursorEnterCallback();

        @Override
        public void invoke(long window, boolean entered) {
            for (GLFWCursorEnterCallbackI callback : callbacks) {
                callback.invoke(window, entered);
            }
        }
    }

    private static class CharModsCallback extends Callback<GLFWCharModsCallbackI> implements GLFWCharModsCallbackI {
        public static final CharModsCallback INSTANCE = new CharModsCallback();

        @Override
        public void invoke(long window, int codepoint, int mods) {
            for (GLFWCharModsCallbackI callback : callbacks) {
                callback.invoke(window, codepoint, mods);
            }
        }
    }

    private static class WindowPosCallback extends Callback<GLFWWindowPosCallbackI> implements GLFWWindowPosCallbackI {
        public static final WindowPosCallback INSTANCE = new WindowPosCallback();

        @Override
        public void invoke(long window, int xPos, int yPos) {
            for (GLFWWindowPosCallbackI callback : callbacks) {
                callback.invoke(window, xPos, yPos);
            }
        }
    }

    private static class WindowMaximizeCallback extends Callback<GLFWWindowMaximizeCallbackI> implements GLFWWindowMaximizeCallbackI {
        public static final WindowMaximizeCallback INSTANCE = new WindowMaximizeCallback();

        @Override
        public void invoke(long window, boolean maximized) {
            for (GLFWWindowMaximizeCallbackI callback : callbacks) {
                callback.invoke(window, maximized);
            }
        }
    }

    private static class WindowRefreshCallback extends Callback<GLFWWindowRefreshCallbackI> implements GLFWWindowRefreshCallbackI {
        public static final WindowRefreshCallback INSTANCE = new WindowRefreshCallback();

        @Override
        public void invoke(long window) {
            for (GLFWWindowRefreshCallbackI callback : callbacks) {
                callback.invoke(window);
            }
        }
    }

    @Data
    private static class MouseData {
        private double x;

        private double y;

        private double screenX;

        private double screenY;

        private MouseButton button;

        private boolean shiftDown;

        private boolean controlDown;

        private boolean altDown;

        private boolean leftButtonDown;

        private boolean middleButtonDown;

        private boolean rightButtonDown;

        private boolean stillSincePress;

        private boolean hiddenCursor;

        private boolean inWindow;
    }
}
