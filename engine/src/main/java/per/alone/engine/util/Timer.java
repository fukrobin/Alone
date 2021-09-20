package per.alone.engine.util;

import per.alone.engine.global.GlobalVariable;

/**
 * @author CRobin
 */
public class Timer {

    private double lastLoopTime;

    private float accumulator;

    private float interval;


    public void init(float interval) {
        lastLoopTime  = getTime();
        this.interval = interval;
    }

    private double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    public void tick() {
        float elapsedTime = getElapsedTime();
        accumulator += elapsedTime;
        GlobalVariable.FRAME_ELAPSED = elapsedTime;
    }

    public boolean needUpdate() {
        boolean f = accumulator >= interval;
        accumulator -= interval;
        return f;
    }
}