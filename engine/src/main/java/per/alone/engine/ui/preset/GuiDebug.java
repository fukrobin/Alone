package per.alone.engine.ui.preset;

import per.alone.engine.kernel.DebugInfo;
import per.alone.engine.ui.BaseGui;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Font;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.stage.Window;

import java.io.IOException;

/**
 * @author Administrator
 */
public class GuiDebug extends BaseGui {
    private final Font font;

    private int currentX;

    private int currentY;

    public GuiDebug() {
        font = new Font(18, "sans");
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
    public void draw(Window window, Canvas canvas) {
        currentX = 0;
        currentY = 0;

        currentX = window.getWidth() - 10;
        currentY = 20;

        DebugInfo debugInfo = EngineThread.getDebugInfo();

        canvas.setFont(font, TextAlignment.TOP_RIGHT);

        debugInfo.getEngineInfo().values().forEach(stringBuilder -> {
            canvas.drawText(stringBuilder, currentX, currentY);
            currentY += font.getFontSize() + 5;
        });


        currentX = 10;
        currentY = 20;
        canvas.textAlign(TextAlignment.TOP_LEFT);

        debugInfo.getGameInfo().values().forEach(stringBuilder -> {
            canvas.drawText(stringBuilder, currentX, currentY);
            currentY += font.getFontSize() + 5;
        });
    }

    public void setFontSize(int fontSize) {
        font.setFontSize(fontSize);
    }

    @Override
    public void close() throws IOException {

    }
}
