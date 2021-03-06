package pers.crobin.engine.util;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @Date 2020/4/27 17:18
 **/
public class CPUTimer extends AbstractTimer {
    private long start;

    public CPUTimer() {
        super();
    }

    @Override public void startTimer() {
        start = System.nanoTime();
    }

    @Override public void endTimer() {
        long end = System.nanoTime();
        autoAverage(end - start);
    }

    @Override protected void autoAverage(long elapsed) {
        totalTime -= historyTimes[currentIndex];
        totalTime += elapsed;
        averageTime = (double)(totalTime / HISTORY_COUNT);

        historyTimes[currentIndex] = elapsed;

        currentIndex = (currentIndex + 1) % HISTORY_COUNT;
    }

    @Override public double getAverageTime() {
        return averageTime;
    }
}
