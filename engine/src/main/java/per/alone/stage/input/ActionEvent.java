package per.alone.stage.input;

import per.alone.event.Event;
import per.alone.event.EventType;

/**
 * @author Administrator
 */
public class ActionEvent extends Event {
    public static final EventType<ActionEvent> ACTION = new EventType<>("ACTION");

    public static final EventType<ActionEvent> ANY = ACTION;

    public ActionEvent() {
        super(ACTION);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends ActionEvent> getEventType() {
        return (EventType<? extends ActionEvent>) super.getEventType();
    }
}
