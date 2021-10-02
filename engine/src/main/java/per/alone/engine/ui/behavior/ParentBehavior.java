package per.alone.engine.ui.behavior;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.control.Parent;
import per.alone.engine.ui.control.Widget;

import java.util.List;

public class ParentBehavior<W extends Parent> extends WidgetBehavior<W> {

    public ParentBehavior(final W widget) {
        super(widget);
    }

    public List<Widget> getChildren() {
        return getWidget().getChildren();
    }

    /**
     * 渲染所有 children
     *
     * @param canvas {@link Canvas}
     */
    @Override
    protected void renderContent(Canvas canvas) {
        for (Widget child : getWidget().getChildren()) {
            child.getBehavior().render(canvas);
        }
    }
}
