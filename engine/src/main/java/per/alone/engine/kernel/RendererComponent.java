package per.alone.engine.kernel;

import per.alone.engine.context.EngineContext;

/**
 * 渲染组件，默认在更新期间莫作任何操作
 *
 * @author fkrobin
 * @date 2021/9/19 22:33
 */
public interface RendererComponent extends EngineComponent {

    /**
     * 渲染组件通常不会在引擎更新期间处理逻辑
     *
     * @param engineContext 引擎上下文
     */
    @Override
    default void update(EngineContext engineContext) {

    }

    /**
     * 渲染
     *
     * @param engineContext 引擎上下文
     */
    void render(EngineContext engineContext);
}
