package per.alone.event;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/23 23:13
 */
public class EventUtil {

    public static <T extends Event> void fireEvent(T event, EventTarget eventTarget) {
        CompositeEventHandler<T> handler = eventTarget.buildEventHandlerChain(event);
        handler.dispatchEvent(event);
    }

}
