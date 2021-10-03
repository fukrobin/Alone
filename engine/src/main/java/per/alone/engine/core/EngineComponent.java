package per.alone.engine.core;

import java.io.Closeable;

/**
 * 引擎组件的抽象，抽象了组件的通用操作，如再引擎更新期间的 update() 方法
 *
 * @author fkrobin
 * @date 2021/9/17 18:34
 */
public interface EngineComponent extends Closeable {

    String INTERNAL_GUI_RENDERER    = "internal_gui_renderer";
    String INTERNAL_RENDERER        = "internal_renderer";
    String INTERNAL_SCENE_RENDERER  = "internal_scene_renderer";
    String INTERNAL_SOUND_COMPONENT = "internal_sound_component";

    /**
     * 组件名称，每个组件都应该有一个全局唯一的名称
     *
     * @return {@link String}
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    @Override
    void close();

    /**
     * 在引擎更新期间被调用，可能会被连续调用多次
     *
     * @param engineContext 引擎上下文
     */
    void update(EngineContext engineContext);
}
