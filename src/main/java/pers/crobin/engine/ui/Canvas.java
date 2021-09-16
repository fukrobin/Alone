package pers.crobin.engine.ui;

import org.joml.Vector2f;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import pers.crobin.engine.ui.text.Font;
import pers.crobin.engine.ui.text.TextAlignment;
import pers.crobin.engine.util.Utils;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * @author Administrator
 * @date 2020/6/6:00点46分
 */
public class Canvas {
    private static final NVGColor TEMP    = NVGColor.create();

    private static       long     context = -1;

    public static long getContext() {
        return context;
    }

    public static void setContext(long context) {
        Canvas.context = context;
    }

    public static void fontFace(CharSequence face) {
        nvgFontFace(context, face);
    }

    public static void fontSize(float fontSize) {
        nvgFontSize(context, fontSize);
    }

    public static void textColor(Vector4i color) {
        fillColor(color);
    }

    public static void textAlign(TextAlignment align) {
        nvgTextAlign(context, align.getAlign());
    }

    public static void setFont(Font font) {
        fontFace(font.getFontFace());
        fontSize(font.getFontSize());
        textColor(font.getColor());
    }

    public static void setFont(Font font, TextAlignment alignment) {
        setFont(font);
        textAlign(alignment);
    }

    public static void drawText(CharSequence text, Vector2f pos, Font font, TextAlignment alignment) {
        setFont(font, alignment);
        nvgText(context, pos.x, pos.y, text);
    }

    public static void drawText(CharSequence text, Vector2f pos) {
        nvgText(context, pos.x, pos.y, text);
    }

    public static void drawText(ByteBuffer text, Vector2f pos) {
        nvgText(context, pos.x, pos.y, text);
    }

    public static void drawText(CharSequence text, float x, float y) {
        nvgText(context, x, y, text);
    }

    public static void drawText(ByteBuffer text, float x, float y) {
        nvgText(context, x, y, text);
    }

    public static void drawText(NVGTextRow row, Vector2f pos) {
        nnvgText(context, pos.x, pos.y, row.start(), row.end());
    }

    public static void textBox(float x, float y, float breakRowWidth, CharSequence text) {
        nvgTextBox(context, x, y, breakRowWidth, text);
    }

    public static void textBoxBounds(float x, float y, float breakRowWidth, CharSequence text,
                                     @Nullable FloatBuffer bounds) {
        nvgTextBoxBounds(context, x, y, breakRowWidth, text, bounds);
    }

    public static void textBoxBounds(float x, float y, float breakRowWidth, CharSequence text,
                                     @Nullable float[] bounds) {
        nvgTextBoxBounds(context, x, y, breakRowWidth, text, bounds);
    }

    public static int textBreakLines(CharSequence text, float breakRowWidth, NVGTextRow.Buffer buffer) {
        return nvgTextBreakLines(context, text, breakRowWidth, buffer);
    }

    public static void beginPath() {
        nvgBeginPath(context);
    }

    public static void fillColor(Vector4i color) {
        nvgFillColor(context, Utils.rgba(color, TEMP));
    }

    public static void fill() {
        nvgFill(context);
    }

    public static void stroke() {
        nvgStroke(context);
    }

    public static void strokeWidth(float width) {
        nvgStrokeWidth(context, width);
    }

    public static void strokeColor(Vector4i color) {
        nvgStrokeColor(context, Utils.rgba(color, TEMP));
    }

    public static void drawLine(float fromX, float fromY, float toX, float toY) {
        if (context != -1) {
            nvgBeginPath(context);
            nvgMoveTo(context, fromX, fromY);
            nvgLineTo(context, toX, toY);
            nvgFill(context);
        }
    }

    public static void drawLine(Vector2f from, Vector2f to) {
        drawLine(from.x, from.y, to.x, to.y);
    }

    public static void roundingRect(float posX, float posY, float width, float height, float rounding) {
        nvgRoundedRect(context, posX, posY, width, height, rounding);
    }

    public static void drawRoundingRect(float posX, float posY, float width, float height, float rounding) {
        beginPath();
        roundingRect(posX, posY, width, height, rounding);
        fill();
    }
}
