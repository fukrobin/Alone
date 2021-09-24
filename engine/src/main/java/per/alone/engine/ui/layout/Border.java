package per.alone.engine.ui.layout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import per.alone.engine.ui.Color;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
public class Border {

    private int width;

    private int radius;

    private Color color;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Border border = (Border) o;
        return width == border.width && radius == border.radius && Objects.equals(color, border.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, radius, color);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Border.class.getSimpleName() + "[", "]")
                .add("width=" + width)
                .add("radius=" + radius)
                .add("color=" + color)
                .toString();
    }
}
