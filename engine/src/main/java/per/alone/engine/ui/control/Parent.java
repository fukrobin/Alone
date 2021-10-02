package per.alone.engine.ui.control;

import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.behavior.ParentBehavior;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 * @date 20/5/18/09点58分
 */
public class Parent extends Widget {
    /**
     * 子控件链表
     */
    private final List<Widget> children;

    public Parent() {
        super();
        children = new LinkedList<>();
    }

    public void addChild(Widget control) {
        Objects.requireNonNull(control);
        control.setParent(this);
        control.setScene(getScene());
        children.add(control);
    }

    /**
     * 更新部件的布局，默认不作任何事情，由具体布局组件实现
     */
    protected void requestLayout() {

    }

    public void addChildren(Widget... controls) {
        for (Widget control : controls) {
            addChild(control);
        }
    }

    public List<Widget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean remove(Widget control) {
        return children.remove(control);
    }

    @Override
    public void draw(Canvas canvas) {
        children.forEach(control -> control.draw(canvas));
    }

    @Override
    protected ParentBehavior<? extends Parent> createWidgetBehavior() {
        return new ParentBehavior<>(this);
    }
}