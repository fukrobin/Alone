package per.alone.engine.renderer;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import per.alone.engine.Ordered;
import per.alone.engine.annotation.AnnotationOrderComparator;
import per.alone.engine.annotation.Order;
import per.alone.engine.core.EngineContext;
import per.alone.engine.core.EngineContextEvent;
import per.alone.engine.core.EngineContextListener;
import per.alone.engine.util.GLHelp;
import per.alone.event.EventType;
import per.alone.stage.Window;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * 渲染管理器，自定义扩展点。渲染器都将按照 {@link AnnotationOrderComparator}
 * 进行排序
 *
 * @author fkobin
 * @date 2020/3/27 21:29
 **/
@Slf4j
@Singleton
public final class RendererManager implements RendererComponent, EngineContextListener {
    /**
     * 用户自定义渲染器
     */
    private final List<RendererComponent> rendererList;

    private boolean dirty = false;

    public RendererManager() {
        rendererList = new LinkedList<>();
    }

    @Override
    public List<EventType<EngineContextEvent>> supportsEventTypes() {
        return Collections.singletonList(EngineContextEvent.ENGINE_CONTEXT_LOADED);
    }

    @Override
    public void onEngineContextLoaded(EngineContextEvent engineContextEvent) {
        List<RendererComponent> components = engineContextEvent.getSource().getComponents(RendererComponent.class);
        for (RendererComponent component : components) {
            if (!(component instanceof RendererManager)) {
                addRenderer(component);
            }
        }

        GLHelp.setGLState();
    }

    public void render(Window window, EngineContext engineContext) {
        GLHelp.frameBufferClearColor();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        window.resetViewport();

        sortRendererIfNecessary();

        for (RendererComponent rendererComponent : rendererList) {
            rendererComponent.render(window, engineContext);
        }
    }

    /**
     * 添加一个自定义渲染器，自定义的渲染器会在Scene之后，Gui之前本渲染
     *
     * @param renderer 渲染器
     */
    public void addRenderer(RendererComponent renderer) {
        Objects.requireNonNull(renderer);
        if (this.rendererList.contains(renderer)) {
            throw new IllegalStateException("重复添加 RenderComponent");
        }
        this.rendererList.add(renderer);
        this.dirty = true;
    }

    public boolean removeRenderer(RendererComponent renderer) {
        if (renderer != null) {
            return this.rendererList.remove(renderer);
        }
        return false;
    }

    /**
     * 如果 renderer list 是脏的，则排序。
     * 排序根据 {@link Ordered#getOrder()} 返回值或 {@link Order}
     * 注解的值进行排序，值越小，表示优先级越高
     */
    private void sortRendererIfNecessary() {
        if (!dirty) {
            return;
        }

        AnnotationOrderComparator.sort(rendererList);
        dirty = false;
    }

    @Override
    public void close() {

    }
}
