package per.alone.engine.ui.painter;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.control.Widgets;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/26 21:07
 */
public class WidgetsPainter {

    public final void render(Widgets widgets, Canvas canvas) {
        if (!widgets.isVisible()) {
            return;
        }

        doRender(widgets, canvas);
    }

    private void doRender(Widgets widgets, Canvas canvas) {
        transform(widgets, canvas);


    }

    protected void renderContent() {

    }

    /**
     * 处理 Widgets 的偏移， NanoVG 坐标系统是基于窗口的，
     * 为了适应 Widget 与 Scene 在 Window 位置的偏移，渲染前必须调用此方法
     *
     * @param widgets 小部件
     * @param canvas  帆布
     */
    private void transform(Widgets widgets, Canvas canvas) {
        float xOffset = widgets.getXInScene() - widgets.getX();
        float yOffset = widgets.getYInScene() - widgets.getY();
        canvas.resetTransform();
        canvas.translate(xOffset, yOffset);
    }


}
