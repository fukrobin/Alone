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

    /**
     * 渲染当前的 WidgetBehavior 绑定的 Widget。
     * 跳过不可见的 Widget
     *
     * @param canvas {@link Canvas}
     */
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

    /**
     * 渲染 content，这可以是任何想要渲染的内容，需要注意的是，
     * 在此方法调用之前会将坐标系变换到此 Widget 的 Parent 坐标空间内
     * （或者 Scene），因此应该不需要做任何的便偏移设置
     *
     * @param canvas {@link Canvas}
     */
    protected void renderContent(Canvas canvas) {

    }

    /**
     * 处理 Widgets 的偏移， NanoVG 坐标系统是基于窗口的，
     * 为了适应 Widget 与 Scene 在 Window 位置的偏移，渲染前必须调用此方法
     *
     * @param canvas 帆布
     */
    private void transform(Canvas canvas) {
        float xOffset = widget.getPositionXInScene() - widget.getX();
        float yOffset = widget.getPositionYInScene() - widget.getY();
        canvas.resetTransform();
        canvas.translate(xOffset, yOffset);
    }

    public W getWidget() {
        return widget;
    }
}
