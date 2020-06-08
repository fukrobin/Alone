package pers.crobin.engine.event;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Administrator
 */
public class KeyEvent implements IEvent {
    /**
     * 所有输入的字符的Unicode代码队列，每次更新读取后清除
     */
    protected final ConcurrentLinkedQueue<Integer> inputQueue;

    protected final boolean[] keysState;

    public KeyEvent() {
        keysState  = new boolean[512];
        inputQueue = new ConcurrentLinkedQueue<>();
    }


    @Override
    public void update() {
        inputQueue.clear();
    }

    public ConcurrentLinkedQueue<Integer> getInputQueue() {
        return inputQueue;
    }

    @Override
    public boolean isFired() {
        return true;
    }

    public void setKeyState(int key, boolean keyState) {
        if (key >= 0 && key < keysState.length) {
            keysState[key] = keyState;
        }
    }

    public boolean isKeyPressed(int key) {
        if (key >= 0 && key < keysState.length) {
            return keysState[key];
        }
        return false;
    }

    public boolean isKeyReleased(int key) {
        return !isKeyPressed(key);
    }
}
