package per.alone.stage.input;

import lombok.Getter;
import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * @author Administrator
 */
@Getter
public class MouseEvent extends Event {

    public static final EventType<MouseEvent> ANY = new EventType<>("MOUSE");

    public static final EventType<MouseEvent> MOUSE_PRESSED = new EventType<>("MOUSE_PRESSED");

    public static final EventType<MouseEvent> MOUSE_RELEASED = new EventType<>("MOUSE_RELEASED");

    public static final EventType<MouseEvent> MOUSE_MOVED = new EventType<>(MouseEvent.ANY, "MOUSE_MOVED");

    /**
     * 鼠标相对于窗口的水平位置
     */
    private final       double                x;

    /**
     * 鼠标相对于窗口垂直位置
     */
    private final double y;

    /**
     * 鼠标相对于显示器的水平位置
     */
    private final double screenX;

    /**
     * 鼠标相对于显示器的垂直位置
     */
    private final double screenY;

    /**
     * 触发事件的 MouseButton
     */
    private final MouseButton button;

    /**
     * 是否按下了 shift 键
     */
    private final boolean shiftDown;

    /**
     * 是否按下了control键
     */
    private final boolean controlDown;

    /**
     * 是否按下了 alt 键
     */
    private final boolean altDown;

    /**
     * 是否按下了 {@link MouseButton#BUTTON_LEFT}
     */
    private final boolean leftButtonDown;

    /**
     * 是否按下了 {@link MouseButton#BUTTON_MIDDLE}
     */
    private final boolean middleButtonDown;

    /**
     * 是否按下了 {@link MouseButton#BUTTON_RIGHT}
     */
    private final boolean rightButtonDown;

    /**
     * 鼠标是否被隐藏
     */
    private final boolean hiddenCursor;

    /**
     * 鼠标是否还在窗口中
     */
    private final boolean inWindow;

    public MouseEvent(EventType<? extends MouseEvent> eventType,
                      double x,
                      double y,
                      double screenX,
                      double screenY,
                      MouseButton button,
                      boolean shiftDown,
                      boolean controlDown,
                      boolean altDown,
                      boolean leftButtonDown,
                      boolean middleButtonDown,
                      boolean rightButtonDown,
                      boolean hiddenCursor,
                      boolean inWindow) {
        super(eventType);
        this.x                = x;
        this.y                = y;
        this.screenX          = screenX;
        this.screenY          = screenY;
        this.button           = button;
        this.shiftDown        = shiftDown;
        this.controlDown      = controlDown;
        this.altDown          = altDown;
        this.leftButtonDown   = leftButtonDown;
        this.middleButtonDown = middleButtonDown;
        this.rightButtonDown  = rightButtonDown;
        this.hiddenCursor     = hiddenCursor;
        this.inWindow         = inWindow;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends MouseEvent> getEventType() {
        return (EventType<? extends MouseEvent>) super.getEventType();
    }
}
