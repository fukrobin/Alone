package per.alone.event;

import java.util.Iterator;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * 此类表示与 Event关联的特定事件类型
 *
 * @author fkrobin
 * @date 2021/9/16 15:25
 */
public class EventType<T extends Event> {

    public static final EventType<Event> ROOT = new EventType<>("EVENT", null);

    private final EventType<? super T> superType;

    private final String name;

    private WeakHashMap<EventType<? extends T>, Void> subTypes;

    /**
     * 构造一个具有 super type 为 {@link EventType#ROOT}
     * 且名称设置为 null的新 EventType
     *
     * @param name 事件类型的 name
     */
    public EventType(String name) {
        this(ROOT, name);
    }

    /**
     * 构造一个具有指定超类型且名称设置为null的新EventType
     *
     * @param superType 事件的  superType
     */
    public EventType(final EventType<? super T> superType) {
        this(superType, null);
    }

    /**
     * 构造一个具有指定 super type 和 name 的 EventType
     *
     * @param superType 事件的 super type
     * @param name      事件的 name
     */
    public EventType(final EventType<? super T> superType,
                     final String name) {
        if (superType == null) {
            throw new NullPointerException(
                    "Event super type must not be null!");
        }

        this.superType = superType;
        this.name      = name;
        superType.register(this);
    }

    @SuppressWarnings("rawtypes")
    EventType(final String name,
              final EventType<? super T> superType) {
        this.superType = superType;
        this.name      = name;
        if (superType != null) {
            if (superType.subTypes != null) {
                for (Iterator i = superType.subTypes.keySet().iterator(); i.hasNext(); ) {
                    EventType t = (EventType) i.next();
                    boolean nameExists = name == null && t.name == null || (name != null && name.equals(t.name));
                    if (nameExists) {
                        i.remove();
                    }
                }
            }
            superType.register(this);
        }
    }

    public final EventType<? super T> getSuperType() {
        return superType;
    }

    public final String getName() {
        return name;
    }

    private void register(EventType<? extends T> subType) {
        if (subTypes == null) {
            subTypes = new WeakHashMap<>();
        }
        for (EventType<? extends T> t : subTypes.keySet()) {
            boolean nameExists = (t.name == null && subType.name == null) ||
                                 (t.name != null && t.name.equals(subType.name));
            if (nameExists) {
                throw new IllegalArgumentException(
                        String.format("EventType %s with parent %s already exists",
                                      subType,
                                      subType.getSuperType()));
            }
        }
        subTypes.put(subType, null);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventType<?> eventType = (EventType<?>) o;
        return Objects.equals(superType, eventType.superType) && Objects.equals(name, eventType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superType, name);
    }
}
