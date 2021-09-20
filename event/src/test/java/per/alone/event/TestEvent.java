package per.alone.event;

/**
 * TestEvent
 *
 * @author fkrobin
 * @date 2021/9/19 17:58
 */
public class TestEvent extends Event {

    public static final EventType<TestEvent> TEST_EVENT_EVENT_TYPE =
            new EventType<>(EventType.ROOT, "TEST_EVENT_EVENT_TYPE");

    public TestEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
