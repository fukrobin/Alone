package per.alone.engine.ui.behavior;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.control.Region;
import per.alone.engine.ui.layout.Border;

public class RegionBehavior<W extends Region> extends ParentBehavior<W> {
    public RegionBehavior(final W widget) {
        super(widget);
    }

    @Override
    protected void renderContent(Canvas canvas) {
        renderAsRectangle(canvas);

        super.renderContent(canvas);
    }

    /**
     * 将 Region 渲染为圆角矩形
     *
     * @param canvas {@link Canvas}
     */
    private void renderAsRectangle(Canvas canvas) {
        Region region = getWidget();

        Border border = region.getBorder();
        canvas.fillColor(region.getBackgroundColor());
        canvas.drawRoundingRect(region.getPositionInWindow(), region.getSize(), border.getRadius());

        canvas.strokeColor(border.getColor());
        canvas.strokeWidth(border.getWidth());
        canvas.stroke();
    }
}
