package per.alone.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventHandlerManagerTest {

    private EventHandlerManager eventHandlerManager;

    @BeforeEach
    void init() {
        if (eventHandlerManager == null) {
            eventHandlerManager = new EventHandlerManager();
        }
    }

    @Test
    void dispatchEvent() {
        TestEvent event = new TestEvent(TestEvent.TEST_EVENT_EVENT_TYPE);
        eventHandlerManager.addEventHandler(TestEvent.TEST_EVENT_EVENT_TYPE, event1 -> {
            assertNotNull(event1);
            assertNull(event1.source);
            assertEquals(event1.getEventType(), TestEvent.TEST_EVENT_EVENT_TYPE);
        });

        eventHandlerManager.dispatchEvent(event);
    }
}