package pers.crobin.engine.kernel;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.crobin.engine.event.ActionEvent;
import pers.crobin.engine.event.Callbacks;
import pers.crobin.engine.event.KeyEvent;
import pers.crobin.engine.event.MouseEvent;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Queue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by CRobin on 2020/4/4.
 *
 * @Author CRobin
 * @Date 2020/4/4 19:30
 **/
@SuppressWarnings("unused")
public class Window {
    private static final Logger LOGGER = LoggerFactory.getLogger("Window");

    /** 窗口id */
    protected final long        windowId;
    /** 鼠标事件 */
    protected final MouseEvent  mouseEvent;
    /** 鼠标点击事件 */
    protected final ActionEvent actionEvent;
    /** 键盘事件 */
    protected final KeyEvent    keyEvent;
    /** 高精度同步实现 */
    private final   ClientSync  sync = new ClientSync();
    /** 窗口宽度 */
    protected       int         width;
    /** 窗口高度 */
    protected       int         height;
    /** 帧缓冲宽度 */
    protected       int         frameBufferWidth;
    /** 帧缓冲高度 */
    protected       int         frameBufferHeight;

    //////////////////////////////
    ///// 状态值
    //////////////////////////////
    /** 像素比例 */
    protected float    pixelRadio = 1;
    /** 是否开启垂直同步 */
    protected boolean  vSync;
    /** 窗口尺寸是否改变（上一次更新到此次） */
    protected boolean  resized    = true;
    /** 窗口是否可见 */
    protected boolean  visible    = false;
    /** 窗口是否最小化 */
    protected boolean  iconified  = false;
    /** 窗口是否聚焦 */
    protected boolean  focused;
    /** 窗口是否最大化 */
    protected boolean  maximized;
    /** 窗口是否全屏 */
    protected boolean  fullScreen;
    /** 窗口是否已正常创建 */
    protected boolean  created    = false;
    /** 窗口是否销毁 */
    protected boolean  destroy    = false;
    /** 窗口标题 */
    protected String   title;
    protected Callback debugMsgCallback;
    /** 窗口是否接受到字符输入（上一次更新到此处更新期间） */
    private   boolean  charInput;
    /** 窗口是否是调试模式 */
    private   boolean  debugMode;

    //////////////////////////////
    ///// GLFW事件回调的扩展
    //////////////////////////////
    private Callbacks.WindowSizeCallback      windowSizeCallback;
    private Callbacks.WindowFocusCallback     windowFocusCallback;
    private Callbacks.WindowIconifyCallback   windowIconifyCallback;
    private Callbacks.WindowMaximizeCallback  windowMaximizeCallback;
    private Callbacks.FramebufferSizeCallback framebufferSizeCallback;
    private Callbacks.KeyCallback             keyCallback;
    private Callbacks.MouseButtonCallback     mouseButtonCallback;
    private Callbacks.CursorPosCallback       cursorPosCallback;
    private Callbacks.CharCallback            charCallback;
    private Callbacks.CursorEnterCallback     cursorEnterCallback;

    public Window(long windowId, int width, int height, String title, boolean vSync) {
        this.windowId = windowId;
        this.width    = width;
        this.height   = height;
        this.title    = title;
        this.vSync    = vSync;

        setWindow();
        mouseEvent  = new MouseEvent();
        actionEvent = new ActionEvent();
        keyEvent    = new KeyEvent();
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

    /** 隐藏鼠标，但不限制鼠标的移动 */
    public void hiddenCursor() {
        WindowManager.submit(() -> glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN));
        mouseEvent.setHiddenCursor(true);
        actionEvent.setHiddenCursor(true);
    }

