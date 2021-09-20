package per.alone.engine.ui.control;

/**
 * @author 22136
 */
public class IndexRange {
    /**
     * Index range value delimiter.
     */
    public static final String VALUE_DELIMITER = ",";

    private final int start;

    private final int end;

    public IndexRange(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException();
        }

        this.start = start;
        this.end   = end;
    }

    public IndexRange(IndexRange range) {
        this.start = range.start;
        this.end   = range.end;
    }

    public static IndexRange normalize(int v1, int v2) {
        return new IndexRange(Math.min(v1, v2), Math.max(v1, v2));
    }

    public static IndexRange valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        String[] values = value.split(VALUE_DELIMITER);
        if (values.length != 2) {
            throw new IllegalArgumentException();
        }

        int start = Integer.parseInt(values[0].trim());
        int end = Integer.parseInt(values[1].trim());

        return IndexRange.normalize(start, end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return end - start;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof IndexRange) {
            IndexRange range = (IndexRange) object;
            return (start == range.start && end == range.end);
        }

        return false;
    }

    /**
     * Returns a hash code for this {@code Range} object.
     *
     * @return a hash code for this {@code Range} object.
     */
    @Override
    public int hashCode() {
        return 31 * start + end;
    }

    @Override
    public String toString() {
        return start + VALUE_DELIMITER + " " + end;
    }
}
