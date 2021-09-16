package pers.crobin.engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import pers.crobin.engine.kernel.EngineThread;

import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2020/4/5.
 *
 * @author fkobin
 * @date 2020/4/5 01:19
 **/
public class Camera {
    private final FloatBuffer viewMtxBuffer       = BufferUtils.createFloatBuffer(16);

    private final FloatBuffer projectionMtxBuffer = BufferUtils.createFloatBuffer(16);

    private final Vector2f rotation;

    private final Vector3f target;

    private final Vector3f up;

    private final Vector3f position;

    private final Matrix4f viewMtx;

    private final Matrix4f projectionMtx;

    /**
     * 视锥体的视野范围大小,默认值为60.0f
     */
    private       float    fovy = 60.0f;

    public Camera() {
        position = new Vector3f();
        target   = new Vector3f(0.0f, 0.0f, -1.0f);
        up       = new Vector3f(0.0f, 1.0f, 0.0f);

        rotation      = new Vector2f();
        viewMtx       = new Matrix4f();
        projectionMtx = new Matrix4f();
        setProjection();
    }

    public void setFovy(float fovy) {
        this.fovy = fovy;
        setProjection();
    }

    private void setProjection() {
        projectionMtx.setPerspective(toRadians(), EngineThread.getThreadWindow().aspect(), 0.01f, 100.0f);
    }

    private float toRadians() {
        return (float) Math.toRadians(fovy);
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

    public Matrix4f getProjectionMtx() {
        setProjection();
        return projectionMtx;
    }

    public Matrix4f getViewMtx() {
        viewMtx.rotationX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y));
        viewMtx.translate(-position.x, -position.y, -position.z);

        return viewMtx;
    }

    public FloatBuffer getProjectionMtxBuffer() {
        return getProjectionMtx().get(projectionMtxBuffer);
    }

    public FloatBuffer getViewMtxBuffer() {
        return getViewMtx().get(viewMtxBuffer);
    }
}
