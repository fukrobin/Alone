package per.alone.engine.ui.control;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import per.alone.engine.geometry.BoundingBox;
import per.alone.engine.geometry.Bounds;
import per.alone.engine.ui.Canvas;
import per.alone.event.*;
import per.alone.stage.Window;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 ** @author fukrobin
 */
@Getter
@Setter
public abstract class Widgets implements EventTarget {
    protected static final NVGColor RESULT = NVGColor.create();

    /**
     * 在Gui中的偏移位置，而不是在整个窗口中的位置
     */
    protected final Vector2f position;

    /**
     * 此控件的尺寸
     */
    protected final Vector2f size;

    /**
     * 此控件的父控件
     */
    protected Parent parent;

    /**
     * 可见性，只有可见的 {@link Widgets} 才能被渲染
     */
    protected boolean visible;

    private Map<EventType<? extends Event>, CompositeEventHandler<? extends Event>> eventHandlerMap;

    protected Widgets() {
        this.position = new Vector2f();
        this.size = new Vector2f();
        this.visible = true;
        this.parent = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> CompositeEventHandler<T> buildEventHandlerChain(T event) {
        CompositeEventHandler<T> temp = new CompositeEventHandler<>();
        Widgets cur = this;
        do {
            Set<EventHandler<? super T>> handlerSet =
                    (Set<EventHandler<? super T>>) cur.eventHandlerMap.get(event.getEventType())
                                                                      .getEventHandlers();
            for (EventHandler<? super T> handler : handlerSet) {
                temp.addEventHandler(handler);
            }
            cur = cur.getParent();
        } while (cur != null);
        return temp;
    }

    @SuppressWarnings("unchecked")
    public final <T extends Event> void addEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        CompositeEventHandler<T> handler =
                (CompositeEventHandler<T>) eventHandlerMap.computeIfAbsent(eventType,
                                                                           type -> new CompositeEventHandler<T>());
        handler.addEventHandler(eventHandler);
    }

    @SuppressWarnings("unchecked")
    public final <T extends Event> void removeEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        if (eventHandlerMap.containsKey(eventType)) {
            CompositeEventHandler<T> handler = (CompositeEventHandler<T>) eventHandlerMap.get(eventType);
            handler.removeEventHandler(eventHandler);
        }
    }

    //////////////////////////
    /// Position
    //////////////////////////

    public void setPosition(Vector2f position) {
        setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    ///////////////////////////////
    /// Size
    ///////////////////////////////

    public void setSize(Vector2f size) {
        setSize(size.x, size.y);
    }

    public void setSize(float width, float height) {
        this.size.set(width, height);
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

    public final void render(Window window, Canvas canvas) {

    }

    /**
     * 此控件的绘图方法，因为Gui库的实现并不是直接操作OpenGL，需要一些外部依赖，
     * 因此每个控件都需要实现自己的绘图方法
     *
     * @param offsetX 此控件在窗口中的X轴上的偏移值，通常等于<code>Gui</code>的position.x
     * @param offsetY 此控件在窗口中的Y轴上的偏移值，通常等于<code>Gui</code>的position.y
     */
    protected abstract void draw(float offsetX, float offsetY, Canvas canvas);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Widgets control = (Widgets) o;
        return position.equals(control.position) &&
                size.equals(control.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, size);
    }
}

