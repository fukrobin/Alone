package per.alone.engine.renderer;

import lombok.extern.slf4j.Slf4j;
import per.alone.engine.context.EngineContext;
import per.alone.engine.context.EngineContextEvent;
import per.alone.engine.context.SmartEngineContextListener;
import per.alone.engine.ui.GuiRenderer;
import per.alone.engine.util.GLHelp;
import per.alone.event.EventType;
import per.alone.stage.Window;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author fkobin
 * @date 2020/3/27 21:29
 **/
@Slf4j
public class CompositeRenderer implements RendererComponent, SmartEngineContextListener {
    /**
     * 用户自定义渲染器
     */
    private final List<RendererComponent> rendererList;

    /**
     * Scene渲染器
     */
//    private SceneRenderer sceneRenderer;  todo

    /**
     * Gui渲染器
     */
    private GuiRenderer guiRenderer;

    public CompositeRenderer() {
        rendererList = new LinkedList<>();
    }

    @Override
    public boolean supportsEventType(EventType<? extends EngineContextEvent> eventType) {
        return eventType.equals(EngineContextEvent.PREPARED_ENGINE_CONTEXT);
    }

    @Override
    public void onEngineContextEvent(EngineContextEvent engineContextEvent) {
        EngineContext engineContext = engineContextEvent.getSource();
//        sceneRenderer = engineContext.getSceneRenderer();
        guiRenderer = engineContext.getGuiRenderer();

//        sceneRenderer.initialize();

        GLHelp.setGLState();
    }

    @Override
    public void render(Window window, EngineContext engineContext) {
        GLHelp.frameBufferClearColor();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        window.resetViewport();

//        sceneRenderer.render(window, engineContext);

        for (RendererComponent rendererComponent : rendererList) {
            rendererComponent.render(window, engineContext);
        }

        guiRenderer.render(window, engineContext);
    }

    /**
     * 添加一个自定义渲染器，自定义的渲染器会在Scene之后，Gui之前本渲染
     *
     * @param renderer 渲染器
     */
    public void addRenderer(RendererComponent renderer) {
        Objects.requireNonNull(renderer);
        this.rendererList.add(renderer);
    }

    public boolean removeRenderer(RendererComponent renderer) {
        if (renderer != null) {
            return this.rendererList.remove(renderer);
        }
        return false;
    }

    @Override
    public void close() {
//        sceneRenderer.close();
        guiRenderer.close();

        for (RendererComponent rendererComponent : rendererList) {
            try {
                rendererComponent.close();
            } catch (IOException e) {
                log.warn("Renderer[{}] exception occurred during close: {}", rendererComponent.getName(), e);
            }
        }
    }
}
