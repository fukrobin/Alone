package per.alone.engine.component;

/**
 * 当 ComponentFactory 无法找到一个组件时抛出此异常
 *
 * @author fkrobin
 * @date 2021/10/6 15:49
 */
public class NoSuchComponentException extends ComponentException {

    private final String componentName;

    private final Class<?> type;

    public NoSuchComponentException(String name) {
        super("No component named '" + name + "' available");
        this.componentName = name;
        this.type          = null;
    }

    public NoSuchComponentException(Class<?> type) {
        super("No qualifying component of type '" + type + "' available");
        this.componentName = null;
        this.type          = type;
    }

    public NoSuchComponentException(Class<?> type, String message) {
        super("No qualifying component of type '" + type + "' available: " + message);
        this.componentName = null;
        this.type          = type;
    }

    public String getComponentName() {
        return componentName;
    }

    public Class<?> getType() {
        return type;
    }
}
