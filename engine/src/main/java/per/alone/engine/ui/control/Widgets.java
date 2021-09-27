package per.alone.engine.ui.control;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import per.alone.engine.geometry.BoundingBox;
import per.alone.engine.geometry.Bounds;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.SimpleScene;
import per.alone.event.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * * @author fukrobin
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

    protected SimpleScene scene;

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
        this.size     = new Vector2f();
        this.visible  = true;
        this.parent   = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> CompositeEventHandler<T> buildEventHandlerChain(T event) {
        CompositeEventHandler<T> temp = new CompositeEventHandler<>();
        Widgets cur = this;
        do {
            CompositeEventHandler<T> compositeEventHandler = (CompositeEventHandler<T>) cur.eventHandlerMap.get(
                    event.getEventType());
            Set<EventHandler<? super T>> handlerSet = compositeEventHandler.getEventHandlers();
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

    @SuppressWarnings("unchecked")
    protected final <T extends Event> void setEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        CompositeEventHandler<T> compositeEventHandler =
                (CompositeEventHandler<T>) eventHandlerMap.computeIfAbsent(eventType,
                                                                           eventType1 -> new CompositeEventHandler<>());
        compositeEventHandler.setEventHandler(eventHandler);
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

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
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

    public float getXInScene() {
        float x = position.x;
        if (parent != null) {
            x += parent.getXInScene();
        }
        return x;
    }

    public float getYInScene() {
        float y = position.y;
        if (parent != null) {
            y += parent.getYInScene();
        }
        return y;
    }

    public final void render(Canvas canvas) {
        // 平移坐标系，以适应 scene 在 window 中的偏移
        canvas.translate(scene.getX() + getXInScene(), scene.getY() + getYInScene());

        draw(canvas);
    }

    /**
     * 此控件的绘图方法，因为Gui库的实现并不是直接操作OpenGL，需要一些外部依赖，
     * 因此每个控件都需要实现自己的绘图方法
     */
    protected abstract void draw(Canvas canvas);

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

