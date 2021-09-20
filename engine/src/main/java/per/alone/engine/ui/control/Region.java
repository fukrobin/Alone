package per.alone.engine.ui.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector4i;
import per.alone.engine.ui.Canvas;
import per.alone.engine.util.Utils;

import java.util.Objects;

/**
 * @author Administrator
 */
public class Region extends BaseControl {
    protected final Vector4i borderColor;

    protected final Vector4i backgroundColor;

    protected int radius = 3;

    protected int border = 1;

    public Region() {
        super();
        borderColor     = new Vector4i();
        backgroundColor = new Vector4i();
        Utils.hexColorToRgba("#ABD8ED", backgroundColor);
        Utils.hexColorToRgba("#ececec", borderColor);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    ///////////////////////////////
    /// Background
    ///////////////////////////////

    public Vector4i getBackgroundColor() {
        return backgroundColor;
    }

    public <T extends Region> T setBackgroundColor(Vector4i backgroundColor) {
        this.backgroundColor.set(backgroundColor);
        return (T) this;
    }

    public <T extends Region> T setBackground(String hexColor) {
        backgroundColor.set(Utils.hexColorToRgba(hexColor));
        return (T) this;
    }

    public <T extends Region> T setBackground(int r, int g, int b, int a) {
        this.backgroundColor.set(r, g, b, a);
        return (T) this;
    }

    ///////////////////////////////
    /// Border
    ///////////////////////////////

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public Vector4i getBorderColor() {
        return borderColor;
    }

    public Region setBorderColor(Vector4i background) {
        this.borderColor.set(background);
        return this;
    }

    public <T extends Region> T setBorderColor(int r, int g, int b, int a) {
        this.borderColor.set(r, g, b, a);

        Region region = this.setPosition(10, 10);
        return (T) this;
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        Canvas.fillColor(backgroundColor);
        Canvas.drawRoundingRect(position.x + offsetX, position.y + offsetY, size.x, size.y, radius);

        Canvas.strokeColor(borderColor);
        Canvas.strokeWidth(border);
        Canvas.stroke();
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject regionObject = new JsonObject();

        regionObject.addProperty("layout-x", position.x);
        regionObject.addProperty("layout-y", position.y);
        regionObject.addProperty("width", size.x);
        regionObject.addProperty("height", size.y);
        regionObject.addProperty("radius", radius);
        regionObject.addProperty("border", border);
        regionObject.addProperty("background-color", Utils.rgbToHexColorString(backgroundColor));
        regionObject.addProperty("border-color", Utils.rgbToHexColorString(borderColor));

        return regionObject;
    }

    @Override
    public String toJsonString() {
        return getJsonObject().toString();
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);
        // 必需的属性，不会对此类属性进行检查，这意味着只要有任何的不规范就会形成异常
        this.size.x = object.get("width").getAsFloat();
        this.size.y = object.get("height").getAsFloat();

        // 非必需属性（带默认值）,此类属性如果为空或是错误，会直接跳过
        JsonElement radiusElement = object.get("radius");
        JsonElement borderElement = object.get("border");
        JsonElement backgroundElement = object.get("background-color");
        JsonElement borderColorElement = object.get("border-color");
        if (radiusElement != null && !radiusElement.isJsonNull()) {
            this.radius = radiusElement.getAsInt();
        }

        if (borderElement != null && !borderElement.isJsonNull()) {
            this.border = borderElement.getAsInt();
        }

        if (backgroundElement != null && !backgroundElement.isJsonNull()) {
            this.backgroundColor.set(Utils.hexColorToRgba(backgroundElement.getAsString()));
        }


        if (borderColorElement != null && !borderColorElement.isJsonNull()) {
            this.borderColor.set(Utils.hexColorToRgba(borderColorElement.getAsString()));
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
        Region region = (Region) o;
        return radius == region.radius &&
               border == region.border &&
               borderColor.equals(region.borderColor) &&
               backgroundColor.equals(region.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), borderColor, backgroundColor, radius, border);
    }
}
