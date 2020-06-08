package pers.crobin.engine.util;

/**
 * Created by Administrator
 *
 * @Date 2020/4/27 17:23
 * @Description $
 **/
public abstract class AbstractTimer {
    public static final int HISTORY_COUNT = 100;

    protected final long[] historyTimes;

    protected double averageTime;
    protected long   totalTime;
    protected int    currentIndex;

    public AbstractTimer() {
        historyTimes = new long[HISTORY_COUNT];
    }

    public abstract void startTimer();

    public abstract void endTimer();

    protected abstract void autoAverage(long elapsed);

    public double getAverageTime() {
        return averageTime;
    }
}
