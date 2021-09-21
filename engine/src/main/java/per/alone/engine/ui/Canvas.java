package per.alone.engine.ui;

import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryUtil;
import per.alone.engine.exception.ComponentLoadException;
import per.alone.engine.ui.text.Font;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.engine.util.Utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author Administrator
 * @date 2020/6/6:00点46分
 */
@Slf4j
public class Canvas {

    private static final NVGColor TEMP = NVGColor.create();

    private ByteBuffer sansFont;

    private ByteBuffer sansBoldFont;

    private ByteBuffer iconFont;

    private long context = -1;

    public Canvas() {
        try {
            loadFont();
            context = createNvgContext();
        } catch (Exception e) {
            throw new ComponentLoadException(e.getMessage(), e);
        } finally {
            if (context == -1) {
                log.error("Could not add font data");
                cleanup();
            }
        }
    }

    private void loadFont() throws IOException {
        sansFont     = Utils.loadResourceToByteBuffer("/asserts/fonts/Deng.ttf");
        sansBoldFont = Utils.loadResourceToByteBuffer("/asserts/fonts/Dengb.ttf");
        iconFont     = Utils.loadResourceToByteBuffer("/asserts/fonts/fontawesome.ttf");
    }

    private long createNvgContext() {
        long context = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_DEBUG);

        try {
            int d = nvgCreateFontMem(context, "sans", sansFont, 0);
            if (d == -1) {
                throw new RuntimeException("Could not add font sans.");
            }

            d = nvgCreateFontMem(context, "sans-bold", sansBoldFont, 0);
            if (d == -1) {
                throw new RuntimeException("Could not add font sans-bold.");
            }

            d = nvgCreateFontMem(context, "icons", iconFont, 0);
            if (d == -1) {
                throw new RuntimeException("Could not add font icons.");
            }
        } catch (RuntimeException e) {
            NanoVGGL3.nvgDelete(context);
            log.error(e.getMessage());
            return -1;
        }
        return context;
    }

    public long getContext() {
        return context;
    }

    public void setContext(long context) {
        this.context = context;
    }

    public void fontFace(CharSequence face) {
        nvgFontFace(context, face);
    }

    public void fontSize(float fontSize) {
        nvgFontSize(context, fontSize);
    }

    public void textColor(Vector4i color) {
        fillColor(color);
    }

    public void textAlign(TextAlignment align) {
        nvgTextAlign(context, align.getAlign());
    }

    public void setFont(Font font) {
        fontFace(font.getFontFace());
        fontSize(font.getFontSize());
        textColor(font.getColor());
    }

    public void setFont(Font font, TextAlignment alignment) {
        setFont(font);
        textAlign(alignment);
    }

    public void drawText(CharSequence text, Vector2f pos, Font font, TextAlignment alignment) {
        setFont(font, alignment);
        nvgText(context, pos.x, pos.y, text);
    }

    public void drawText(CharSequence text, Vector2f pos) {
        nvgText(context, pos.x, pos.y, text);
    }

    public void drawText(ByteBuffer text, Vector2f pos) {
        nvgText(context, pos.x, pos.y, text);
    }

    public void drawText(CharSequence text, float x, float y) {
        nvgText(context, x, y, text);
    }

    public void drawText(ByteBuffer text, float x, float y) {
        nvgText(context, x, y, text);
    }

    public void drawText(NVGTextRow row, Vector2f pos) {
        nnvgText(context, pos.x, pos.y, row.start(), row.end());
    }

    public void textBox(float x, float y, float breakRowWidth, CharSequence text) {
        nvgTextBox(context, x, y, breakRowWidth, text);
    }

    public void textBoxBounds(float x, float y, float breakRowWidth, CharSequence text,
                              @Nullable FloatBuffer bounds) {
        nvgTextBoxBounds(context, x, y, breakRowWidth, text, bounds);
    }

    public void textBoxBounds(float x, float y, float breakRowWidth, CharSequence text,
                              @Nullable float[] bounds) {
        nvgTextBoxBounds(context, x, y, breakRowWidth, text, bounds);
    }

    public int textBreakLines(CharSequence text, float breakRowWidth, NVGTextRow.Buffer buffer) {
        return nvgTextBreakLines(context, text, breakRowWidth, buffer);
    }

    public void beginPath() {
        nvgBeginPath(context);
    }

    public void fillColor(Vector4i color) {
        nvgFillColor(context, Utils.rgba(color, TEMP));
    }

    public void fill() {
        nvgFill(context);
    }

    public void stroke() {
        nvgStroke(context);
    }

    public void strokeWidth(float width) {
        nvgStrokeWidth(context, width);
    }

    public void strokeColor(Vector4i color) {
        nvgStrokeColor(context, Utils.rgba(color, TEMP));
    }

    public void drawLine(float fromX, float fromY, float toX, float toY) {
        if (context != -1) {
            nvgBeginPath(context);
            nvgMoveTo(context, fromX, fromY);
            nvgLineTo(context, toX, toY);
            nvgFill(context);
        }
    }

    public void drawLine(Vector2f from, Vector2f to) {
        drawLine(from.x, from.y, to.x, to.y);
    }

    public void roundingRect(float posX, float posY, float width, float height, float rounding) {
        nvgRoundedRect(context, posX, posY, width, height, rounding);
    }

    public void drawRoundingRect(float posX, float posY, float width, float height, float rounding) {
        beginPath();
        roundingRect(posX, posY, width, height, rounding);
        fill();
    }

    public void cleanup() {
        cleanFont();
        if (context != -1 && context != MemoryUtil.NULL) {
            NanoVGGL3.nvgDelete(context);
            context = -1;
        }
    }

    private void cleanFont() {
        MemoryUtil.memFree(sansFont);
        MemoryUtil.memFree(sansBoldFont);
        MemoryUtil.memFree(iconFont);
        sansFont     = null;
        sansBoldFont = null;
        iconFont     = null;
    }
}
