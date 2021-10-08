package per.alone.engine.exception;

import per.alone.engine.component.ComponentException;

/**
 * 组件加载异常
 *
 * @author fkrobin
 * @date 2021/9/21 21:31
 */
public class ComponentLoadException extends ComponentException {

    private static final long serialVersionUID = 4414570085505740618L;

    public ComponentLoadException(String name) {
        super("Component '" + name + "' has an exception during the initialization phase");
    }

    public ComponentLoadException(String name, Throwable cause) {
        super("Component '" + name + "' has an exception during the initialization phase", cause);
    }
}
