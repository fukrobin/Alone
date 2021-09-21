package per.alone.engine.renderer;

import per.alone.engine.config.GuiContext;
import per.alone.engine.context.EngineContext;
import per.alone.engine.ui.BaseGui;
import per.alone.stage.Window;

import java.io.IOException;
import java.util.*;

/**
 * @author Administrator
 */
public class GuiRenderer implements RendererComponent {
    private final GuiContext guiContext;

    private final List<BaseGui> guiList;

    public GuiRenderer() {
        this.guiContext = new GuiContext();
        this.guiList    = new ArrayList<>();
    }

    @Override
    public void render(Window window, EngineContext engineContext) {
        guiContext.start(window);

        Iterator<BaseGui> iterator = guiList.iterator();
        while (iterator.hasNext()) {
            BaseGui gui = iterator.next();

            if (gui.isDisable()) {
                iterator.remove();
            } else if (gui.isVisible()) {
                gui.draw(window, guiContext.getCanvas());
            }
        }

        guiContext.end();
    }

    public void addGui(BaseGui gui) {
        if (!guiList.contains(gui)) {
            guiList.add(gui);
        }
    }

    public void addAll(BaseGui... guis) {
        for (BaseGui gui : guis) {
            addGui(gui);
        }
    }

    public void addAll(Collection<BaseGui> collection) {
        guiList.addAll(collection);
    }

    public boolean remove(BaseGui gui) {
        return guiList.remove(gui);
    }

    public boolean removeAll(BaseGui... guis) {
        boolean flag = true;
        for (BaseGui gui : guis) {
            if (!remove(gui)) {
                flag = false;
            }
        }

        return flag;
    }

    public boolean removeAll(Collection<BaseGui> collection) {
        return guiList.removeAll(collection);
    }

    public List<BaseGui> getGuiList() {
        return guiList;
    }

    /**
     * 寻找b并返回第一个可见且未被禁用的Gui
     *
     * @return 第一个可见且未被禁用的Gui
     */
    public BaseGui getActiveGui() {
        BaseGui gui1 = null;
        for (BaseGui baseGui : guiList) {
            gui1 = baseGui;
            if (!gui1.isDisable() && gui1.isVisible()) {
                break;
            }
        }
        return gui1;
    }

    /**
     * @param gui 设置指定的{@link BaseGui}为当前活动Gui
     * @return 如果指定的Gui可见并且未被禁用，返回<code>true</code>；否则返回<code>false</code>
     */
    public boolean setActiveGui(BaseGui gui) {
        Objects.requireNonNull(gui);
        if (!gui.isDisable() && gui.isVisible()) {
            guiList.remove(gui);
            guiList.add(gui);
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        guiContext.cleanup();
        for (BaseGui baseGui : guiList) {
            try {
                baseGui.close();
            } catch (IOException ignored) {

            }
        }
    }
}
