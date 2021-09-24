package per.alone.engine.renderer;

import per.alone.engine.config.GuiContext;
import per.alone.engine.context.EngineContext;
import per.alone.engine.ui.control.Widgets;
import per.alone.stage.Window;

/**
 * @author Administrator
 */
public class GuiRenderer implements RendererComponent {
    private final GuiContext guiContext;

    private Widgets root;

    public GuiRenderer() {
        this.guiContext = new GuiContext();
    }

    public Widgets getRoot() {
        return root;
    }

    public void setRoot(Widgets root) {
        this.root = root;
    }

    @Override
    public void render(Window window, EngineContext engineContext) {
        guiContext.start(window);

        root.render(window, guiContext.getCanvas());

        guiContext.end();
    }

    @Override
    public void close() {
        guiContext.cleanup();
    }
}
