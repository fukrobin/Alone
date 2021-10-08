package per.alone.event;

import java.util.EventListener;
import java.util.List;

public interface AloneEventListener<T extends Event> extends EventListener {

    /**
     * 确定此侦听器支持给定的事件类型
     *
     * @return 可以处理的事件类型列表
     */
    List<EventType<T>> supportsEventTypes();

    /**
     * Invoked when a specific per.fkrobin.per.fkrobin.event of the type for which this handler is
     * registered happens.
     *
     * @param event the per.fkrobin.per.fkrobin.event which occurred
     */
    void onAloneEvent(T event);
}
