package per.alone.engine.ui;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import per.alone.engine.ui.control.Parent;
import per.alone.engine.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Administrator
 */
public class GuiJsonParser {
    private final String jsonString;

    public GuiJsonParser(String url) throws IOException {
        File file = new File(url);
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            jsonString = Utils.loadResource(new FileInputStream(file));
        } else {
            throw new IOException("Unable to read Gui's JSON configuration file.");
        }
    }

    public Parent parse() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parent.class, (JsonDeserializer<Parent>) (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonObject root = jsonObject.get("RootPane").getAsJsonObject();
            if (root != null) {
                Parent parent = new Parent();

                parent.setupFromJson(root);

                return parent;
            }

            return null;
        });

        return builder.create().fromJson(jsonString, Parent.class);
    }
}



























