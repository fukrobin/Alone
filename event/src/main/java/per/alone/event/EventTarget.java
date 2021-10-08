package per.alone.event;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/23 23:18
 */
public interface EventTarget {

    <T extends Event> CompositeAloneEventListener<T> buildEventHandlerChain(T event);
}
