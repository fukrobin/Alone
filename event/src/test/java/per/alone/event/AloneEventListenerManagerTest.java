package per.alone.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AloneEventListenerManagerTest {

    private EventListenerManager eventListenerManager;

    @BeforeEach
    void init() {
        if (eventListenerManager == null) {
            eventListenerManager = new EventListenerManager();
        }
    }

    @Test
    void dispatchEvent() {
        TestEvent event = new TestEvent(TestEvent.TEST_EVENT_EVENT_TYPE);
        eventListenerManager.addEventListener((TestEventListener) event1 -> {
            assertNotNull(event1);
            assertNull(event1.source);
            assertEquals(event1.getEventType(), TestEvent.TEST_EVENT_EVENT_TYPE);
        });

        eventListenerManager.dispatchEvent(event);
    }

    private interface TestEventListener extends AloneEventListener<TestEvent> {

        @Override
        default List<EventType<TestEvent>> supportsEventTypes() {
            return Collections.singletonList(TestEvent.TEST_EVENT_EVENT_TYPE);
        }
    }
}