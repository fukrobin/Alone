package per.alone.stage.input;

import lombok.Getter;
import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * @author Administrator
 */
@Getter
public class KeyEvent extends Event {


    /**
     * Common supertype for all key per.fkrobin.per.fkrobin.event types.
     */
    public static final EventType<KeyEvent> ANY =
            new EventType<>(Event.ANY, "KEY");

    /**
     * This per.fkrobin.per.fkrobin.event occurs when a key has been pressed.
     */
    public static final EventType<KeyEvent> KEY_PRESSED =
            new EventType<>(KeyEvent.ANY, "KEY_PRESSED");

    /**
     * This per.fkrobin.per.fkrobin.event occurs when a key has been released.
     */
    public static final EventType<KeyEvent> KEY_RELEASED =
            new EventType<>(KeyEvent.ANY, "KEY_RELEASED");

    /**
     * This per.fkrobin.per.fkrobin.event occurs when a key has been repeated.
     */
    public static final EventType<KeyEvent> KEY_REPEAT   =
            new EventType<>(KeyEvent.ANY, "KEY_REPEAT");

    private final KeyCode code;

    private final String text;

    private final boolean shiftDown;

    private final boolean controlDown;

    private final boolean altDown;

    public KeyEvent(EventType<KeyEvent> eventType,
                    KeyCode code,
                    boolean shiftDown,
                    boolean controlDown,
                    boolean altDown) {
        super(eventType);
        this.code        = code;
        this.text        = code.name;
        this.shiftDown   = shiftDown;
        this.controlDown = controlDown;
        this.altDown     = altDown;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<KeyEvent> getEventType() {
        return (EventType<KeyEvent>) super.getEventType();
    }
}
