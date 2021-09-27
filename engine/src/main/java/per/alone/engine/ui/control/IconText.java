package per.alone.engine.ui.control;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.Color;
import per.alone.engine.ui.text.Alignment;

/**
 * 配有文字的的图标控件，此控件需要自行为文字部分分配足够的空间，
 * 因此建议只使用简短且清晰表达意图的文字。
 * <p>
 * 同时请注意，此控件的布局范围事实上是没有计算文字区域的，值计算了图标的区域
 *
 * @author Administrator
 */
public class IconText extends Icon {

    private Color textColor;

    private String text;

    IconText() {
        super();
    }

    public IconText(int iconCodePoint, String text) {
        super(iconCodePoint);
        this.text = text;
    }

    public IconText(int iconCodePoint, String text, int iconSize) {
        this(iconCodePoint, text);
        this.font.setFontSize(iconSize);
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.fontSize(font.getFontSize() - 2);
        canvas.fontFace("sans");
        canvas.fillColor(textColor);
        canvas.textAlign(Alignment.TOP_LEFT);
        canvas.drawText(text, position.x + font.getFontSize() + 5, position.y + 1);
    }
}
