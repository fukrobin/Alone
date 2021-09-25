package per.alone.engine.ui.control;

import per.alone.engine.ui.Canvas;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 * @date 20/5/18/09点58分
 */
public class Parent extends Widgets {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String[] CONTROLS_PACKAGE = {"pers.crobin.per.fkrobin.engine.ui.control.",
            "pers.crobin.per.fkrobin.engine.ui.text."};

    /**
     * 子控件链表
     */
    private final List<Widgets> children;

    public Parent() {
        super();
        children = new LinkedList<>();
    }

    public void addChild(Widgets control) {
        Objects.requireNonNull(control);
        control.setParent(this);
        control.setScene(getScene());
        children.add(control);
    }

    public void addChildren(Widgets... controls) {
        for (Widgets control : controls) {
            addChild(control);
        }
    }

    public List<Widgets> getChildren() {
        return children;
    }

    public boolean remove(Widgets control) {
        return children.remove(control);
    }

    @Override
    public void draw(Canvas canvas) {
        children.forEach(control -> control.draw(canvas));
    }
}