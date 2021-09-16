package pers.crobin.engine.scene.voxel;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/7 21:09
 **/
public class OcTree {
    public static final int FULL_SPACE_SHIFT = 3;

    public static final int CULLED_PLANE_SHIFT = 0;

    public static final int MASK_CULLED_PLANE = 0b111;

    public static final int MASK_POSITION_X = 0xf;

    public static final int MASK_POSITION_Y = 0xf0;

    public static final int MASK_POSITION_Z = 0xf00;

    public static final int MASK_SIZE = 0xf000;

    public static final int POSITION_X_SHIFT = 0;

    public static final int POSITION_Y_SHIFT = 4;

    public static final int POSITION_Z_SHIFT = 8;

    public static final int SIZE_SHIFT = 12;

    private static final int CHILD_COUNT = 8;

    public OcTree parent;

    public OcTree[] children;

    /**
     * default 11111111
     * size culledPlane
     * 0 0000 000
     */
    public byte nodeData = 0;

    public short spaceInfo;

    public OcTree(OcTree parent, boolean hasChildren) {
        this.parent = parent;
        if (hasChildren) {
            this.children = new OcTree[CHILD_COUNT];
        } else {
            this.children = null;
        }
    }

    public boolean addLeaf(int x, int y, int z, int posX, int posY, int posZ, int size, byte behavior) {
        size >>= 1;
        int index = calcIndex(x, y, z, size);
        posX = posX + (x / size) * size;
        posY = posY + (y / size) * size;
        posZ = posZ + (z / size) * size;
        // 默认空间全部填满
        boolean flag = true;
        if (size > 1) {
            if (children[index] == null) {
                children[index] = new OcTree(this, true).setSpaceInfo(posX, posY, posZ, size);
            }
            flag = children[index].addLeaf(x % size, y % size, z % size, posX, posY, posZ, size, behavior);

            if (flag) {
                // 只要有任何一个孩子节点为空，即没有填满
                for (OcTree child : children) {
                    if (child == null || (child.nodeData & 1 << FULL_SPACE_SHIFT) == 0) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            if (children[index] == null) {
                children[index] = new OcTree.Leaf(this).setBehavior(behavior).setSpaceInfo(posX, posY, posZ, 1);

                // 只要有任何一个孩子节点为空，即没有填满
                for (OcTree child : children) {
                    if (child == null) {
                        flag = false;
                        break;
                    }
                }
            }
        }

        if (flag) {
            this.nodeData = (byte) (this.nodeData | 1 << FULL_SPACE_SHIFT);
        }

        return flag;
    }

    public boolean removeLeaf(int x, int y, int z, int size) {
        size >>= 1;
        int index = calcIndex(x, y, z, size);
        // 默认此树为空
        boolean flag = true;
        if (children[index] != null) {
            if (size > 1) {
                flag = children[index].removeLeaf(x % size, y % size, z % size, size);
                if (flag) {
                    children[index] = null;
                    // 任何一个子节点非空，即代表此树不为空
                    for (OcTree child : children) {
                        if (child != null) {
                            flag = false;
                            break;
                        }
                    }
                }

            } else {
                children[index] = null;
                // 任何一个子节点非空，即代表此树不为空
                for (OcTree child : children) {
                    if (child != null) {
                        flag = false;
                        break;
                    }
                }
            }
        }

        return flag;
    }

    private int calcIndex(int x, int y, int z, int size) {
        return (z / size) << 2 | (y / size) << 1 | (x / size);
    }

    public OcTree setSpaceInfo(int posX, int posY, int posZ, int size) {
        this.spaceInfo = (short) (posX << POSITION_X_SHIFT | posY << POSITION_Y_SHIFT | posZ << POSITION_Z_SHIFT |
                                  (size - 1) << SIZE_SHIFT);
        return this;
    }

    public boolean isLeaf() {
        return false;
    }

    public static class Leaf extends OcTree {
        public Leaf(OcTree parent) {
            super(parent, false);
        }

        public Leaf setBehavior(byte behavior) {
            this.nodeData = behavior;
            return this;
        }

        @Override
        public OcTree.Leaf setSpaceInfo(int posX, int posY, int posZ, int size) {
            super.setSpaceInfo(posX, posY, posZ, size);
            return this;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }
}
