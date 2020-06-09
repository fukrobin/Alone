package pers.crobin.engine.ui.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lwjgl.system.MemoryStack;
import pers.crobin.engine.ui.Canvas;
import pers.crobin.engine.ui.control.Region;
import pers.crobin.engine.util.Utils;

import java.nio.FloatBuffer;
import java.util.Objects;

/**
 * Text类定义一个显示文本的节点。 段落之间用'\n'分隔，并且文本被包裹在段落边界上。
 *
 * @author Administrator
 */
public class Text extends Region {
    protected final Font          font;
    protected       TextAlignment align;
    protected       String        text;

    /**
     * 为此<code>Text</code>控件定义一个宽度限制，例如 像素，而不是字形或字符数。 如果值> 0， 则将根据需要对该行进行换行，以满足此约束。
     */
    protected float wrappingWidth;

    public Text() {
        super();
        this.font  = new Font();
        this.align = TextAlignment.TOP_LEFT;
    }

    public Text(String text) {
        super();
        Objects.requireNonNull(text);
        this.font  = new Font();
        this.text  = text;
        this.align = TextAlignment.TOP_LEFT;
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        // 必需的属性，不会对此类属性进行检查，这意味着此类属性只要有任何的不规范就会形成异常
        this.text = object.get("text").getAsString();

        // 非必需属性（带默认值）,此类属性如果为空或是错误，会直接跳过
        JsonElement fontSizeElement  = object.get("font-size");
        JsonElement fontFaceElement  = object.get("font-face");
        JsonElement fontColorElement = object.get("font-color");
        JsonElement alignElement     = object.get("align");
        if (fontSizeElement != null && !fontSizeElement.isJsonNull()) {
            this.font.fontSize = fontSizeElement.getAsInt();
        }
        if (fontFaceElement != null && !fontFaceElement.isJsonNull()) {
            this.font.fontFace = fontFaceElement.getAsString();
        }
        if (fontColorElement != null && !fontColorElement.isJsonNull()) {
            this.font.color.set(Utils.hexColorToRgba(fontColorElement.getAsString()));
        }
        if (alignElement != null && !alignElement.isJsonNull()) {
            this.align = TextAlignment.valueOf(alignElement.getAsString());
        }
    }

    public TextAlignment getAlign() {
        return align;
    }

    public Text setAlign(TextAlignment align) {
        this.align = align;

        return this;
    }

    public String getText() {
        return text;
    }

    public Text setText(String text) {
        Objects.requireNonNull(text);
        this.text = text;

        return this;
    }

    /**
     * Gets the value of the property wrappingWidth.
     *
     * @return wrappingWidth.
     */
    public float getWrappingWidth() {
        return wrappingWidth;
    }

    /**
     * Sets the value of the wrappingWidth.
     *
     * @param wrappingWidth 为此<code>Text</code>控件定义一个宽度限制，例如 像素，而不是字形或字符数。
     *                      如果值> 0， 则将根据需要对该行进行换行，以满足此约束。
     *
     * @return {@link Text}
     */
    public Text setWrappingWidth(float wrappingWidth) {
        this.wrappingWidth = wrappingWidth;
        return this;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        if (this.text != null) {
            Canvas.setFont(font);
            Canvas.textAlign(align);

            float x = position.x + offsetX;
            float y = position.y + offsetY;
            if (wrappingWidth != 0) {
                Canvas.textBox(x, y, wrappingWidth, text);
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    // 测量Text的文本区域边界
                    FloatBuffer floatBuffer = stack.mallocFloat(4);
                    Canvas.textBoxBounds(x, y, wrappingWidth, text, floatBuffer);
                    size.x = floatBuffer.get(2) - floatBuffer.get(0);
                    size.y = floatBuffer.get(3) - floatBuffer.get(1);
                }
            } else {
                Canvas.drawText(text, x, y);
            }
        }
    }

    @Override
    public String toJsonString() {
        return getJsonObject().toString();
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject textObject = new JsonObject();

        textObject.addProperty("text", text);
        textObject.addProperty("layout-x", position.x);
        textObject.addProperty("layout-y", position.y);
        textObject.addProperty("font-size", font.fontSize);
        textObject.addProperty("font-face", font.fontFace);
        textObject.addProperty("font-color", Utils.rgbToHexColorString(font.getColor()));
        textObject.addProperty("align", align.toString());

        return textObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Text text1 = (Text) o;
        return font.equals(text1.font) &&
               text.equals(text1.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), font, text);
    }
}
