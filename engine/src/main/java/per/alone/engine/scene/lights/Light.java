package per.alone.engine.scene.lights;

import org.joml.Vector3f;

/**
 * Created by Administrator
 *
 * @date 2020/4/19 19:30
 **/
public class Light {
    private final Vector3f color;

    private final Vector3f position;

    private       float    intensity;

    public Light() {
        this(new Vector3f(1.0f), 1.0f, new Vector3f());
    }

    public Light(Vector3f color) {
        this(color, 1.0f, new Vector3f());
    }

    public Light(Vector3f color, float intensity, Vector3f position) {
        this.color     = color;
        this.intensity = intensity;
        this.position  = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
