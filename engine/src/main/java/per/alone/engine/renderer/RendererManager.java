package per.alone.engine.renderer;

import per.alone.engine.context.EngineContext;
import per.alone.engine.context.EngineContextEvent;
import per.alone.engine.context.SmartEngineContextListener;
import per.alone.engine.kernel.EngineComponent;
import per.alone.engine.util.GLHelp;
import per.alone.event.EventType;
import per.alone.stage.Window;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author fkobin
 * @date 2020/3/27 21:29
 **/
public class RendererManager implements EngineComponent, SmartEngineContextListener {
    /**
     * 用户自定义渲染器
     */
    private final List<BaseRenderer<?>> customRenderer;

    /**
     * Scene渲染器
     */
    private SceneRenderer sceneRenderer;

    /**
     * Gui渲染器
     */
    private GuiRenderer guiRenderer;

    public RendererManager() {
        customRenderer = new LinkedList<>();
    }

    @Override
    public boolean supportsEventType(EventType<? extends EngineContextEvent> eventType) {
        return eventType.equals(EngineContextEvent.PREPARED_ENGINE_CONTEXT);
    }


    @Override
    public void onEngineContextEvent(EngineContextEvent engineContextEvent) {
        EngineContext engineContext = engineContextEvent.getSource();
        sceneRenderer = engineContext.getSceneRenderer();
        guiRenderer   = engineContext.getGuiRenderer();

        sceneRenderer.initialize();
        guiRenderer.initialize();

        GLHelp.setGLState();
    }

    public void render(Window window) {
        GLHelp.frameBufferClearColor();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        window.resetViewport();

        sceneRenderer.render();

        customRenderer.forEach(BaseRenderer::render);

        guiRenderer.render();
    }

    /**
     * 添加一个自定义渲染器，自定义的渲染器会在Scene之后，Gui之前本渲染
     *
     * @param t   自定义的渲染器
     * @param <T> 渲染器必须继承自{@link BaseRenderer}
     */
    public <T extends BaseRenderer<?>> void addRenderer(T t) {
        Objects.requireNonNull(t);
        customRenderer.add(t);
    }

    public boolean removeRenderer(BaseRenderer<?> baseRenderer) {
        if (baseRenderer != null) {
            return customRenderer.remove(baseRenderer);
        }
        return false;
    }

    @Override
    public void close() {
        sceneRenderer.cleanup();
        guiRenderer.cleanup();

        customRenderer.forEach(BaseRenderer::cleanup);
    }

    @Override
    public void update(EngineContext engineContext) {

    }
}
