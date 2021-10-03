package per.alone.engine.ui.control;

import lombok.Getter;
import lombok.Setter;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.Color;
import per.alone.engine.ui.behavior.LabeledBehavior;
import per.alone.engine.ui.text.Alignment;
import per.alone.engine.ui.text.Font;

@Setter
@Getter
public class Labeled extends Region {

    private Font font;

    private Alignment alignment;

    private String text;

    private Color textColor;

    public Labeled() {
        super();
        alignment = Alignment.CENTER_LEFT;
        text = "";
        textColor = Color.BLACK;
    }

    public Font getFont() {
        if (font == null) {
            font = Font.getDefault();
        }
        return font;
    }


    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    protected LabeledBehavior<? extends Labeled> createWidgetBehavior() {
        return new LabeledBehavior<>(this);
    }
}
