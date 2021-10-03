package per.alone.engine.annotation;

import per.alone.engine.Ordered;

import java.lang.annotation.*;

/**
 * 被解释为优先级，具有最低顺序值的优先级最高
 *
 * @author fkrobin
 * @date 2021/10/3 17:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {
    /**
     * The order value.
     * <p>Default is {@link Ordered#LOWEST_PRECEDENCE}.
     *
     * @see Ordered#getOrder()
     */
    int value() default Ordered.LOWEST_PRECEDENCE;
}
