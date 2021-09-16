package pers.crobin.engine.ui.text;

import org.joml.Vector4i;

/**
 * @author Administrator
 */
public class Font {
    public static final int      MIN_FONT_SIZE = 5;

    protected final     Vector4i color;

    protected           int      fontSize;

    protected           String   fontFace;

    public Font() {
        fontSize = 14;
        fontFace = "sans";
        color    = new Vector4i(255);
    }

    public Font(int fontSize, String fontFace) {
        this.fontSize = fontSize;
        this.fontFace = fontFace;
        this.color    = new Vector4i(255);
    }

    public Font(int fontSize, String fontFace, Vector4i color) {
        this.fontSize = fontSize;
        this.fontFace = fontFace;
        this.color    = color;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Font setFontSize(int fontSize) {
        if (fontSize > MIN_FONT_SIZE) {
            this.fontSize = fontSize;
        }
        return this;
    }

    public String getFontFace() {
        return fontFace;
    }

    public Font setFontFace(String fontFace) {
        this.fontFace = fontFace;
        return this;
    }

    public Vector4i getColor() {
        return color;
    }

    public Font setColor(Vector4i color) {
        this.color.set(color);
        return this;
    }

    public Font setColor(int r, int g, int b, int a) {
        this.color.set(r, g, b, a);
        return this;
    }
}
