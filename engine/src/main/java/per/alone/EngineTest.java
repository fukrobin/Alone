package per.alone;

import per.alone.engine.core.BaseEngine;
import per.alone.engine.core.EngineContext;
import per.alone.engine.ui.GuiRenderer;
import per.alone.engine.ui.SimpleScene;
import per.alone.engine.ui.control.Button;
import per.alone.stage.Window;

public class EngineTest extends BaseEngine {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void start(EngineContext engineContext, Window window) {
        GuiRenderer guiRenderer = engineContext.getComponent(GuiRenderer.class);
        SimpleScene scene = new SimpleScene();

        Button button = new Button();
        button.setPosition(100, 100);
        button.setSize(100, 100);
        button.setText("Hello Alone");

        scene.setRoot(button);
        guiRenderer.setScene(scene);

        window.show();
    }
}
