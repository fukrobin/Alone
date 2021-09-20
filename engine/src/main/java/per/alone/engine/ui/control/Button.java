package per.alone.engine.ui.control;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.joml.Vector2f;
import per.alone.engine.event.ActionEvent;
import per.alone.engine.event.EventHandler;
import per.alone.engine.ui.text.Text;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.stage.input.MouseEvent;

/**
 * @author Administrator
 */
public class Button extends Region {
    public static final Vector2f MIN_SIZE = new Vector2f(50f, 20f);

    private final Text textControl;

    /**
     * <code>onAction、mouseClicked、mouseRelease</code>实际上是同时触发的相同事件，可以酌情注册它们
     */
    protected EventHandler<ActionEvent> onAction;

    protected EventHandler<MouseEvent>  onHover;

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
    public Button setSize(Vector2f size) {
        this.setSize(size.x, size.y);

        return this;
    }

    @Override
    public Button setSize(float w, float h) {
        if (w >= MIN_SIZE.x && h >= MIN_SIZE.y) {
            size.x = w;
            size.y = h;
            textControl.setPosition(position.x + w * 0.5f, position.y + h * 0.5f);
        }

        return this;
    }

    @Override
    public Button setPosition(Vector2f position) {
        this.setPosition(position.x, position.y);

        return this;
    }

    @Override
    public Button setPosition(float x, float y) {
        position.set(x, y);
        textControl.setPosition(x + size.x * 0.5f, y + size.y * 0.5f);

        return this;
    }

    public Text getTextControl() {
        return textControl;
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        super.draw(offsetX, offsetY);
        textControl.draw(offsetX, offsetY);
    }

    public String getText() {
        return textControl.getText();
    }

    public Button setText(String text) {
        textControl.setText(text);

        return this;
    }

    /**
     * <code>onAction、mouseClicked、mouseRelease</code>实际上是同时触发的相同事件，
     * 可以酌情注册它们
     *
     * @return 当前 <code>Button</code> 在鼠标按下时的触发事件
     */
    public EventHandler<ActionEvent> getOnAction() {
        return onAction;
    }

    public Button setOnAction(EventHandler<ActionEvent> onAction) {
        this.onAction = onAction;
        return this;
    }

    public EventHandler<MouseEvent> getOnHover() {
        return onHover;
    }

    public Button setOnHover(EventHandler<MouseEvent> onHover) {
        this.onHover = onHover;
        return this;
    }

    @Override
    public String toJsonString() {
        return new GsonBuilder().registerTypeAdapter(Button.class,
                                                     (JsonSerializer<Button>) (src, typeOfSrc, context) -> src
                                                             .getJsonObject()).create().toJson(this);
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject buttonObject = super.getJsonObject();

        buttonObject.add("text-control", textControl.getJsonObject());

        return buttonObject;
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        // 必须有text-control属性
        JsonElement textControlElement = object.get("text-control");
        if (textControlElement != null && !textControlElement.isJsonNull()) {
            this.textControl.setupFromJson(textControlElement.getAsJsonObject());
        } else {
            throw new IllegalArgumentException("The button control must have a text-control and cannot be null.");
        }
    }
}
