package pers.crobin.engine.scene.lights;

/**
 * Created by Administrator
 *
 * @Date 2020/4/19 19:27
 * @Description 指示光的衰减程度
 **/
public class Attenuation {
    private float constant;

    private float linear;

    private float exponent;

    public Attenuation(float constant, float linear, float exponent) {
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
}
