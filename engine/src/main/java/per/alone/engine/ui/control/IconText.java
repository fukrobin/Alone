package per.alone.engine.ui.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector4i;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.engine.util.Utils;

/**
 * 配有文字的的图标控件，此控件需要自行为文字部分分配足够的空间，
 * 因此建议只使用简短且清晰表达意图的文字。
 * <p>
 * 同时请注意，此控件的布局范围事实上是没有计算文字区域的，值计算了图标的区域
 *
 * @author Administrator
 */
public class IconText extends Icon {

    private final Vector4i textColor;

    private String text;

    IconText() {
        super();
        this.textColor = new Vector4i(0, 0, 0, 222);
    }

    public IconText(int iconCodePoint, String text) {
        super(iconCodePoint);
        this.text      = text;
        this.textColor = new Vector4i(0, 0, 0, 222);
    }

    public IconText(int iconCodePoint, String text, int iconSize) {
        this(iconCodePoint, text);
        this.font.setFontSize(iconSize);
    }

    public void setTextColor(Vector4i textColor) {
        this.setTextColor(textColor.x, textColor.y, textColor.z, textColor.w);
    }

    public void setTextColor(int r, int g, int b, int a) {
        this.textColor.set(r, g, b, a);
    }

    @Override
    public void draw(float offsetX, float offsetY, Canvas canvas) {
        super.draw(offsetX, offsetY, canvas);

        canvas.fontSize(font.getFontSize() - 2);
        canvas.fontFace("sans");
        canvas.fillColor(textColor);
        canvas.textAlign(TextAlignment.TOP_LEFT);
        canvas.drawText(text, position.x + offsetX + font.getFontSize() + 5, position.y + offsetY + 1);
    }

    @Override
    protected String toJsonString() {
        return getJsonObject().toString();
    }

    @Override
    protected JsonObject getJsonObject() {
        JsonObject iconTextObject = super.getJsonObject();

        iconTextObject.addProperty("text", text);
        iconTextObject.addProperty("text-color", Utils.rgbToHexColorString(textColor));

        return iconTextObject;
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        this.text = object.get("text").getAsString();
        JsonElement textColorElement = object.get("text-color");
        if (textColorElement != null && !textColorElement.isJsonNull()) {
            this.textColor.set(Utils.hexColorToRgba(textColorElement.getAsString()));
        }
    }
}
