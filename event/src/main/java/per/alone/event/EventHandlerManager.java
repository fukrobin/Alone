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
public class EventHandlerManager implements EventDispatcher {
    private final Map<EventType<? extends Event>, CompositeEventHandler<? extends Event>> eventHandlerMap;

    public EventHandlerManager() {
        eventHandlerMap = new HashMap<>();
    }

    /**
     * 注册一个处理 eventType 类型事件的 EventHandler
     *
     * @param handler   事件处理器
     * @param eventType 事件类型
     * @param <T>       EventHandler 的特定事件类
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> handler) {
        Objects.requireNonNull(eventType, "Event type must not be null");
        Objects.requireNonNull(handler, "Event handler must not be null");

        CompositeEventHandler<T> eventHandler =
                (CompositeEventHandler<T>) eventHandlerMap.computeIfAbsent(eventType,
                                                                           type -> new CompositeEventHandler<>());
        eventHandler.addEventHandler(handler);
    }

    @Override
    public boolean canDispatch(EventType<? extends Event> eventType) {
        return eventHandlerMap.containsKey(eventType) &&
               eventHandlerMap.get(eventType).hashHandlers();
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
        final CompositeEventHandler<? extends Event> handler = eventHandlerMap.get(eventType);
        if (handler != null) {
            handler.dispatchEvent(event);
        }
    }
}