    /** 禁用鼠标，此方法将会隐藏鼠标并且禁止鼠标移动，但会提供一个虚拟的鼠标位置以供使用，适合于游戏相机移动。 */
    public void disableCursor() {
        WindowManager.submit(() -> glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED));
        mouseEvent.setHiddenCursor(true);
        actionEvent.setHiddenCursor(true);
    }

    /** 重设鼠标状态为正常：鼠标可见并随意移动 */
    public void resetCursor() {
        WindowManager.submit(() -> glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL));
        mouseEvent.setHiddenCursor(false);
        actionEvent.setHiddenCursor(false);
    }

    public boolean cursorIsVisible() {
        return mouseEvent.isHiddenCursor();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        WindowManager.submit(() -> glfwSetWindowTitle(windowId, title));
    }

    public void setCenter() {
        WindowManager.submit(() -> {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(windowId, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        });
    }

    //////////////////////////////
    ///// 状态值
    //////////////////////////////

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        if (this.vSync != vSync) {
            glfwSwapInterval(vSync ? 1 : 0);
        }

        this.vSync = vSync;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            if (visible) {
                WindowManager.submit(() -> glfwShowWindow(windowId));
            } else {
                WindowManager.submit(() -> glfwHideWindow(windowId));
            }
        }
        this.visible = visible;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public boolean isCharInput() {
        return charInput;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isFocused() {
        return focused;
    }

    //////////////////////////////
    ///// 窗口参数-杂项
    //////////////////////////////

    public float aspect() {
        return (float) width / (float) height;
    }

    public float getPixelRadio() {
        return pixelRadio;
    }

    public void show() {
        setVisible(true);
        focus();
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowId);
    }

    public void closeWindow() {
        glfwSetWindowShouldClose(windowId, true);
    }

    public void focus() {
        WindowManager.submit(() -> glfwFocusWindow(windowId));
    }


    //////////////////////////////
    ///// 引擎预设事件
    //////////////////////////////

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public ActionEvent getActionEvent() {
        return actionEvent;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    /**
     * 获取上次更新到此次更新期间所有输入的数据，可以监听输入法输入
     *
     * @return 获上次更新到此次更新期间所有字符输入的数据，以Unicode返回。
     */
    public Queue<Integer> getInputQueue() {
        return keyEvent.getInputQueue();
    }

    protected void updateDisplay(int fps) {
        glfwSwapBuffers(windowId);
        sync.sync(fps);
    }

    //////////////////////////////
    ///// Destory Window
    //////////////////////////////

    protected void shutdown() {
        if (!created) {
            return;
        }

        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
        created = false;
    }

    protected void cleanupContext() {
        if (debugMsgCallback != null) {
            debugMsgCallback.free();
        }
        glfwMakeContextCurrent(NULL);
        GL.setCapabilities(null);
        this.destroy = true;
    }

    //////////////////////////////
    ///// GLFW事件部分
    //////////////////////////////

    public Callbacks.WindowSizeCallback getWindowSizeCallback() {
        return windowSizeCallback;
    }

    public void addWindowSizeCallback(GLFWWindowSizeCallbackI callbackI) {
        windowSizeCallback.addCallback(callbackI);
    }

    public Callbacks.FramebufferSizeCallback getFramebufferSizeCallback() {
        return framebufferSizeCallback;
    }

    public void addFrameBufferSizeCallback(GLFWFramebufferSizeCallbackI callbackI) {
        framebufferSizeCallback.addCallback(callbackI);
    }

    public Callbacks.WindowFocusCallback getWindowFocusCallback() {
        return windowFocusCallback;
    }

    public void addWindowFocusCallback(GLFWWindowFocusCallbackI callbackI) {
        windowFocusCallback.addCallback(callbackI);
    }

    public Callbacks.WindowIconifyCallback getWindowIconifyCallback() {
        return windowIconifyCallback;
    }

    public void addWindowIconifyCallback(GLFWWindowIconifyCallbackI callbackI) {
        windowIconifyCallback.addCallback(callbackI);
    }

    public Callbacks.WindowMaximizeCallback getWindowMaximizeCallback() {
        return windowMaximizeCallback;
    }

    public void addWindowMaximizeCallback(GLFWWindowMaximizeCallbackI callbackI) {
        windowMaximizeCallback.addCallback(callbackI);
    }

    public Callbacks.KeyCallback getKeyCallback() {
        return keyCallback;
    }

    public void addKeyCallback(GLFWKeyCallbackI callbackI) {
        keyCallback.addCallback(callbackI);
    }

    public Callbacks.MouseButtonCallback getMouseButtonCallback() {
        return mouseButtonCallback;
    }

    public void addMouseButtonCallback(GLFWMouseButtonCallbackI callbackI) {
        mouseButtonCallback.addCallback(callbackI);
    }

    public Callbacks.CursorPosCallback getCursorPosCallback() {
        return cursorPosCallback;
    }

    public void addCursorPosCallback(GLFWCursorPosCallbackI callbackI) {
        cursorPosCallback.addCallback(callbackI);
    }

    public Callbacks.CharCallback getCharCallback() {
        return charCallback;
    }

    public void addCharCallback(GLFWCharCallbackI callbackI) {
        charCallback.addCallback(callbackI);
    }

    public Callbacks.CursorEnterCallback getCursorEnterCallback() {
        return cursorEnterCallback;
    }

    public void addCursorEnterCallback(GLFWCursorEnterCallbackI callbackI) {
        cursorEnterCallback.addCallback(callbackI);
    }

    private void setWindowIcon() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w        = stack.mallocInt(1);
            IntBuffer h        = stack.mallocInt(1);
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

    public long getWindowId() {
        return windowId;
    }

    /**
     * 初始化GLFW
     */
    private void setWindow() {
        setCallBacks();
        setWindowIcon();
        setCursorIcon();
    }

    private void setCallBacks() {
        windowSizeCallback = new Callbacks.WindowSizeCallback();
        glfwSetWindowSizeCallback(windowId, windowSizeCallback);
        windowSizeCallback.addCallback((window, width, height) -> {
            if (width == 0 || height == 0 || this.frameBufferWidth == 0 || this.frameBufferHeight == 0) {
                return;
            }
            pixelRadio  = this.frameBufferWidth <= width ? 1 : (float) this.frameBufferWidth / width;
            this.width  = width;
            this.height = height;
            resized     = true;
        });

        framebufferSizeCallback = new Callbacks.FramebufferSizeCallback();
        glfwSetFramebufferSizeCallback(windowId, framebufferSizeCallback);
        framebufferSizeCallback.addCallback((window, width, height) -> {
            if (width == 0 || height == 0 || this.width == 0 || this.height == 0) {
                return;
            }

            pixelRadio        = width <= this.width ? 1 : (float) width / this.width;
            frameBufferHeight = height;
            frameBufferWidth  = width;
            resized           = true;
        });

        windowFocusCallback = new Callbacks.WindowFocusCallback();
        glfwSetWindowFocusCallback(windowId, windowFocusCallback);
        windowFocusCallback.addCallback((window, focused) -> this.focused = focused);

        windowIconifyCallback = new Callbacks.WindowIconifyCallback();
        glfwSetWindowIconifyCallback(windowId, windowIconifyCallback);
        windowIconifyCallback.addCallback((window, iconified) -> {
            this.iconified = iconified;
            this.resized   = true;
        });

        windowMaximizeCallback = new Callbacks.WindowMaximizeCallback();
        glfwSetWindowMaximizeCallback(windowId, windowMaximizeCallback);
        windowMaximizeCallback.addCallback((window, maximized) -> {
            this.maximized = maximized;
            this.resized   = true;
        });

        keyCallback = new Callbacks.KeyCallback();
        glfwSetKeyCallback(windowId, keyCallback);
        keyCallback.addCallback((window, key, scancode, action, mods) -> {
            keyEvent.setKeyState(key, action == GLFW_PRESS || action == GLFW_REPEAT);

            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_F12) {
                    debugMode = !debugMode;
                }
            }

            if (key == GLFW_KEY_ESCAPE && mouseEvent.isHiddenCursor()) {
                resetCursor();
                mouseEvent.setHiddenCursor(false);
                actionEvent.setHiddenCursor(false);
            }
        });

        mouseButtonCallback = new Callbacks.MouseButtonCallback();
        glfwSetMouseButtonCallback(windowId, mouseButtonCallback);
        mouseButtonCallback.addCallback((window, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                actionEvent.addPressedButton(button);
                actionEvent.addHoldingButton(button);
            } else if (action == GLFW_RELEASE) {
                actionEvent.addReleasedButton(button);
                actionEvent.removeHoldingButton(button);
            }

            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (!mouseEvent.isHiddenCursor()) {
                    mouseEvent.setHiddenCursor(true);
                    actionEvent.setHiddenCursor(true);
                }
            }
        });

        cursorPosCallback = new Callbacks.CursorPosCallback();
        glfwSetCursorPosCallback(windowId, cursorPosCallback);
        cursorPosCallback.addCallback((l, xPos, yPos) -> {
            actionEvent.setCursorPosX(xPos);
            actionEvent.setCursorPosY(yPos);

            mouseEvent.setCursorPosX(xPos);
            mouseEvent.setCursorPosY(yPos);
        });

        charCallback = new Callbacks.CharCallback();
        glfwSetCharCallback(windowId, charCallback);
        charCallback.addCallback((long l, int codePoint) -> {
            keyEvent.getInputQueue().offer(codePoint);
            charInput = true;
        });

        cursorEnterCallback = new Callbacks.CursorEnterCallback();
        glfwSetCursorEnterCallback(windowId, cursorEnterCallback);
        cursorEnterCallback.addCallback((window, entered) -> {
            mouseEvent.setInWindow(entered);
            actionEvent.setInWindow(entered);
        });
    }

    protected void setContext() {
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

        glfwSwapInterval(vSync ? 1 : 0);
    }

}
