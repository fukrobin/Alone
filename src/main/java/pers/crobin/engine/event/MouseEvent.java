package pers.crobin.engine.event;

/**
 * @author Administrator
 */
public class MouseEvent implements IEvent {
    protected double cursorPosX, cursorPosY;
    protected boolean hiddenCursor;

    protected boolean inWindow;

    public MouseEvent() {
        hiddenCursor = false;
        inWindow     = false;
    }

    public double getCursorPosX() {
        return cursorPosX;
    }

    public void setCursorPosX(double cursorPosX) {
        this.cursorPosX = cursorPosX;
    }

    public double getCursorPosY() {
        return cursorPosY;
    }

    public void setCursorPosY(double cursorPosY) {
        this.cursorPosY = cursorPosY;
    }

    public boolean isHiddenCursor() {
        return hiddenCursor;
    }

    public void setHiddenCursor(boolean hiddenCursor) {
        this.hiddenCursor = hiddenCursor;
    }

    public boolean isInWindow() {
        return inWindow;
    }

    public void setInWindow(boolean inWindow) {
        this.inWindow = inWindow;
    }

    @Override
    public boolean isFired() {
        return true;
    }

    @Override
    public void update() {

    }
}
