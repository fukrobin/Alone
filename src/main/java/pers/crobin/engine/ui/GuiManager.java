package pers.crobin.engine.ui;

import pers.crobin.engine.IMemoryManager;
import pers.crobin.engine.ui.preset.GuiDebug;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
public class GuiManager implements IMemoryManager {

    /**
     * {@link BaseGui}的链表，表头为当前活动Gui，只有当前活动的Gui才会处理事件
     */
    private final LinkedList<BaseGui> guiList;

    public GuiManager() {
        guiList = new LinkedList<>();
    }

    public void init() {
        addGui(new GuiDebug());
    }

    public void start() {
        guiList.forEach(BaseGui::start);
    }

    public void addGui(BaseGui gui) {
        if (!guiList.contains(gui)) {
            guiList.addFirst(gui);
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
            guiList.addFirst(gui);
            return true;
        }
        return false;
    }

    @Override
    public void cleanup() {
        guiList.forEach(BaseGui::cleanup);
    }
}
