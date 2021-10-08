package per.alone.engine.component;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Component 期望找到单个组件，但同类型的组件找到了多个时，将触发此异常
 *
 * @author fkrobin
 * @date 2021/10/6 15:54
 */
public class NoUniqueComponentException extends NoSuchComponentException {

    @Nullable
    private final Collection<String> beanNamesFound;

    public NoUniqueComponentException(Class<?> type) {
        super(type);
        this.beanNamesFound = null;
    }

    public NoUniqueComponentException(Class<?> type, Collection<String> beanNamesFound) {
        super(type, "expected single matching bean but found " + beanNamesFound.size() + ": " +
                    beanNamesFound);
        this.beanNamesFound = beanNamesFound;
    }

    public Collection<String> getBeanNamesFound() {
        return beanNamesFound;
    }
}
