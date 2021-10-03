package per.alone.engine.annotation;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/20 18:44
 */
public @interface EngineComponent {
    /**
     * 组件的名称，组件应该有一个独一无二的名称
     *
     * @return {@link String}
     */
    String value();

    /**
     * 在引擎更新期间是否可更新
     *
     * @return boolean
     */
    boolean updatable() default true;

    /**
     * 是否是可渲染的
     *
     * @return boolean
     */
    boolean renderable() default false;
}
