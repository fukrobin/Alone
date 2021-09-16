package pers.crobin.engine.ui.control;

import com.google.gson.JsonObject;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import pers.crobin.engine.event.ActionEvent;
import pers.crobin.engine.event.EventHandler;
import pers.crobin.engine.geometry.BoundingBox;
import pers.crobin.engine.geometry.Bounds;

import java.util.Objects;

/**
 * @author Administrator
 */
public abstract class BaseControl {
    protected static final NVGColor RESULT = NVGColor.create();

    /**
     * 在Gui中的偏移位置，而不是在整个窗口中的位置
     */
    protected final        Vector2f position;

    /**
     * 此控件的尺寸
     */
    protected final        Vector2f size;

    /**
     * 此控件的父控件
     */
    protected              Parent   parent;

    /**
     * 可见性，只有可见的 {@link BaseControl} 才能被渲染
     */
    protected              boolean  visible;

    /**
     * 鼠标按下事件
     */
    protected EventHandler<ActionEvent> mousePressedEvent;

    /**
     * 鼠标释放事件
     */
    protected EventHandler<ActionEvent> mouseReleasedEvent;

    /**
     * 鼠标点击事件
     */
    protected EventHandler<ActionEvent> mouseClickedEvent;

    protected BaseControl() {
        this.position = new Vector2f();
        this.size     = new Vector2f();
        this.visible  = true;
        this.parent   = null;
    }

    public Parent getParent() {
        return parent;
    }

    public <T extends BaseControl> T setParent(Parent parent) {
        this.parent = parent;
        return (T) this;
    }

    public boolean isVisible() {
        return visible;
    }

    public <T extends BaseControl> T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }


    //////////////////////////
    /// Position
    //////////////////////////

    public Vector2f getPosition() {
        return position;
    }

    public <T extends BaseControl> T setPosition(Vector2f position) {
        this.position.set(position);

        return (T) this;
    }

    public <T extends BaseControl> T setPosition(float x, float y) {
        this.position.set(x, y);
        return (T) this;
    }

    public double getLayoutX() {
        return position.x;
    }

    public double getLayoutY() {
        return position.y;
    }

    ///////////////////////////////
    /// Size
    ///////////////////////////////

    public Vector2f getSize() {
        return size;
    }

    public <T extends BaseControl> T setSize(Vector2f size) {
        this.size.set(size);

        return (T) this;
    }

    public <T extends BaseControl> T setSize(float width, float height) {
        this.size.set(width, height);
        return (T) this;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }


    ///////////////////////////////
    /// EventHandler
    ///////////////////////////////

    public EventHandler<ActionEvent> getMousePressedEvent() {
        return mousePressedEvent;
    }

    public BaseControl setMousePressedEvent(EventHandler<ActionEvent> mousePressedEvent) {
        this.mousePressedEvent = mousePressedEvent;
        return this;
    }

    public EventHandler<ActionEvent> getMouseReleasedEvent() {
        return mouseReleasedEvent;
    }

    public BaseControl setMouseReleasedEvent(EventHandler<ActionEvent> mouseReleasedEvent) {
        this.mouseReleasedEvent = mouseReleasedEvent;
        return this;
    }

    public EventHandler<ActionEvent> getMouseClickedEvent() {
        return mouseClickedEvent;
    }

    public BaseControl setMouseClickedEvent(EventHandler<ActionEvent> mouseClickedEvent) {
        this.mouseClickedEvent = mouseClickedEvent;
        return this;
    }

    public Bounds getLayoutBounds() {
        if (parent == null) {
            return new BoundingBox(position.x, position.y, size.x, size.y);
        }
        return new BoundingBox(parent.position.x + position.x, parent.position.y + position.y, size.x, size.y);
    }

    ///////////////////////////////
    /// Json
    ///////////////////////////////

    /**
     * 获取此<code>Control</code>的Json描述对象
     */
    protected JsonObject getJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("layout-x", position.x);
        object.addProperty("layout-y", position.y);

        return object;
    }

    /**
     * @param object Json对象，包含了描述此控件属性的信息。
     * @throws IllegalArgumentException 如果参数为<code>null</code>或JsonObject内容为空
     */
    public void setupFromJson(JsonObject object) {
        if (object == null || object.isJsonNull()) {
            throw new IllegalArgumentException("Illegal json data.");
        }

        this.position.x = object.get("layout-x").getAsFloat();
        this.position.y = object.get("layout-y").getAsFloat();
    }

    /**
     * 将此控件的详细信息作为Json格式的字符串返回
     *
     * @return Json字符串
     */
    protected String toJsonString() {
        return getJsonObject().toString();
    }

    /**
     * 此控件的绘图方法，因为Gui库的实现并不是直接操作OpenGL，需要一些外部依赖，
     * 因此每个控件都需要实现自己的绘图方法
     *
     * @param offsetX 此控件在窗口中的X轴上的偏移值，通常等于<code>Gui</code>的position.x
     * @param offsetY 此控件在窗口中的Y轴上的偏移值，通常等于<code>Gui</code>的position.y
     */
    protected abstract void draw(float offsetX, float offsetY);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseControl control = (BaseControl) o;
        return position.equals(control.position) &&
               size.equals(control.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, size);
    }
}

