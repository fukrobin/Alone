package per.alone.engine.ui.behavior;

import per.alone.engine.ui.control.Labeled;
import per.alone.engine.ui.text.Text;

public class LabeledBehavior<W extends Labeled> extends RegionBehavior<W> {
    public LabeledBehavior(final W widget) {
        super(widget);

        widget.addChild(new Text(widget.getText()));
    }
}
