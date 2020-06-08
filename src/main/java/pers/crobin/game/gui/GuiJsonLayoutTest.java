package pers.crobin.game.gui;

import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.ui.BaseGui;
import pers.crobin.engine.ui.GuiJsonParser;

import java.io.IOException;

public class GuiJsonLayoutTest extends BaseGui {
    @Override
    protected void start() {

        GuiJsonParser jsonParser;
        try {
            jsonParser = new GuiJsonParser("asserts/ui/mainMenu.json");
            parent = jsonParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(long context, Window window) {
        if (window.isResized()) {
            float x = (window.getWidth() - parent.getSize().x) * 0.5f;
            float y = (window.getHeight() - parent.getSize().y) * 0.5f;
            parent.setPosition(x, y);
        }
        parent.draw(context, 0, 0);
    }

    @Override
    public void cleanup() {

    }
}
