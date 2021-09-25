package per.alone.engine.ui;

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
        // TODO JSON parse
        return null;
    }
}



























