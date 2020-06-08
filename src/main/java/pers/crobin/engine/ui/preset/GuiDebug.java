package pers.crobin.engine.ui.preset;

import pers.crobin.engine.kernel.DebugInfo;
import pers.crobin.engine.kernel.EngineThread;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.ui.BaseGui;
import pers.crobin.engine.ui.text.Font;
import pers.crobin.engine.util.Utils;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author Administrator
 */
public class GuiDebug extends BaseGui {
    private final Font font;
    private       int  currentX;
    private       int  currentY;

    public GuiDebug() {
        font = new Font(18, "sans");
    }

    @Override
    public void start() {

    }

    @Override
    public boolean isVisible() {
        return EngineThread.getThreadWindow().isDebugMode();
    }

    @Override
    public boolean isDisable() {
        return false;
    }

    @Override
    public void draw(long context, Window window) {
        currentX = 0;
        currentY = 0;

        currentX = window.getWidth() - 10;
        currentY = 20;

        DebugInfo debugInfo = EngineThread.getDebugInfo();

        nvgFontFace(context, font.getFontFace());
        nvgFontSize(context, font.getFontSize());
        nvgFillColor(context, Utils.rgba(font.getColor(), RESULT_COLOR));
        nvgTextAlign(context, NVG_ALIGN_RIGHT | NVG_ALIGN_TOP);

        debugInfo.getEngineInfo().values().forEach(stringBuilder -> {
            nvgText(context, currentX, currentY, stringBuilder);
            currentY += font.getFontSize() + 5;
        });


        currentX = 10;
        currentY = 20;
        nvgTextAlign(context, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

        debugInfo.getGameInfo().values().forEach(stringBuilder -> {
            nvgText(context, currentX, currentY, stringBuilder);
            currentY += font.getFontSize() + 5;
        });
    }

    public void setFontSize(int fontSize) {
        font.setFontSize(fontSize);
    }


    @Override
    public void cleanup() {

    }
}
