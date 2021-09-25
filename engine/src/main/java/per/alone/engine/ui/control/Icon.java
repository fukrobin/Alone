package per.alone.engine.ui.control;

import lombok.Getter;
import per.alone.engine.geometry.Bounds;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Font;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.engine.util.Utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Administrator
 */
@Getter
public class Icon extends Widgets {
    private static final Map<Integer, ByteBuffer> CODE_POINT_ICON = new HashMap<>(16);

    protected Font font;

    protected int codePoint;

    /**
     * 本质上是图标字体Unicode代码点的bytebuffer表示形式
     */
    private ByteBuffer iconBuffer;

    /**
     * 无参构造器，此构造器不做任何的多余事情，包括变量初始化
     */
    Icon() {
        super();
    }

    public Icon(int codePoint) {
        super();
        if (codePoint >= 0 && codePoint <= 0xf2e0) {
            this.codePoint  = codePoint;
            this.iconBuffer = CODE_POINT_ICON.computeIfAbsent(codePoint, Utils::cpToUtf8);
        } else {
            throw new IllegalArgumentException("The code point of the icon can only be between 0xf000 and 0xf2e0");
        }
    }

    public void setCodePoint(int codePoint) {
        if (this.codePoint != codePoint) {
            this.codePoint  = codePoint;
            this.iconBuffer = CODE_POINT_ICON.computeIfAbsent(codePoint, Utils::cpToUtf8);
        }
    }

    @Override
    public Bounds getLayoutBounds() {
        size.set(font.getFontSize());
        return super.getLayoutBounds();
    }

    @Override
    public void draw(Canvas canvas) {
        if (iconBuffer != null) {
            canvas.setFont(font, TextAlignment.TOP_LEFT);
            canvas.drawText(iconBuffer, position.x, position.y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Icon icon = (Icon) o;
        return codePoint == icon.codePoint &&
               font.equals(icon.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), font, codePoint);
    }
}
