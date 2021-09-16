package pers.crobin.engine.event;

import java.util.Objects;

/**
 * 此类表示与 Event关联的特定事件类型
 *
 * @author fkrobin
 * @date 2021/9/16 15:25
 */
public class EventType<T extends IEvent> {

    private final String name;

    public EventType(String name) {
        Objects.requireNonNull(name, "Event type name can't be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
