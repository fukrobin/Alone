package per.alone.engine.scene;

/**
 * 透视相机
 *
 * @author fkrobin
 * @date 2021/9/25 20:02
 */
public class PerspectiveCamera extends Camera {
    private double fieldOfView = -1;

    public PerspectiveCamera() {
    }

    public PerspectiveCamera(double fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    @Override
    public void computeProjectionMatrix() {
        projectionMatrix.setPerspective((float) Math.toRadians(getFieldOfView()),
                                        getViewWidth() / getViewHeight(),
                                        getNearClip(),
                                        getFarClip());
    }

    @Override
    public void computeViewMatrix() {
        viewMatrix.rotationX((float) Math.toRadians(rotation.x))
                  .rotateY((float) Math.toRadians(rotation.y));
        viewMatrix.translate(-position.x, -position.y, -position.z);
    }

    public double getFieldOfView() {
        return fieldOfView <= 0 ? 30 : fieldOfView;
    }

    public void setFieldOfView(double fieldOfView) {
        this.fieldOfView = fieldOfView;
    }
}
