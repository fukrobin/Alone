package per.alone.stage.input;

import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/21 1:17
 */
public class CharInputEvent extends Event {
    public static final EventType<CharInputEvent> ANY =
            new EventType<>(EventType.ROOT, "CHAR_INPUT");

    private final int codePoint;

    private final String text;

    public CharInputEvent(EventType<? extends Event> eventType, int codePoint) {
        super(eventType);
        this.codePoint = codePoint;
        text           = String.valueOf(Character.toChars(codePoint));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<CharInputEvent> getEventType() {
        return (EventType<CharInputEvent>) super.getEventType();
    }

    public int getCodePoint() {
        return codePoint;
    }

    public String getText() {
        return text;
    }
}
