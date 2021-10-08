package per.alone.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/1 11:54
 **/
public class EventListenerManager implements EventDispatcher {
    private final Map<EventType<? extends Event>, CompositeAloneEventListener<? extends Event>> eventHandlerMap;

    public EventListenerManager() {
        eventHandlerMap = new HashMap<>();
    }

    /**
     * 注册一个处理 eventType 类型事件的 EventHandler
     *
     * @param listener  事件处理器
     * @param eventType 事件类型
     * @param <T>       EventHandler 的特定事件类
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void addEventListener(EventType<T> eventType, AloneEventListener<T> listener) {
        Objects.requireNonNull(eventType, "Event type must not be null");
        Objects.requireNonNull(listener, "Event listener must not be null");

        CompositeAloneEventListener<T> eventHandler =
                (CompositeAloneEventListener<T>) eventHandlerMap.computeIfAbsent(eventType,
                                                                                 type -> new CompositeAloneEventListener<>());
        eventHandler.addEventListener(listener);
    }

    public <T extends Event> void addEventListener(AloneEventListener<T> listener) {
        Objects.requireNonNull(listener, "Event listener must not be null");

        for (EventType<T> eventType : listener.supportsEventTypes()) {
            addEventListener(eventType, listener);
        }
    }

    @Override
    public boolean canDispatch(EventType<? extends Event> eventType) {
        return eventHandlerMap.containsKey(eventType) &&
               eventHandlerMap.get(eventType).hasListeners();
    }

    @Override
    public void dispatchEvent(Event event) {
        EventType<? extends Event> eventType = event.getEventType();
        do {
            dispatchEvent(event, eventType);
            eventType = eventType.getSuperType();
        } while (eventType != null);
    }

    private void dispatchEvent(Event event, EventType<? extends Event> eventType) {
        final CompositeAloneEventListener<? extends Event> handler = eventHandlerMap.get(eventType);
        if (handler != null) {
            handler.dispatchEvent(event);
        }
    }
}
