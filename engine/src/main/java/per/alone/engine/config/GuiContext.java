package per.alone.engine.config;

import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryUtil;
import per.alone.engine.util.Utils;
import per.alone.stage.Window;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.*;


/**
 * @author Administrator
 */
public class GuiContext {

    private final ByteBuffer sansFont;

    private final ByteBuffer sansBoldFont;

    private final ByteBuffer iconFont;

    private       long       context;

    public GuiContext() throws IOException {
        sansFont     = Utils.loadResourceToByteBuffer("/asserts/fonts/Deng.ttf");
        sansBoldFont = Utils.loadResourceToByteBuffer("/asserts/fonts/Dengb.ttf");
        iconFont     = Utils.loadResourceToByteBuffer("/asserts/fonts/fontawesome.ttf");
    }

    public void init() {
        context = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_DEBUG);

        int d = nvgCreateFontMem(context, "sans", sansFont, 0);
        if (d == -1) {
            throw new RuntimeException("Could not add font sans.\n");
        }

        d = nvgCreateFontMem(context, "sans-bold", sansBoldFont, 0);
        if (d == -1) {
            throw new RuntimeException("Could not add font sans-bold.\n");
        }

        d = nvgCreateFontMem(context, "icons", iconFont, 0);
        if (d == -1) {
            throw new RuntimeException("Could not add font icons.\n");
        }
    }

    public long getContext() {
        return context;
    }

    public void start(Window window) {
        nvgSave(context);
        nvgBeginFrame(context, window.getWidth(), window.getHeight(), window.getPixelRadio());
    }

    public void end() {
        nvgEndFrame(context);
        nvgRestore(context);
    }

    public void cleanup() {
        MemoryUtil.memFree(sansFont);
        MemoryUtil.memFree(sansBoldFont);
        MemoryUtil.memFree(iconFont);
        NanoVGGL3.nvgDelete(context);
    }
}
