package per.alone.engine.ui.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import per.alone.engine.geometry.Bounds;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.text.Font;
import per.alone.engine.ui.text.TextAlignment;
import per.alone.engine.util.Utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Administrator
 */
public class Icon extends BaseControl {
    private static final Map<Integer, ByteBuffer> CODE_POINT_ICON = new HashMap<>(16);

    protected final Font font;

    protected       int        codePoint;

    /**
     * 本质上是图标字体Unicode代码点的bytebuffer表示形式
     */
    private         ByteBuffer iconBuffer;

    /**
     * 无参构造器，此构造器不做任何的多余事情，包括变量初始化
     */
    Icon() {
        super();
        font = new Font(14, "icons", Utils.hexColorToRgba("#ABD8ED"));
    }

    public Icon(int codePoint) {
        super();
        if (codePoint >= 0 && codePoint <= 0xf2e0) {
            this.codePoint  = codePoint;
            this.iconBuffer = CODE_POINT_ICON.computeIfAbsent(codePoint, Utils::cpToUtf8);
        } else {
            throw new IllegalArgumentException("The code point of the icon can only be between 0xf000 and 0xf2e0");
        }
        font = new Font(14, "icons", Utils.hexColorToRgba("#ABD8ED"));
    }

    public int getCodePoint() {
        return codePoint;
    }

    public Icon setCodePoint(int codePoint) {
        if (this.codePoint != codePoint) {
            this.codePoint  = codePoint;
            this.iconBuffer = CODE_POINT_ICON.computeIfAbsent(codePoint, Utils::cpToUtf8);
        }
        return this;
    }

    public ByteBuffer getIconBuffer() {
        return iconBuffer;
    }

    @Override
    public Bounds getLayoutBounds() {
        size.set(font.getFontSize());
        return super.getLayoutBounds();
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        if (iconBuffer != null) {
            Canvas.setFont(font, TextAlignment.TOP_LEFT);
            Canvas.drawText(iconBuffer, position.x + offsetX, position.y + offsetY);
        }
    }

    public Font getFont() {
        return font;
    }

    @Override
    protected JsonObject getJsonObject() {
        JsonObject iconTextObject = new JsonObject();

        iconTextObject.addProperty("icon", Integer.toHexString(codePoint));
        iconTextObject.addProperty("icon-size", font.getFontSize());
        iconTextObject.addProperty("icon-color", Utils.rgbToHexColorString(font.getColor()));
        iconTextObject.addProperty("layout-x", position.x);
        iconTextObject.addProperty("layout-y", position.y);

        return iconTextObject;
    }

    @Override
    protected String toJsonString() {
        return getJsonObject().toString();
    }


    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        this.setCodePoint(Integer.parseUnsignedInt(object.get("icon").getAsString(), 16));
        JsonElement iconSizeElement = object.get("icon-size");
        JsonElement iconColorElement = object.get("icon-color");

        if (iconSizeElement != null && !iconSizeElement.isJsonNull()) {
            this.font.setFontSize(iconSizeElement.getAsInt());
        }

        if (iconColorElement != null && !iconColorElement.isJsonNull()) {
            this.font.setColor(Utils.hexColorToRgba(iconColorElement.getAsString()));
        }
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
        Icon icon = (Icon) o;
        return codePoint == icon.codePoint &&
               font.equals(icon.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), font, codePoint);
    }
}
