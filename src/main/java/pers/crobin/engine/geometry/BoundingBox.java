package pers.crobin.engine.geometry;

/**
 * @author Administrator
 */
public class BoundingBox extends Bounds {

    protected BoundingBox(double minX, double minY, double minZ, double width, double height, double depth) {
        super(minX, minY, minZ, width, height, depth);
    }

    public BoundingBox(double minX, double minY, double width, double height) {
        super(minX, minY, 0, width, height, 0);
    }

    @Override
    public boolean contains(double x, double y) {
        return contains(x, y, 0.0f);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        if (isEmpty()) {
            return false;
        }

        return (x >= getMinX() && x <= getMaxX()) &&
               (y >= getMinY() && y <= getMaxY()) &&
               (z >= getMinZ() && z <= getMaxZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return getMaxX() < getMinX() ||
               getMaxY() < getMinY() ||
               getMaxZ() < getMinZ();
    }
}
