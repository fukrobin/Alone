package per.alone.event;

import lombok.extern.slf4j.Slf4j;
import per.alone.common.exception.MultiException;

import java.util.HashSet;
import java.util.Set;

/**
 * 复合的 EventHandler。分发事件到 eventHandlerSet 中的所有 EventHandler
 *
 * @author fkrobin
 * @date 2021/9/17 0:11
 */
@Slf4j
public class CompositeEventHandler<T extends Event> {
    private final Set<EventHandler<? super T>> eventHandlerSet;

    public CompositeEventHandler() {
        this.eventHandlerSet = new HashSet<>();
    }

    public void dispatchEvent(Event event) {
        final T specificEvent = (T) event;

        MultiException multiException = null;
        for (EventHandler<? super T> eventHandler : eventHandlerSet) {

            try {
                eventHandler.handle(specificEvent);
            } catch (Exception e) {
                if (multiException == null) {
                    multiException = new MultiException();
                }
                multiException.addException(e);
            }
        }

        if (multiException != null) {
            throw multiException;
        }
    }

    public void addEventHandler(final EventHandler<? super T> eventHandler) {
        eventHandlerSet.add(eventHandler);
    }

    public boolean hashHandlers() {
        return eventHandlerSet.size() > 0;
    }
}
