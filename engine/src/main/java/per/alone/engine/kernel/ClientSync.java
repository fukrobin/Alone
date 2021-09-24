package per.alone.engine.kernel;

import org.lwjgl.glfw.GLFW;

/**
 * @author Administrator
 */
public class ClientSync {

    private static final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;

    private final RunningAvg sleepDurations = new RunningAvg(10);

    private final RunningAvg yieldDurations = new RunningAvg(10);

    private long nextFrame = 0;

    public ClientSync() {
        sleepDurations.init(1000 * 1000);
        yieldDurations.init(0);
        nextFrame = getTime();
    }

    /**
     * Get the system time in nano seconds
     *
     * @return will return the current time in nano's
     */
    private static long getTime() {
        return ((long) (GLFW.glfwGetTime() * 1000) * NANOS_IN_SECOND) / 1000;
    }

    public void sync(int fps) {
        if (fps <= 0) {
            return;
        }
        try {
            for (long t0 = getTime(), t1; (nextFrame - t0) > sleepDurations.avg(); t0 = t1) {
                Thread.sleep(1);
                // update average
                sleepDurations.add((t1 = getTime()) - t0);
                // sleep time
            }
            sleepDurations.dampenForLowResTicker();
            for (long t0 = getTime(), t1; (nextFrame - t0) > yieldDurations.avg(); t0 = t1) {
                Thread.yield();
                // update average
                yieldDurations.add((t1 = getTime()) - t0);
                // yield time
            }
        } catch (InterruptedException ignored) {

        }

        nextFrame = Math.max(nextFrame + NANOS_IN_SECOND / fps, getTime());
    }

    private static class RunningAvg {
        private static final long DAMPEN_THRESHOLD = 10 * 1000L * 1000L; // 10ms

        private static final float DAMPEN_FACTOR = 0.9f; // don't change: 0.9f

        private final long[] slots;

        private int offset;
        // is exactly right!

        public RunningAvg(int slotCount) {
            this.slots  = new long[slotCount];
            this.offset = 0;
        }

        public void init(long value) {
            while (this.offset < this.slots.length) {
                this.slots[this.offset++] = value;
            }
        }

        public void add(long value) {
            this.slots[this.offset++ % this.slots.length] = value;

            this.offset %= this.slots.length;
        }

        public long avg() {
            long sum = 0;
            for (long slot : this.slots) {
                sum += slot;
            }
            return sum / this.slots.length;
        }

        public void dampenForLowResTicker() {
            if (this.avg() > DAMPEN_THRESHOLD) {
                for (int i = 0; i < this.slots.length; i++) {
                    this.slots[i] *= DAMPEN_FACTOR;
                }
            }
        }
    }

}