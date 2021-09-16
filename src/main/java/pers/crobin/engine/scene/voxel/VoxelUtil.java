package pers.crobin.engine.scene.voxel;

import java.util.HashMap;

/**
 * Created by Administrator
 *
 * @date 2020/4/16 01:52
 **/
public class VoxelUtil {
    private static final HashMap<Integer, Chunk> CHUNK_MAP          = new HashMap<>();

    private static final int                     WORLD_BOUND_MIN    = -32000;

    private static final int                     WORLD_BOUND_MAX    = 32000;

    private static final int                     WORLD_HEIGHT_LIMIT = 256;

    public static Chunk getNewChunk(short positionX, short positionZ) {
        if (!canLoadChunk(positionX, positionZ)) {
            return null;
        }

        int index = positionX | positionZ << 16;
        Chunk chunk = new Chunk(positionX, positionZ);
        CHUNK_MAP.put(index, chunk);
        return chunk;
    }

    public static boolean checkChunkCoordinate(int x, int z) {
        return x >= WORLD_BOUND_MIN && x <= WORLD_BOUND_MAX && z >= WORLD_BOUND_MIN && z <= WORLD_BOUND_MAX;
    }

    private static boolean canLoadChunk(short x, short z) {
        return x >= WORLD_BOUND_MIN && x <= WORLD_BOUND_MAX && z >= WORLD_BOUND_MIN && z <= WORLD_BOUND_MAX;
    }

    /**
     * 卸载区块，存储到文件中 //TODO
     */
    private static void unloadChunk() {

    }

    public static int calculateIndex(int x, int y, int z, int size) {
        int index = 0;
        if (x >= size) {
            index |= 0b1;
        }

        if (y >= size) {
            index |= 0b10;
        }

        if (z >= size) {
            index |= 0b100;
        }
        return index;
    }
}
