package per.alone.engine.ui;

import per.alone.engine.scene.Scene;
import per.alone.engine.ui.control.Region;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/25 21:04
 */
public class SimpleScene extends Scene {
    private Region root;

    public Region getRoot() {
        return root;
    }

    public void setRoot(Region root) {
        this.root = root;
        this.root.setScene(this);
    }
}
