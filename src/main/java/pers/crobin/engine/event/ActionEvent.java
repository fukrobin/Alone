package pers.crobin.engine.event;

import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Administrator
 */
public class ActionEvent extends MouseEvent {
    protected final Set<Integer> pressedButtons;

    protected final Set<Integer> holdingButtons;

    protected final Set<Integer> releasedButtons;

    public ActionEvent() {
        pressedButtons  = new HashSet<>();
        holdingButtons  = new HashSet<>();
        releasedButtons = new HashSet<>();
    }

    @Override
    public boolean isFired() {
        return isLeftClick();
    }

    @Override
    public void update() {
        super.update();
        pressedButtons.clear();
        releasedButtons.clear();
    }

    public void addPressedButton(int button) {
        pressedButtons.add(button);
    }

    public void addReleasedButton(int button) {
        releasedButtons.add(button);
    }

    public void addHoldingButton(int button) {
        holdingButtons.add(button);
    }

    public void removeHoldingButton(int button) {
        holdingButtons.remove(button);
    }

    public boolean isLeftClick() {
        return releasedButtons.contains(GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    public boolean isRightClick() {
        return releasedButtons.contains(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }

    public boolean isButtonPress(int button) {
        return pressedButtons.contains(button);
    }

    public boolean isButtonRelease(int button) {
        return releasedButtons.contains(button);
    }
}
