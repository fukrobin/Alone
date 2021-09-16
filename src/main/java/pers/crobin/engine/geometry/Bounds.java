package pers.crobin.engine.geometry;

/**
 * @author Administrator
 */
public abstract class Bounds {
    private final double minX;

    private final double minY;

    private final double minZ;

    private final double width;

    private final double height;

    private final double depth;

    private final double maxX;

    private final double maxY;

    private final double maxZ;

    protected Bounds(double minX, double minY, double minZ, double width, double height, double depth) {
        this.minX   = minX;
        this.minY   = minY;
        this.minZ   = minZ;
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        this.maxX   = minX + width;
        this.maxY   = minY + height;
        this.maxZ   = minZ + depth;
    }

    /**
     * The x coordinate of the upper-left corner of this {@code Bounds}.
     */
    public final double getMinX() {
        return minX;
    }

    /**
     * The y coordinate of the upper-left corner of this {@code Bounds}.
     */
    public final double getMinY() {
        return minY;
    }

    /**
     * The minimum z coordinate of this {@code Bounds}.
     */
    public final double getMinZ() {
        return minZ;
    }

    /**
     * The width of this {@code Bounds}.
     */
    public final double getWidth() {
        return width;
    }

    /**
     * The height of this {@code Bounds}.
     */
    public final double getHeight() {
        return height;
    }

    /**
     * The depth of this {@code Bounds}.
     */
    public final double getDepth() {
        return depth;
    }

    /**
     * The x coordinate of the lower-right corner of this {@code Bounds}.
     */
    public final double getMaxX() {
        return maxX;
    }

    /**
     * The y coordinate of the lower-right corner of this {@code Bounds}.
     */
    public final double getMaxY() {
        return maxY;
    }

    /**
     * The maximum z coordinate of this {@code Bounds}.
     */
    public final double getMaxZ() {
        return maxZ;
    }

    /**
     * Tests if the specified {@code (x, y)} coordinates are inside the boundary
     * of {@code Bounds}.
     *
     * @param x the specified x coordinate to be tested
     * @param y the specified y coordinate to be tested
     * @return true if the specified {@code (x, y)} coordinates are inside the
     * boundary of this {@code Bounds}; false otherwise.
     */
    public abstract boolean contains(double x, double y);

    /**
     * Tests if the specified {@code (x, y, z)} coordinates are inside the boundary
     * of {@code Bounds}.
     *
     * @param x the specified x coordinate to be tested
     * @param y the specified y coordinate to be tested
     * @return true if the specified {@code (x, y)} coordinates are inside the
     * boundary of this {@code Bounds}; false otherwise.
     */
    public abstract boolean contains(double x, double y, double z);

    /**
     * 指示此边界的任何尺寸（宽度，高度或深度）是否小于零。
     *
     * @return 如果此范围的任何尺寸（宽度，高度或深度）小于零，则为true。
     */
    public abstract boolean isEmpty();
}
