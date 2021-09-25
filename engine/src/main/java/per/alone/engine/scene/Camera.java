package per.alone.engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import per.alone.stage.Window;

import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2020/4/5.
 *
 * @author fkobin
 * @date 2020/4/5 01:19
 **/
public abstract class Camera {
    protected final Vector2f rotation;

    protected final Vector3f position;

    protected final Matrix4f viewMatrix;

    protected final Matrix4f projectionMatrix;

    private final FloatBuffer viewMtxBuffer;

    private final FloatBuffer projectionMtxBuffer;

    private final Vector3f target;

    private final Vector3f up;

    /**
     * 视锥体的视野范围大小,默认值为60.0f
     */
    private final float fovy = 60.0f;

    private float viewWidth = 1.0f;

    private float viewHeight = 1.0f;

    /**
     * 指定近裁剪面的距离
     */
    private float nearClip = 0.0f;

    /**
     * 指定远裁剪平面的距离
     */
    private float farClip = -1.0f;

    private Window window; // TODO inject


    public Camera() {
        position = new Vector3f();
        target   = new Vector3f(0.0f, 0.0f, -1.0f);
        up       = new Vector3f(0.0f, 1.0f, 0.0f);

        rotation         = new Vector2f();
        viewMatrix       = new Matrix4f();
        projectionMatrix = new Matrix4f();

        viewMtxBuffer       = BufferUtils.createFloatBuffer(16);
        projectionMtxBuffer = BufferUtils.createFloatBuffer(16);
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(float viewWidth) {
        this.viewWidth = viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
    }

    public final float getNearClip() {
        return nearClip <= 0.0 ? 0.1f : nearClip;
    }

    public final void setNearClip(float value) {
        nearClip = value;
    }

    public final float getFarClip() {
        return farClip <= 0 ? 100.0f : farClip;
    }

    public final void setFarClip(float value) {
        farClip = value;
    }

    public void setCameraPos(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setRotation(float pitch, float yaw) {
        rotation.x = pitch;
        rotation.y = yaw;
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void rotateOffset(double pitchOffset, double yawOffset) {
        rotation.x += pitchOffset;
        rotation.y += yawOffset;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * 计算投影矩阵
     */
    public abstract void computeProjectionMatrix();

    /**
     * 计算视图矩阵
     */
    public abstract void computeViewMatrix();

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * 将投影矩阵转为 float buffer，在返回之前将会调用 {@link Camera#computeProjectionMatrix}
     *
     * @return {@link FloatBuffer}
     */
    public FloatBuffer getProjectionMtxBuffer() {
        computeProjectionMatrix();
        return getProjectionMatrix().get(projectionMtxBuffer);
    }

    /**
     * 将视图矩阵转为 float buffer，在返回之前将会调用 {@link Camera#computeViewMatrix}
     *
     * @return {@link FloatBuffer}
     */
    public FloatBuffer getViewMtxBuffer() {
        computeViewMatrix();
        return getViewMatrix().get(viewMtxBuffer);
    }
}
