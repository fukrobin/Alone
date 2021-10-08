package per.alone.engine.component;

/**
 * component 的所有异常的父异常，通常表明一个组件的加载问题
 *
 * @author fkrobin
 * @date 2021/10/6 15:46
 */
public abstract class ComponentException extends RuntimeException {

    private static final long serialVersionUID = -3134318211128849161L;

    public ComponentException(String message) {
        super(message);
    }

    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}
