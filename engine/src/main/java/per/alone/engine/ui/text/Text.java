package per.alone.engine.ui.text;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.system.MemoryStack;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.behavior.TextBehavior;
import per.alone.engine.ui.control.Widget;

import java.nio.FloatBuffer;
import java.util.Objects;

/**
 * Text类定义一个显示文本的节点。 段落之间用'\n'分隔，并且文本被包裹在段落边界上。
 *
 * @author Administrator
 */
@Getter
@Setter
public class Text extends Widget {
    public static final Alignment DEFAULT_ALIGNMENT = Alignment.TOP_LEFT;

    protected Font font;

    protected Alignment align;

    protected String text;

    /**
     * 为此<code>Text</code>控件定义一个宽度限制，例如 像素，而不是字形或字符数。
     * 如果值> 0， 则将根据需要对该行进行换行，以满足此约束。
     */
    protected float wrappingWidth;

    public Text() {
        super();
        this.align = DEFAULT_ALIGNMENT;
    }

    public Text(String text) {
        super();
        Objects.requireNonNull(text);
        this.text  = text;
        this.align = DEFAULT_ALIGNMENT;
    }

    public Font getFont() {
        if (font == null) {
            font = Font.getDefault();
        }
        return font;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.text != null) {
            canvas.setFont(font);
            canvas.textAlign(align);

            if (wrappingWidth != 0) {
                canvas.textBox(position.x, position.y, wrappingWidth, text);
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    // 测量Text的文本区域边界
                    FloatBuffer boundBuffer = stack.mallocFloat(4);
                    canvas.textBoxBounds(position.x, position.y, wrappingWidth, text, boundBuffer);
                    size.x = boundBuffer.get(2) - boundBuffer.get(0);
                    size.y = boundBuffer.get(3) - boundBuffer.get(1);
                }
            } else {
                canvas.drawText(text, position.x, position.y);
            }
        }
    }


    @Override
    protected TextBehavior<? extends Text> createWidgetBehavior() {
        return new TextBehavior<>(this);
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
        Text text1 = (Text) o;
        return font.equals(text1.font) &&
                text.equals(text1.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), font, text);
    }
}