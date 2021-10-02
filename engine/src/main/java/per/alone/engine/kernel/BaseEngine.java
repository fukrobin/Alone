package per.alone.engine.kernel;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.alone.AloneContext;
import per.alone.engine.context.EngineContext;
import per.alone.engine.context.EngineContextEvent;
import per.alone.engine.context.EngineContextListener;
import per.alone.engine.renderer.RendererComponent;
import per.alone.stage.Window;
import per.alone.stage.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * 引擎核心
 *
 * @author fkobin
 * @date 2020/4/4 19:29
 **/
public class BaseEngine {
    protected static final Logger LOGGER = LoggerFactory.getLogger("BaseEngine");

    private static EngineCore engineCore;

    private String[] args;

    public static void launch(String[] args) {
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();

        boolean foundThisMethod = false;
        String callingClassName = null;

        for (StackTraceElement stackTraceElement : cause) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            if (foundThisMethod) {
                callingClassName = className;
                break;
            } else if (BaseEngine.class.getName().equals(className) && "launch".equals(methodName)) {
                foundThisMethod = true;
            }
        }

        if (callingClassName == null) {
            throw new RuntimeException("Error: unable to determine main class");
        }
        BaseEngine object;
        try {
            Class<?> clazz = Class.forName(callingClassName, true, Thread.currentThread().getContextClassLoader());
            Constructor<?> defaultConstructor = null;
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.getParameterCount() == 0) {
                    defaultConstructor = constructor;
                    break;
                }
            }
            if (defaultConstructor == null) {
                throw new RuntimeException("Base Engine must be have a default constructor");
            }
            object      = (BaseEngine) defaultConstructor.newInstance();
            object.args = args;
            object.launch();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static AloneContext createContext() {
        Window window = createWindow(854, 480, "Voxel Engine", true);
        return AloneContext.builder()
                           .window(window)
                           .build();
    }

    private static Window createWindow(int width, int height, String title, boolean vsync) {
        WindowManager.init();
        return WindowManager.createWindow(width, height, title, vsync);
    }

    void launch() {
        LOGGER.info("Engine launch.");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }

        LOGGER.info("Engine initializing.");

        BaseEngine baseEngine = this;
        AloneContext context = createContext();
        engineCore = new EngineCore(context.getWindow(), true) {
            @Override
            protected void start(Window window, EngineContext engineContext) {
                super.start(window, engineContext);
                baseEngine.start(args, engineContext, window);
            }

            @Override
            protected void update(float interval) {
                super.update(interval);
                baseEngine.update();
            }

            @Override
            protected void errCallback(Exception e) {
                super.errCallback(e);
                baseEngine.errorCallback(e);
            }

            @Override
            protected void cleanup() {
                super.cleanup();
                baseEngine.cleanup();
            }
        };
        engineCore.start();

        baseEngine.loop();

        try {
            engineCore.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            baseEngine.shutdown();
            LOGGER.info("Engine quit.");
        }
    }

    private void loop() {
        ClientSync sync = new ClientSync();

        while (!WindowManager.isEmpty()) {
            glfwWaitEventsTimeout(0.2);
            WindowManager.update();

//            sync.sync(120);
        }
    }

    private void shutdown() {
        glfwTerminate();
        GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        if (errorCallback != null) {
            errorCallback.free();
        }
    }

    protected void cleanup() {

    }

    public void addEngineComponents(EngineComponent... engineComponents) {
        engineCore.addEngineComponents(engineComponents);
    }

    public void addRendererComponents(RendererComponent... rendererComponents) {
        engineCore.addRendererComponents(rendererComponents);
    }

    public void addEngineComponents(List<EngineComponent> engineComponents) {
        engineCore.addEngineComponents(engineComponents);
    }

    public void addRendererComponents(List<RendererComponent> rendererComponents) {
        engineCore.addRendererComponents(rendererComponents);
    }

    public void addEngineContextListeners(
            EngineContextListener<? extends EngineContextEvent>... engineContextListeners) {
        engineCore.addEngineContextListeners(engineContextListeners);
    }

    public void addEngineContextListeners(
            List<EngineContextListener<? extends EngineContextEvent>> engineContextListeners) {
        engineCore.addEngineContextListeners(engineContextListeners);
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    protected void errorCallback(Exception e) {

    }

    protected void start(String[] args, EngineContext engineContext, Window window) {

    }

    protected void update() {

    }
}
