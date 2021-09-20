package per.alone.engine.kernel;

import per.alone.engine.context.EngineContext;

import java.io.Closeable;

/**
 * 引擎组件的抽象，抽象了组件的通用操作，如再引擎更新期间的 update() 方法
 *
 * @author fkrobin
 * @date 2021/9/17 18:34
 */
public interface EngineComponent extends Closeable {

    /**
     * 组件名称，每个组件都应该有一个全局唯一的名称
     *
     * @return {@link String}
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 在引擎更新期间被调用，可能会被连续调用多次
     *
     * @param engineContext 引擎上下文
     */
    void update(EngineContext engineContext);

}
