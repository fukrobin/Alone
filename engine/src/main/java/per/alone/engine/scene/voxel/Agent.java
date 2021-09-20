package per.alone.engine.scene.voxel;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;
import per.alone.engine.scene.Camera;
import per.alone.engine.util.Profiler;
import per.alone.engine.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import static org.joml.FrustumIntersection.*;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/4/11 02:59
 * @Description 代理类，处理与体素相关的操作，如视锥剔除、体素信息获取
 **/
public class Agent {
    private static final int ALL_PLANE =
            PLANE_MASK_NX | PLANE_MASK_NY | PLANE_MASK_NZ | PLANE_MASK_PX | PLANE_MASK_PY | PLANE_MASK_PZ;

    /**
     * 视锥剔除工具类，较好的封装了各种包装盒的视锥剔除方法，并且拥有优化版本的剔除方法
     */
    private final FrustumIntersection           frustumIntersection = new FrustumIntersection();

    private final HashMap<Integer, FloatBuffer> behaviorMapInstance;

    private final Multiset<Integer>             instanceCount;

    private final Matrix4f projectViewMtx;

    private final Vector3f cameraPosition;

    private final Vector3i worldPosOffset;

    private final Profiler profiler;

    /**
     * 保存与视锥体相交的树，组成链表，用于避免递归调用
     */
    private final LinkedList<OcTree> needTestTree;

    /**
     * 保存通过视锥剔除的树，组成链表，用于避免递归调用
     */
    private final LinkedList<OcTree> passTreeList;

    public Agent() {
        cameraPosition = new Vector3f();
        worldPosOffset = new Vector3i();
        projectViewMtx = new Matrix4f();

        behaviorMapInstance = new HashMap<>(VoxelBehavior.MAX_BEHAVIOR_COUNT);
        instanceCount       = HashMultiset.create(VoxelBehavior.MAX_BEHAVIOR_COUNT);

        passTreeList = new LinkedList<>();
        needTestTree = new LinkedList<>();
        profiler     = new Profiler();
    }

    public FloatBuffer getInstanceBuffer(int behavior) {
        FloatBuffer buffer = behaviorMapInstance.get(behavior);
        buffer.flip();
        return buffer;
    }

    public int getInstanceCount(int behavior) {
        return instanceCount.count(behavior);
    }

    public Set<Integer> getBehaviors() {
        return behaviorMapInstance.keySet();
    }

    public void reset(Camera camera) {
        cameraPosition.set(camera.getPosition());
        projectViewMtx.set(camera.getProjectionMtx());
        projectViewMtx.mul(camera.getViewMtx());
        frustumIntersection.set(projectViewMtx, false);
    }

    /**
     * 重置缓冲区的position、mark、limit值
     */
    private void resetBuffer() {
        behaviorMapInstance.values().forEach(Buffer::clear);
    }

    public boolean filterVoxel(Chunk chunk, int index) {
        // 判断参数是否合法
        Objects.requireNonNull(chunk);
        if (index < 0 || index >= Chunk.ROOT_COUNT) {
            return false;
        }

        resetBuffer();
        instanceCount.clear();
        if (chunk.root[index] != null) {
            worldPosOffset.set(chunk.positionX << 4, index << 4, chunk.positionZ << 4);
            int result;

            needTestTree.push(chunk.root[index]);
            OcTree current;

            profiler.startSection("filterVoxel");
            while (!needTestTree.isEmpty()) {
                current = needTestTree.pop();

                if (current.isLeaf()) {
                    addInstanceInfo(current);
                } else {
                    result = frustumCulling(current.spaceInfo, current.nodeData & OcTree.MASK_CULLED_PLANE);
                    if (result < 0) {
                        // 把保存上一次剔除此树的平面的3个bit置零。
                        current.nodeData = (byte) (current.nodeData & 0b11111000);
                        if (result == INSIDE) {
                            addInstanceInfo(current);
                        } else {
                            for (OcTree child : current.children) {
                                if (child != null) {
                                    needTestTree.push(child);
                                }
                            }
                        }
                    } else {
                        current.nodeData = (byte) (current.nodeData & 0b11111000 | result);
                    }
                }
            }
            profiler.endSection();

            return true;
        }

        return false;
    }

    private void addInstanceInfo(OcTree tree) {
        passTreeList.push(tree);
        OcTree current;
        int info;
        profiler.startSection("addInstanceInfo");
        while (!passTreeList.isEmpty()) {
            current = passTreeList.pop();
            if (current.isLeaf()) {
                info = current.spaceInfo;
                FloatBuffer buffer = behaviorMapInstance.computeIfAbsent(current.nodeData & 0xff,
                                                                         integer -> MemoryUtil.memAllocFloat(4096 * 3));
                buffer.put(((info & 0xf) >> OcTree.POSITION_X_SHIFT) + worldPosOffset.x + 0.5f);
                buffer.put(((info & 0xf0) >> OcTree.POSITION_Y_SHIFT) + worldPosOffset.y + 0.5f);
                buffer.put(((info & 0xf00) >> OcTree.POSITION_Z_SHIFT) + worldPosOffset.z + 0.5f);
                instanceCount.add(current.nodeData & 0xff);
            } else {
                for (OcTree child : current.children) {
                    if (child != null) {
                        passTreeList.push(child);
                    }
                }
            }
        }
        profiler.endSection();
    }

    /**
     * 测试区块是否与视锥体相交，或是完全处于视锥体之内
     *
     * @param chunk 需要测试的区块
     * @return boolean 是否与视锥体相交，或是完全处于视锥体之内
     */
    public boolean testChunk(Chunk chunk) {
        int x = chunk.positionX << 4;
        int z = chunk.positionZ << 4;
        int result;
        if (chunk.lastCulledPlane == 0) {
            result = (byte) frustumIntersection.intersectAab(x, 0, z, x + 16, Chunk.HEIGHT_LIMIT, z + 16, ALL_PLANE);
        } else {
            result = (byte) frustumIntersection.intersectAab(x, 0, z, x + 16, Chunk.HEIGHT_LIMIT, z + 16, ALL_PLANE,
                                                             chunk.lastCulledPlane);
        }
        chunk.lastCulledPlane = (byte) (Math.max(result, 0));
        return result < 0;
    }

    public int frustumCulling(float x, float y, float z, int size, int startPlane) {
        return frustumIntersection.intersectAab(x, y, z, x + size, y + size, z + size, ALL_PLANE, startPlane);
    }

    public int frustumCulling(int spaceInfo, int startPlane) {
        float x = (spaceInfo & OcTree.MASK_POSITION_X) >> OcTree.POSITION_X_SHIFT;
        float y = (spaceInfo & OcTree.MASK_POSITION_Y) >> OcTree.POSITION_Y_SHIFT;
        float z = (spaceInfo & OcTree.MASK_POSITION_Z) >> OcTree.POSITION_Z_SHIFT;
        return frustumCulling(
                x + worldPosOffset.x,
                y + worldPosOffset.y,
                z + worldPosOffset.z,
                ((spaceInfo & OcTree.MASK_SIZE) >> OcTree.SIZE_SHIFT) + 1,
                startPlane);
    }

    public void cleanup() {
        behaviorMapInstance.values().forEach(Utils::freeBuffer);
        try {
            profiler.outputToFile(new File("tmp/profiler.json"));
        } catch (IOException e) {
            System.err.println("Can't output profiler information!");
        }
    }
}
