package per.alone.engine.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL33.GL_TIME_ELAPSED;
import static org.lwjgl.opengl.GL33.glGetQueryObjectui64v;

@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class GPUTimer extends AbstractTimer {
    private static final int GPU_QUERY_COUNT = 5;

    private final IntBuffer queries = BufferUtils.createIntBuffer(GPU_QUERY_COUNT);

    private int     currentQueryId;

    private int     lastQueryId;

    private boolean supported;

    public void initTimer() {
        supported = GL.getCapabilities().GL_ARB_timer_query;
        if (supported) {
            glGenQueries(queries);
        }
    }

    @Override
    public void startTimer() {
        if (this.supported) {
            glBeginQuery(GL_TIME_ELAPSED, this.queries.get(this.currentQueryId % GPU_QUERY_COUNT));
            this.currentQueryId++;
        }
    }

    @Override
    public void endTimer() {
        if (this.supported) {
            glEndQuery(GL_TIME_ELAPSED);

            int count = 0;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer available = stack.ints(1);
                while (available.get(0) != 0 && this.lastQueryId <= this.currentQueryId) {
                    glGetQueryObjectiv(this.queries.get(this.lastQueryId % GPU_QUERY_COUNT), GL_QUERY_RESULT_AVAILABLE,
                                       available);
                    if (available.get(0) != 0) {
                        LongBuffer timeElapsed = stack.mallocLong(1);
                        glGetQueryObjectui64v(this.queries.get(this.lastQueryId % GPU_QUERY_COUNT), GL_QUERY_RESULT,
                                              timeElapsed);
                        this.lastQueryId++;
                        if (count < 3) {
                            autoAverage(timeElapsed.get(0));
                            count++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getAverageTime() {
        return averageTime;
    }

    @Override
    protected void autoAverage(long elapsed) {
        totalTime -= historyTimes[currentIndex];
        totalTime += elapsed;
        averageTime = (double) (totalTime / HISTORY_COUNT);

        historyTimes[currentIndex] = elapsed;

        currentIndex = (currentIndex + 1) % HISTORY_COUNT;
    }
}