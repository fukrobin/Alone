package pers.crobin.engine.event;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class MouseEvent implements IEvent {
    protected double cursorPosX, cursorPosY;

    protected boolean hiddenCursor;

    protected boolean inWindow;

    public MouseEvent() {
        hiddenCursor = false;
        inWindow     = false;
    }

    @Override
    public boolean isFired() {
        return true;
    }

    @Override
    public void update() {

    }
}
