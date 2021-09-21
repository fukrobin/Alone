package per.alone.engine.exception;

/**
 * 组件加载异常
 *
 * @author fkrobin
 * @date 2021/9/21 21:31
 */
public class ComponentLoadException extends RuntimeException {

    private static final long serialVersionUID = 4414570085505740618L;

    public ComponentLoadException(String message) {
        super(message);
    }

    public ComponentLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
