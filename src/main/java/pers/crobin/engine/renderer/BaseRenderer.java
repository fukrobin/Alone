package pers.crobin.engine.renderer;

import pers.crobin.engine.IMemoryManager;

/**
 * 渲染基类，每个渲染器都需要指定一个渲染目标。
 * @author Administrator
 */
public abstract class BaseRenderer<T> implements IMemoryManager {
    protected T target;

    public BaseRenderer() {
    }

    public BaseRenderer(T target) {
        this.target = target;
    }

    public void setTarget(T t) {
        this.target = t;
    }

    public T getTarget() {
        return target;
    }

    /**
     * 对渲染目标进行渲染
     */
    public abstract void render();
}
