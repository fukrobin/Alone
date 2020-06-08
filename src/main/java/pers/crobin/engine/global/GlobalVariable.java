package pers.crobin.engine.global;

import pers.crobin.engine.util.Profiler;

import java.util.LinkedList;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @Date 2020/4/8 16:28
 * @Description 管理所有全局变量，如系统属性变量，引擎全局变量
 **/
@SuppressWarnings({"unused"}) public class GlobalVariable {
    public static final float VOXEL_LEAF_MIN_SIZE = 0.5f;
    public static final int   VOXEL_ROOT_SIZE     = 16;

    /**
     * 鼠标点击触发间隔，以秒为单位
     **/
    public static float MOUSE_CLICK_INTERVAL = 0.1f;

    /**
     * 当前渲染的体素数量
     */
    public static int VOXEL_RENDER_COUNT = 0;

    /******************************************************************************
     *
     *  游戏引擎信息
     *
     ******************************************************************************/

    public static final Profiler PROFILER         = new Profiler();
    public static       double   FRAME_ELAPSED        = 0;
    public static final int      INSTANCE_BUFFER_SIZE = 1 << 20;

    public static final LinkedList<String> ENGINE_DEBUG_INFO = new LinkedList<>();
}
