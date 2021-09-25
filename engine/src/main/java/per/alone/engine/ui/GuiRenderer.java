package per.alone.engine.ui;

import per.alone.engine.config.GuiContext;
import per.alone.engine.context.EngineContext;
import per.alone.engine.renderer.RendererComponent;
import per.alone.stage.Window;

/**
 * @author Administrator
 */
public class GuiRenderer implements RendererComponent {
    private final GuiContext guiContext;

    private SimpleScene scene;

    public GuiRenderer() {
        this.guiContext = new GuiContext();
    }

    public SimpleScene getScene() {
        return scene;
    }

    public void setScene(SimpleScene scene) {
        this.scene = scene;
    }

    @Override
    public void render(Window window, EngineContext engineContext) {
        guiContext.start(window);

        scene.getRoot().render(guiContext.getCanvas());

        guiContext.end();
    }

    @Override
    public void close() {
        guiContext.cleanup();
    }
}
