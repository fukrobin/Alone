package pers.crobin.engine.ui.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NanoVG;
import pers.crobin.engine.util.Utils;

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
    private       String   text;

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

    public String getText() {
        return text;
    }

    public IconText setText(String text) {
        this.text = text;
        return this;
    }

    public IconText setTextColor(int r, int g, int b, int a) {
        this.textColor.set(r, g, b, a);
        return this;
    }

    public Vector4i getTextColor() {
        return textColor;
    }

    public IconText setTextColor(Vector4i textColor) {
        this.textColor.set(textColor);
        return this;
    }

    @Override
    public void draw(long context, float offsetX, float offsetY) {
        super.draw(context, offsetX, offsetY);

        NanoVG.nvgFontSize(context, font.getFontSize() - 2);
        NanoVG.nvgFontFace(context, "sans");
        NanoVG.nvgTextAlign(context, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        NanoVG.nvgFillColor(context, Utils.rgba(textColor, RESULT));
        NanoVG.nvgText(context, position.x + offsetX + font.getFontSize() + 5, position.y + offsetY + 1, text);
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
