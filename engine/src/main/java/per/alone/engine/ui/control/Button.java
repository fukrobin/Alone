package per.alone.engine.ui.control;

import lombok.Getter;
import org.joml.Vector2f;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Text;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.event.EventHandler;
import per.alone.stage.input.ActionEvent;

/**
 * @author Administrator
 */
@Getter
public class Button extends Region {
    public static final Vector2f MIN_SIZE = new Vector2f(50f, 20f);

    private final Text textControl;

    public Button() {
        super();
        textControl = new Text();
        textControl.setAlign(TextAlignment.CENTER);
        setSize(MIN_SIZE);
    }

    public Button(String text) {
        textControl = new Text(text);
        textControl.setAlign(TextAlignment.CENTER);
        setSize(MIN_SIZE);
    }

    @Override
    public void setSize(float w, float h) {
        if (w >= MIN_SIZE.x && h >= MIN_SIZE.y) {
            size.x = w;
            size.y = h;
            textControl.setPosition(position.x + w * 0.5f, position.y + h * 0.5f);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        textControl.setPosition(x + size.x * 0.5f, y + size.y * 0.5f);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        textControl.draw(canvas);
    }

    public String getText() {
        return textControl.getText();
    }

    public void setText(String text) {
        textControl.setText(text);
    }

    /**
     * Button 被触发时将会调用，如：鼠标点击、键盘按键
     *
     * @param handler 处理程序
     */
    public void setOnAction(EventHandler<ActionEvent> handler) {
        setEventHandler(ActionEvent.ACTION, handler);
    }
}
