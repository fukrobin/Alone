package pers.crobin.engine.event;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Administrator
 */
public abstract class Callback<T> {

    protected List<T> callbacks = new LinkedList<>();

    public void addCallback(T callback) {
        if (callback != null) {
            callbacks.add(callback);
        }
    }

    public void removeCallback(T callback) {
        if (callback != null) {
            callbacks.remove(callback);
        }
    }
}
