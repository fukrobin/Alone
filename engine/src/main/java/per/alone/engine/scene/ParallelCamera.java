package per.alone.engine.scene;

/**
 * 平行的摄像机
 *
 * @author fkrobin
 * @date 2021/9/25 20:17
 */
public class ParallelCamera extends Camera {
    @Override
    public void computeProjectionMatrix() {
        projectionMatrix.ortho2D(0f, getViewWidth(), 0, getViewHeight());
    }

    @Override
    public void computeViewMatrix() {
        viewMatrix.identity();
    }
}
