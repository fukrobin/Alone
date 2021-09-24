package per.alone.engine.ui.control;

import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import per.alone.engine.ui.Canvas;
import per.alone.engine.ui.Color;
import per.alone.engine.ui.layout.Border;
import per.alone.engine.util.Utils;

/**
 * @author Administrator
 */
@Getter
public class Region extends Parent {
    protected Color backgroundColor;

    protected Border border;

    public Region() {
        super();
        backgroundColor = Color.parseHexColor("#ABD8ED");
        border = new Border(1, 3, Color.parseHexColor("#ececec"));
    }

    @Override
    public void draw(float offsetX, float offsetY, Canvas canvas) {
        canvas.fillColor(backgroundColor);
        canvas.drawRoundingRect(position.x + offsetX,
                                position.y + offsetY,
                                size.x, size.y,
                                border.getRadius());

        canvas.strokeColor(border.getColor());
        canvas.strokeWidth(border.getWidth());
        canvas.stroke();
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Region region = (Region) o;
        return Objects.equal(backgroundColor, region.backgroundColor) && Objects.equal(border, region.border);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), backgroundColor, border);
    }
}
