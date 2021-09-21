package per.alone.engine.ui.text;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4i;

/**
 * @author Administrator
 */
@Getter
@Setter
public class Font {
    public static final int MIN_FONT_SIZE = 5;

    protected final Vector4i color;

    protected int fontSize;

    protected String fontFace;

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

    public void setFontSize(int fontSize) {
        if (fontSize > MIN_FONT_SIZE) {
            this.fontSize = fontSize;
        }
    }

    public void setColor(Vector4i color) {
        setColor(color.x, color.y, color.z, color.w);
    }

    public void setColor(int r, int g, int b, int a) {
        this.color.set(r, g, b, a);
    }
}
