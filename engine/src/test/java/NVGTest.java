import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.Color;
import per.alone.stage.Window;
import per.alone.stage.WindowManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/25 16:13
 */
public class NVGTest {

    // The window handle
    private Window window;

    private Canvas canvas;

    public static void main(String[] args) {
        new NVGTest().run();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        initCanvas();

        try {
            loop();
        } finally {
            canvas.cleanup();
            window.shutdown();
            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void initCanvas() {
        canvas = new Canvas();
    }

    private void init() {
        WindowManager.init();
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }

        window = WindowManager.createWindow(500, 500, NVGTest.class.getSimpleName(), true);
        WindowManager.bindWindowContext(window);
    }

    private void draw() {
        long context = canvas.getContext();
        nvgSave(context);
        nvgBeginFrame(context, window.getWidth(), window.getHeight(), window.getPixelRadio());

        nvgTranslate(context, 100, 100);

        canvas.fillColor(Color.WHITE);
        canvas.drawRoundingRect(10,
                                0,
                                100, 100,
                                5);

        canvas.strokeColor(Color.RED);
        canvas.strokeWidth(2);
        canvas.stroke();

        nvgEndFrame(context);
        nvgRestore(context);
    }

    private void loop() {
        window.show();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window.getWindowId())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            draw();

            window.swapBuffers();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
}
