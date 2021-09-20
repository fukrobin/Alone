package per.alone.engine.scene.voxel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预设的体素组织方式，对应Minecraft中的区块，16 * 256 * 16
 *
 * @author Administrator
 * @date 2020/4/12 15:52
 **/
public class Chunk {
    public static final int CHUNK_SIZE = 16;

    public static final int HEIGHT_LIMIT = 256;

    public static final int SIZE_X16_SHIFT = 4, SIZE_X8_SHIFT = 3, SIZE_X4_SHIFT = 2, SIZE_X2_SHIFT = 1, SIZE_X1_SHIFT
                                           = 0;

    public static final int CHILD_COUNT = 8;

    public static final int ROOT_COUNT = 16;

    public static final short EMPTY_VOXEL = (short) 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk.class);

    public final short positionX, positionZ;

    public final OcTree[] root;

    /**
     * 上一次视锥剔除中，第一个剔除此区块的平面的索引标志
     */
    public byte lastCulledPlane = 0;

    public Chunk(short positionX, short positionZ) {
        this.positionX = positionX;
        this.positionZ = positionZ;

        root = new OcTree[ROOT_COUNT];
    }

    public boolean addVoxel(int x, int y, int z, byte behavior) {
        if (parameterInValid(x, y, z, behavior)) {
            return false;
        }

        int idx = y / ROOT_COUNT;
        if (root[idx] == null) {
            root[idx] = new OcTree(null, true).setSpaceInfo(0, 0, 0, 1 << SIZE_X16_SHIFT);
        }
        return root[idx].addLeaf(x, y & 0xf, z, 0, 0, 0, 1 << SIZE_X16_SHIFT, behavior);
    }

    public void removeVoxel(int x, int y, int z) {
        if (parameterInValid(x, y, z)) {
            return;
        }

        if (root[y / ROOT_COUNT] != null) {
            root[y / ROOT_COUNT].removeLeaf(x, y & 0xf, z, 1 << SIZE_X16_SHIFT);
        }
    }

    private boolean parameterInValid(int x, int y, int z, byte behaviorId) {
        return parameterInValid(x, y, z) || !VoxelBehavior.registered(behaviorId);
    }

    private boolean parameterInValid(int x, int y, int z) {
        return x < 0 || y < 0 || z < 0 || x >= CHUNK_SIZE || y >= HEIGHT_LIMIT || z >= CHUNK_SIZE;
    }
}
