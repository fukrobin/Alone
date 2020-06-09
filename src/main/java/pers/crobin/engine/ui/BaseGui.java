package pers.crobin.engine.ui;

import org.lwjgl.nanovg.NVGColor;
import pers.crobin.engine.IMemoryManager;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.ui.control.Parent;

import java.util.Objects;

/**
 * @author Administrator
 */
public abstract class BaseGui implements IMemoryManager {
    protected static final NVGColor RESULT_COLOR = NVGColor.create();
    protected              Parent   parent;
    protected              boolean  visible      = true;
    protected              boolean  disable      = false;

    protected BaseGui() {
        this.parent = new Parent().setSize(100, 100);
    }

    public boolean isVisible() {
        return visible;
    }

    public BaseGui setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isDisable() {
        return disable;
    }

    public BaseGui setDisable(boolean disable) {
        this.disable = disable;
        return this;
    }

    public Parent getParent() {
        return parent;
    }

    protected abstract void start();

    public abstract void draw(Window window);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseGui baseGui = (BaseGui) o;
        return parent.equals(baseGui.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent);
    }
}
