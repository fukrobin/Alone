package pers.crobin.engine.event;

/**
 * Created by Administrator
 * <p>
 * 所有事件的基类，此基类基本无事可做，甚至于它的一些子类也是如此，有时
 * 他们的唯一作用 仅仅是为了能够 分辨不同的对象，已使能将事件正确的分配
 *
 * @author Administrator
 * @date 2020/5/1 12:08
 **/
public interface IEvent {

    /**
     * 是否触发此事件
     */
    boolean isFired();

    /**
     * 在每次游戏更新的最后调用，通常你可以将此方法用来更新事件的触发条件
     */
    void update();
}
