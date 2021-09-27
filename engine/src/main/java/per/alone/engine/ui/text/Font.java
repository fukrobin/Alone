package per.alone.engine.ui.text;

import lombok.Getter;
import lombok.Setter;
import per.alone.engine.ui.Color;

/**
 * @author Administrator
 */
@Getter
@Setter
public class Font {
    public static final int MIN_FONT_SIZE = 5;

    protected Color color;

    protected int fontSize;

    protected String fontFace;

    public Font(int fontSize, String fontFace) {
        this(fontSize, fontFace, Color.BLACK);
    }

    public Font(int fontSize, String fontFace, Color color) {
        this.fontSize = fontSize;
        this.fontFace = fontFace;
        this.color = color;
    }

    public void setFontSize(int fontSize) {
        if (fontSize > MIN_FONT_SIZE) {
            this.fontSize = fontSize;
        }
    }

    private static Font DEFAULT;
    public static synchronized Font getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new Font(14, "sans", Color.BLACK);
        }
        return DEFAULT;
    }
}
