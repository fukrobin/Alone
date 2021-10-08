package per.alone.event;

import per.alone.common.exception.MultiException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 复合的 EventHandler。分发事件到 eventHandlerSet 中的所有 EventHandler
 *
 * @author fkrobin
 * @date 2021/9/17 0:11
 */
public class CompositeAloneEventListener<T extends Event> {
    private final Set<AloneEventListener<? super T>> aloneEventListeners;

    private AloneEventListener<? super T> aloneEventListener;

    public CompositeAloneEventListener() {
        this.aloneEventListeners = new HashSet<>();
    }

    public AloneEventListener<? super T> getEventListener() {
        return aloneEventListener;
    }

    public void setEventListener(final AloneEventListener<? super T> aloneEventListener) {
        this.aloneEventListener = aloneEventListener;
    }

    public Set<AloneEventListener<? super T>> getEventListeners() {
        return Collections.unmodifiableSet(aloneEventListeners);
    }

    public void dispatchEvent(Event event) {
        final T specificEvent = (T) event;

        MultiException multiException = null;
        for (AloneEventListener<? super T> aloneEventListener : aloneEventListeners) {

            try {
                aloneEventListener.onAloneEvent(specificEvent);
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

    public void addEventListener(final AloneEventListener<? super T> aloneEventListener) {
        aloneEventListeners.add(aloneEventListener);
    }

    public void removeEventListener(final AloneEventListener<? super T> aloneEventListener) {
        aloneEventListeners.remove(aloneEventListener);
    }

    public boolean hasListeners() {
        return aloneEventListeners.size() > 0;
    }
}
