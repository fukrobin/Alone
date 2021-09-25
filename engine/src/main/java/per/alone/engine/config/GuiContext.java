package per.alone.engine.config;

import lombok.extern.slf4j.Slf4j;
import per.alone.engine.ui.Canvas;
import per.alone.stage.Window;

import static org.lwjgl.nanovg.NanoVG.*;


/**
 * @author Administrator
 */
@Slf4j
public class GuiContext {
    private final Cmanvas canvas;

    private boolean inFrame;

    public GuiContext() {
        this.canvas = new Canvas();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void start(Window window) {
        if (inFrame) {
            end();
        }
        long context = canvas.getContext();
        nvgSave(context);
        nvgBeginFrame(context, window.getWidth(), window.getHeight(), window.getPixelRadio());
        inFrame = true;
    }

    public void end() {
        long context = canvas.getContext();
        nvgEndFrame(context);
        nvgRestore(context);
        inFrame = false;
    }

    public void cleanup() {
        canvas.cleanup();
    }
}
