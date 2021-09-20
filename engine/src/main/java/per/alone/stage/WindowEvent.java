package per.alone.stage;

import per.alone.event.Event;
import per.alone.event.EventType;

import java.util.StringJoiner;

/**
 * 与 Window 尺寸、show、hide 等更改相关的事件
 *
 * @author fkrobin
 * @date 2021/9/17 12:27
 */
public class WindowEvent extends Event {

    public static final EventType<WindowEvent> ANY =
            new EventType<>(Event.ANY, "WINDOW");

    /**
     * This per.fkrobin.per.fkrobin.event occurs on window just before it is shown.
     */
    public static final EventType<WindowEvent> WINDOW_SHOWING =
            new EventType<>(WindowEvent.ANY, "WINDOW_SHOWING");

    /**
     * This per.fkrobin.per.fkrobin.event occurs on window just after it is shown.
     */
    public static final EventType<WindowEvent> WINDOW_SHOWN =
            new EventType<>(WindowEvent.ANY, "WINDOW_SHOWN");

    /**
     * This per.fkrobin.per.fkrobin.event occurs on window just before it is hidden.
     */
    public static final EventType<WindowEvent> WINDOW_HIDING =
            new EventType<>(WindowEvent.ANY, "WINDOW_HIDING");

    /**
     * This per.fkrobin.per.fkrobin.event occurs on window just after it is hidden.
     */
    public static final EventType<WindowEvent> WINDOW_HIDDEN =
            new EventType<>(WindowEvent.ANY, "WINDOW_HIDDEN");

    /**
     * 当有外部请求关闭该窗口时，此事件将传递到该窗口。
     * 如果任何已安装的窗口事件处理程序都没有使用该事件，则此事件的默认处理程序将关闭相应的窗口。
     */
    public static final EventType<WindowEvent> WINDOW_CLOSE_REQUEST =
            new EventType<>(WindowEvent.ANY, "WINDOW_CLOSE_REQUEST");

    /**
     * 当 Window 的尺寸改变时触发此事件类型
     */
    public static final EventType<WindowEvent> WINDOW_SIZE_CHANGE =
            new EventType<>(WindowEvent.ANY, "WINDOW_SIZE_CHANGE");

    public static final EventType<WindowEvent> WINDOW_FRAMEBUFFER_SIZE_CHANGE =
            new EventType<>(WindowEvent.ANY, "WINDOW_FRAMEBUFFER_SIZE_CHANGE");

    /**
     * 当 Window 获得焦点时触发的事件
     */
    public static final EventType<WindowEvent> WINDOW_FOCUS =
            new EventType<>(WindowEvent.ANY, "WINDOW_FOCUS");

    /**
     * 当 Window 最大化时触发的事件
     */
    public static final EventType<WindowEvent> WINDOW_MAXIMIZE =
            new EventType<>(WindowEvent.ANY, "WINDOW_MAXIMIZE");

    /**
     * 当 Window 图标化（最小化）时触发的事件
     */
    public static final EventType<WindowEvent> WINDOW_ICONIFY =
            new EventType<>(WindowEvent.ANY, "WINDOW_ICONIFY");

    public WindowEvent(WindowData source, EventType<? extends Event> eventType) {
        super(source, eventType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WindowEvent.class.getSimpleName() + "[", "]")
                .add("eventType=" + eventType)
                .add("source=" + source)
                .toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<WindowEvent> getEventType() {
        return (EventType<WindowEvent>) super.getEventType();
    }
}
