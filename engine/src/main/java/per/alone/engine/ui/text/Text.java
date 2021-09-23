package per.alone.engine.ui.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.system.MemoryStack;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.control.Region;
import per.alone.engine.util.Utils;

import java.nio.FloatBuffer;
import java.util.Objects;

/**
 * Text类定义一个显示文本的节点。 段落之间用'\n'分隔，并且文本被包裹在段落边界上。
 *
 * @author Administrator
 */
@Getter
@Setter
public class Text extends Region {
    public static final TextAlignment DEFAULT_ALIGNMENT = TextAlignment.TOP_LEFT;

    protected final Font font;

    protected TextAlignment align;

    protected String text;

    /**
     * 为此<code>Text</code>控件定义一个宽度限制，例如 像素，而不是字形或字符数。
     * 如果值> 0， 则将根据需要对该行进行换行，以满足此约束。
     */
    protected float wrappingWidth;

    public Text() {
        super();
        this.font  = new Font();
        this.align = DEFAULT_ALIGNMENT;
    }

    public Text(String text) {
        super();
        Objects.requireNonNull(text);
        this.font  = new Font();
        this.text  = text;
        this.align = DEFAULT_ALIGNMENT;
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        // 必需的属性，不会对此类属性进行检查，这意味着此类属性只要有任何的不规范就会形成异常
        this.text = object.get("text").getAsString();

        // 非必需属性（带默认值）,此类属性如果为空或是错误，会直接跳过
        JsonElement fontSizeElement = object.get("font-size");
        JsonElement fontFaceElement = object.get("font-face");
        JsonElement fontColorElement = object.get("font-color");
        JsonElement alignElement = object.get("align");
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

    @Override
    public void draw(float offsetX, float offsetY, Canvas canvas) {
        if (this.text != null) {
            canvas.setFont(font);
            canvas.textAlign(align);

            float x = position.x + offsetX;
            float y = position.y + offsetY;
            if (wrappingWidth != 0) {
                canvas.textBox(x, y, wrappingWidth, text);
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    // 测量Text的文本区域边界
                    FloatBuffer boundBuffer = stack.mallocFloat(4);
                    canvas.textBoxBounds(x, y, wrappingWidth, text, boundBuffer);
                    size.x = boundBuffer.get(2) - boundBuffer.get(0);
                    size.y = boundBuffer.get(3) - boundBuffer.get(1);
                }
            } else {
                canvas.drawText(text, x, y);
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
