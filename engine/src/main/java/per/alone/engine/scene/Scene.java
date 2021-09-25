package per.alone.engine.scene;

import lombok.Getter;
import lombok.Setter;
import per.alone.engine.ui.Color;
import per.alone.stage.Window;

/**
 * 场景
 *
 * @author fkrobin
 * @date 2021/9/25 18:11
 */
@Getter
@Setter
public class Scene {

    protected Camera camera;

    private Window window;

    private Color fill;

    private float x;

    private float y;

    private float width;

    private float height;

    public Scene() {
        this(Color.BLACK, -1, -1);
    }

    public Scene(float width, float height) {
        this(Color.BLACK, width, height);
    }

    public Scene(Color fill, float width, float height) {
        this.fill   = fill;
        this.width  = width;
        this.height = height;
    }

    public Camera getCamera() {
        if (camera == null) {
            camera = new ParallelCamera();
        }
        return camera;
    }
}
