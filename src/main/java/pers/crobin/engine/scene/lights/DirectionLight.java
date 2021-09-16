package pers.crobin.engine.scene.lights;

import org.joml.Vector3f;

/**
 * Created by Administrator
 *
 * @date 2020/4/19 19:45
 **/
public class DirectionLight extends Light {

    public DirectionLight() {
        this(new Vector3f(1.0f), 1.0f, new Vector3f(-1.0f, 0.0f, 0.0f));
    }

    public DirectionLight(Vector3f color, Vector3f direction) {
        this(color, 1.0f, direction);
    }

    public DirectionLight(Vector3f color, float intensity, Vector3f direction) {
        super(color, intensity, direction);
    }

    public Vector3f getDirection() {
        return getPosition();
    }

    public void setDirection(Vector3f direction) {
        setPosition(direction);
    }
}
