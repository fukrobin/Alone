package pers.crobin.engine.kernel;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author CRobin
 * @Date 2020/4/4 19:29
 * @Description 引擎核心
 **/
public abstract class BaseEngine {
    protected static final Logger LOGGER = LoggerFactory.getLogger("BaseEngine");

    public static void launch(String[] args) {
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();

        boolean foundThisMethod  = false;
        String  callingClassName = null;

        for (StackTraceElement stackTraceElement : cause) {
            String className  = stackTraceElement.getClassName();
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
        Object object = null;
        try {
            Class<?> clazz = Class.forName(callingClassName, true, Thread.currentThread().getContextClassLoader());
            object = clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        launch((BaseEngine)object, args);
    }

    public static void launch(BaseEngine baseEngine, String[] args) {
        LOGGER.info("Engine launch.");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }

        LOGGER.info("Engine initializing.");
        WindowManager.init();
        EngineCore engineCore = new EngineCore(854, 480, "Voxel Engine", true) {
            @Override protected void start(Window window, Linker linker) {
                super.start(window, linker);
                baseEngine.start(args, linker, window);
            }

            @Override protected void update(float interval) {
                super.update(interval);
                baseEngine.run();
            }

            @Override protected void errCallback(Exception e) {
                super.errCallback(e);
                baseEngine.errorCallback(e);
            }

            @Override protected void cleanup() {
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
        }
        baseEngine.shutdown();
        LOGGER.info("Engine quit.");
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

    public abstract void errorCallback(Exception e);

    public abstract void start(String[] args, Linker linker, Window window);

    public abstract void run();
}
