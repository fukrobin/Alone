package pers.crobin.engine.util;

public abstract class BaseTimeLimitObject {
    protected long lastAccessTime;

    /**
     * 对象保质期
     */
    protected long shelfLife;

    public BaseTimeLimitObject(long shelfLife) {
        this.shelfLife = shelfLife;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public BaseTimeLimitObject access() {
        this.lastAccessTime = System.currentTimeMillis();
        return this;
    }

    public long getShelfLife() {
        return shelfLife;
    }

    public BaseTimeLimitObject setShelfLife(long shelfLife) {
        this.shelfLife = shelfLife;
        return this;
    }

    /**
     * 是否过期
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() - lastAccessTime) >= shelfLife;
    }

    /**
     * 释放已经过期的对象
     */
    public void freeExpiredObject() {
        if (isExpired()) {
            freeObject();
        }
    }

    public abstract void freeObject();
}
