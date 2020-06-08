package pers.crobin.engine.renderer;

import pers.crobin.engine.config.GuiContext;
import pers.crobin.engine.kernel.EngineThread;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.ui.BaseGui;
import pers.crobin.engine.ui.Canvas;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Administrator
 */
public class GuiRenderer extends BaseRenderer<List<BaseGui>> {
    private final GuiContext guiContext;

    public GuiRenderer(List<BaseGui> list) {
        try {
            this.guiContext = new GuiContext();
        } catch (IOException e) {
            throw new RuntimeException("The engine default font file is missing!", e);
        }
        this.target = list;
    }

    public void initialize() {
        guiContext.init();
        Canvas.setContext(guiContext.getContext());
    }

    @Override
    public void render() {
        Window window = EngineThread.getThreadWindow();
        guiContext.start(window);

        long              c        = guiContext.getContext();
        Iterator<BaseGui> iterator = target.iterator();
        while (iterator.hasNext()) {
            BaseGui gui = iterator.next();

            if (gui.isDisable()) {
                iterator.remove();
            } else if (gui.isVisible()) {
                gui.draw(c, window);
            }
        }

        guiContext.end();
    }

    @Override
    public void cleanup() {
        guiContext.cleanup();
    }
}
