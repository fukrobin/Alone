package per.alone.engine.scene.voxel;

import per.alone.engine.util.GLHelp;

import java.util.LinkedList;

/**
 * Created by Administrator<br>
 *
 * <p>管理体素体素行为，每个体素都必须与一个体素行为关联。
 * 体素行为应该包括【呈现的纹理、与玩家交互的方式、碰撞行为、光照行为等等】</p>
 *
 * @author Administrator
 * @apiNote 此类未进行任何同步，从本质上来说对此类进行多线程调用没有任何必要的， 因为
 * {{@link #getNewVoxelBehavior(String)}}此方法将会调用<code>OpenGL</code>函数
 * 生成纹理，并返回一个<code>behaviorId</code>，而<code>OpenGL</code>是无法多线程调用的。
 * @date 2020/4/16 01:03
 **/
public class VoxelBehavior {
    /**
     * 当前版本的引擎中只支持最多256中不同的体素纹理。未来可能会改变为<code>Integer.MAX_VALUE</code>
     */
    public static final  int                 MAX_BEHAVIOR_COUNT = 256;

    private static final VoxelBehavior[]     BEHAVIORS          = new VoxelBehavior[MAX_BEHAVIOR_COUNT];

    private static final LinkedList<Integer> IDLE_INDEX         = new LinkedList<>();

    static {
        for (int i = 1; i < MAX_BEHAVIOR_COUNT; i++) {
            IDLE_INDEX.offer(i);
        }
    }

    private int textureId = -1;

    private VoxelBehavior() {
    }

    /**
     * 请不要在多线程中调用此方法。
     *
     * @param texturePath 需要添加的纹理的路径
     * @return 返回一个可用的behaviorId，用于索引
     * @throws Exception           当前的引擎只支持256中不同的纹理，如果超出范围即会报错。
     * @throws java.io.IOException 当纹理路径错误（任何的不合理表示）时抛出。
     */
    public static Byte getNewVoxelBehavior(String texturePath) throws Exception {
        Integer index = IDLE_INDEX.pollFirst();
        if (index == null) {
            throw new Exception(String.format("Engine can only support up to [ %d ] textures", MAX_BEHAVIOR_COUNT));
        }
        VoxelBehavior behavior = new VoxelBehavior();
        BEHAVIORS[index] = behavior;

        behavior.textureId = GLHelp.loadTexture(texturePath);
        return index.byteValue();
    }

    /**
     * 根据<code>Behavior</code>返回纹理
     *
     * @param behaviorId 需要查询绑定的纹理的<code>Behavior</code>的id
     * @return 如果此行为id已注册，返回绑定的纹理；否则返回-1.
     */
    public static int getTextureId(byte behaviorId) {
        VoxelBehavior behavior = BEHAVIORS[behaviorId & 0xff];
        return behavior == null ? -1 : behavior.textureId;
    }

    /**
     * @param behaviorId 需要查询的<code>Behavior</code>的id
     * @return 此 <code>Behavior</code>是否已注册
     */
    public static boolean registered(byte behaviorId) {
        return BEHAVIORS[behaviorId & 0xff] != null;
    }

    /**
     * 请不要进行多线程调用（这样简单的方法为何您要多线程呢¿¿¿）
     *
     * @param behaviorId 需要移除的<code>Behavior</code>的索引。
     */
    public static void removeBehiorId(byte behaviorId) {
        int idx = behaviorId & 0xff;

        if (idx > 0 && !IDLE_INDEX.contains(idx) && BEHAVIORS[idx] != null) {
            BEHAVIORS[idx] = null;
            IDLE_INDEX.offer(idx);
        }
    }

    public static int behaviorCount() {
        return MAX_BEHAVIOR_COUNT - IDLE_INDEX.size();
    }
}
