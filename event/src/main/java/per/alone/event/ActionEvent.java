package per.alone.event;

/**
 * @author Administrator
 */
public class ActionEvent extends Event {
    public static final EventType<ActionEvent> ACTION = new EventType<ActionEvent>("ACTION");

    public static final EventType<ActionEvent> ANY    = ACTION;

    public ActionEvent() {
        super(ACTION);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends ActionEvent> getEventType() {
        return (EventType<? extends ActionEvent>) super.getEventType();
    }
}
