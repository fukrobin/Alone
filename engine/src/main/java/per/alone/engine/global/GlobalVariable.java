package per.alone.engine.global;

import per.alone.engine.util.Profiler;

import java.util.LinkedList;

/**
 * 管理所有全局变量，如系统属性变量，引擎全局变量
 *
 * @author Administrator
 * @date 2020/4/8 16:28
 **/
@SuppressWarnings({"unused"})
public class GlobalVariable {
    public static final float VOXEL_LEAF_MIN_SIZE = 0.5f;

    public static final int   VOXEL_ROOT_SIZE     = 16;

    /******************************************************************************
     *
     *  游戏引擎信息
     *
     ******************************************************************************/

    public static final Profiler PROFILER = new Profiler();

    public static final int INSTANCE_BUFFER_SIZE = 1 << 20;

    public static final LinkedList<String> ENGINE_DEBUG_INFO = new LinkedList<>();

    /**
     * 鼠标点击触发间隔，以秒为单位
     **/
    public static float MOUSE_CLICK_INTERVAL = 0.1f;

    /**
     * 当前渲染的体素数量
     */
    public static int VOXEL_RENDER_COUNT = 0;

    public static double FRAME_ELAPSED = 0;
}
