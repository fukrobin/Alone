package per.alone.engine.ui.control;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.Color;
import per.alone.engine.ui.behavior.ParentBehavior;
import per.alone.engine.ui.behavior.RegionBehavior;
import per.alone.engine.ui.layout.Border;

/**
 * @author Administrator
 */
@Getter
@Setter
public class Region extends Parent {

    protected Color backgroundColor;

    protected Border border;

    public Region() {
        super();
        backgroundColor = Color.parseColor("#ABD8ED");
        border          = new Border(1, 3, Color.parseColor("#ececec"));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.fillColor(backgroundColor);
        canvas.drawRoundingRect(getPosition(),getSize(), border.getRadius());

        canvas.strokeColor(border.getColor());
        canvas.strokeWidth(border.getWidth());
        canvas.stroke();
    }

    @Override
    protected RegionBehavior<? extends Region> createWidgetBehavior() {
        return new RegionBehavior<>(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Region region = (Region) o;
        return Objects.equal(backgroundColor, region.backgroundColor) && Objects.equal(border, region.border);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), backgroundColor, border);
    }
}
