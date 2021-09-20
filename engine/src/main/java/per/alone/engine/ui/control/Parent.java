package per.alone.engine.ui.control;

import com.google.gson.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 * @date 20/5/18/09点58分
 */
public class Parent extends Region {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String[] CONTROLS_PACKAGE = {"pers.crobin.per.fkrobin.engine.ui.control.",
            "pers.crobin.per.fkrobin.engine.ui.text."};

    /**
     * 子控件链表
     */
    private final        List<BaseControl> children;

    public Parent() {
        super();
        children = new LinkedList<>();
    }

    public void addChild(BaseControl control) {
        Objects.requireNonNull(control);
        control.setParent(this);
        children.add(control);
    }

    public void addChildren(BaseControl... controls) {
        for (BaseControl control : controls) {
            addChild(control);
        }
    }

    public List<BaseControl> getChildren() {
        return children;
    }

    public boolean remove(BaseControl control) {
        return children.remove(control);
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        super.draw(offsetX, offsetY);
        children.forEach(control -> control.draw(offsetX + this.position.x, offsetY + this.position.y));
    }

    @Override
    public String toJsonString() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parent.class, (JsonSerializer<Parent>) (src, typeOfSrc, context) -> {
            JsonObject root = new JsonObject();

            JsonObject parentObject = super.getJsonObject();
            if (!children.isEmpty()) {
                JsonObject childrenObject = new JsonObject();

                children.forEach(control -> {
                    JsonElement controlElement = childrenObject.get(control.getClass().getSimpleName());
                    JsonArray controlArray;
                    if (controlElement == null) {
                        childrenObject.add(control.getClass().getSimpleName(), controlArray = new JsonArray());
                    } else {
                        controlArray = controlElement.getAsJsonArray();
                    }
                    controlArray.add(control.getJsonObject());
                });

                parentObject.add("children", childrenObject);
            }

            root.add("RootPane", parentObject);

            return root;
        });
        return builder.create().toJson(this);
    }

    @Override
    public void setupFromJson(JsonObject object) {
        super.setupFromJson(object);

        JsonElement childrenElement = object.get("children");
        if (childrenElement != null && !childrenElement.isJsonNull()) {
            JsonObject childrenObject = childrenElement.getAsJsonObject();

            childrenObject.entrySet().forEach(entry -> {
                if (!entry.getValue().isJsonNull()) {

                    final Class<?> clazz;
                    try {
                        String key = entry.getKey();
                        // 判断是否是textb包下的控件，并根据结果添加包路径字符串
                        if ("Text".equals(key) || "Font".equals(key)) {
                            clazz = Class.forName(CONTROLS_PACKAGE[1] + key);
                        } else {
                            clazz = Class.forName(CONTROLS_PACKAGE[0] + key);
                        }
                        JsonArray controlArray = entry.getValue().getAsJsonArray();
                        controlArray.forEach(controlElement -> {
                            try {
                                BaseControl controlObject = (BaseControl) clazz.newInstance();
                                controlObject.setupFromJson(controlElement.getAsJsonObject());
                                controlObject.setParent(this);
                                this.children.add(controlObject);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Class [" + entry.getKey() + "] not found.", e);
                    }
                }
            });
        }
    }
}