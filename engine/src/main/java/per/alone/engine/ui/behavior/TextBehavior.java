package per.alone.engine.ui.behavior;

import com.google.common.base.Strings;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Text;

import java.nio.FloatBuffer;

/**
 * @author jamie
 * @param <W> Text
 */
public class TextBehavior<W extends Text> extends WidgetBehavior<W> {
    public TextBehavior(W widget) {
        super(widget);
    }

    @Override
    protected void renderContent(Canvas canvas) {
        Text widget = getWidget();
        String text = widget.getText();
        if (!Strings.isNullOrEmpty(text)) {
            canvas.setFont(widget.getFont());
            canvas.textAlign(widget.getAlign());

            float wrappingWidth = widget.getWrappingWidth();
            Vector2f position = widget.getPositionInWindow();
            if (wrappingWidth != 0) {
                Vector2f size = widget.getSize();
                canvas.textBox(position, wrappingWidth, text);
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    // 测量Text的文本区域边界
                    FloatBuffer boundBuffer = stack.mallocFloat(4);
                    canvas.textBoxBounds(position, wrappingWidth, text, boundBuffer);
                    size.x = boundBuffer.get(2) - boundBuffer.get(0);
                    size.y = boundBuffer.get(3) - boundBuffer.get(1);
                }
            } else {
                canvas.drawText(text, position);
            }
        }
    }
}
