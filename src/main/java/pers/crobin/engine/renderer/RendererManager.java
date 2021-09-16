package pers.crobin.engine.renderer;

import pers.crobin.engine.IMemoryManager;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.util.GLHelp;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author fkobin
 * @date 2020/3/27 21:29
 * @Description
 **/
public class RendererManager implements IMemoryManager {
    /**
     * 用户自定义渲染器
     */
    private final List<BaseRenderer<?>> customRenderer;

    /**
     * Scene渲染器
     */
    private       SceneRenderer         sceneRenderer;

    /**
     * Gui渲染器
     */
    private       GuiRenderer           guiRenderer;

    public RendererManager() {
        customRenderer = new LinkedList<>();
    }

    public void init() {
        guiRenderer.initialize();
        sceneRenderer.initialize();

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

    public void bindGuiRenderer(GuiRenderer guiRenderer) {
        Objects.requireNonNull(guiRenderer);
        this.guiRenderer = guiRenderer;
    }

    public void bindSceneRenderer(SceneRenderer sceneRenderer) {
        Objects.requireNonNull(sceneRenderer);
        this.sceneRenderer = sceneRenderer;
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
    public void cleanup() {
        sceneRenderer.cleanup();
        guiRenderer.cleanup();

        customRenderer.forEach(BaseRenderer::cleanup);
    }
}
