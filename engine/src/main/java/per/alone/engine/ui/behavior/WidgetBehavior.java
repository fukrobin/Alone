package per.alone.engine.ui.behavior;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.control.Widget;

import java.util.Objects;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/26 21:07
 */
public class WidgetBehavior<W extends Widget> {

    private final W widget;

    public WidgetBehavior(final W widget) {
        Objects.requireNonNull(widget, "Cannot pass null for widget");
        this.widget = widget;
    }

    public final void render(Canvas canvas) {
        if (!widget.isVisible()) {
            return;
        }

        doRender(canvas);
    }

    private void doRender(Canvas canvas) {
        transform(canvas);
        renderContent(canvas);
    }

    protected void renderContent(Canvas canvas) {

    }

    /**
     * 处理 Widgets 的偏移， NanoVG 坐标系统是基于窗口的，
     * 为了适应 Widget 与 Scene 在 Window 位置的偏移，渲染前必须调用此方法
     *
     * @param canvas 帆布
     */
    private void transform(Canvas canvas) {
        float xOffset = widget.getXInScene() - widget.getX();
        float yOffset = widget.getYInScene() - widget.getY();
        canvas.resetTransform();
        canvas.translate(xOffset, yOffset);
    }

    public W getWidget() {
        return widget;
    }
}
