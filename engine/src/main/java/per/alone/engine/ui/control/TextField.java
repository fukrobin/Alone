package per.alone.engine.ui.control;

import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGTextRow;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Alignment;

/**
 * @author Administrator
 */
public class TextField extends Region {
    private static final NVGTextRow.Buffer BUFFER = NVGTextRow.create(2);

    /**
     * 按键触发间隔，单位为ms
     */
    private static final double FIRE_INTERVAL = 0.1;

    protected final TextInputControl inputControl;

    protected boolean focus;

    public TextField() {
        inputControl = new TextInputControl();
        focus = false;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float x = position.x + 2;
        float y = position.y;

        canvas.setFont(inputControl.font, Alignment.CENTER_LEFT);
        canvas.drawText(inputControl.text, new Vector2f(x, y + size.y * 0.5f));
        if (focus) {
            // 绘制光标
            int caret = inputControl.caretPosition;
            // 无字符串时，此处的代码有问题，需要清空BUFFER内的数据
            if (caret > 0) {
                canvas.textBreakLines(inputControl.getText(0, inputControl.caretPosition), size.x - 4, BUFFER);
                NVGTextRow row = BUFFER.get(0);
                canvas.drawLine(x + row.maxx(), y + 5, x + row.maxx(), y - 5 + size.y);
            } else {
                canvas.drawLine(x, y + 5, x, y - 5 + size.y);
            }
        }
    }
}
