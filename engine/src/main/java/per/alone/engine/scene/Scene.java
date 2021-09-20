package per.alone.engine.scene;

import org.lwjgl.glfw.GLFW;
import per.alone.engine.event.EventHandler;
import per.alone.engine.scene.voxel.Agent;
import per.alone.engine.scene.voxel.Chunk;
import per.alone.stage.input.KeyEvent;
import per.alone.stage.input.MouseEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author fkobin
 * @date 2020/4/5 01:00
 **/
@SuppressWarnings("UnstableApiUsage")
public class Scene {
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static float             CAMERA_POS_STEP = 0.30f;

    private final  LinkedList<Chunk> chunkList;

    private final  LinkedList<Model> modelList;

    private SkyBox skyBox;

    private Camera camera;

    private Agent agent;

    private EventHandler<KeyEvent> cameraMoveHandler;

    private EventHandler<MouseEvent> cameraRotateHandler;

    private double lastCameraX, lastCameraY;

    public Scene() {
        chunkList = new LinkedList<>();
        modelList = new LinkedList<>();
        camera    = new Camera();
        camera.setFovy(60);
    }

    public Camera getCamera() {
        return camera;
    }

    public void bindPlayerCamera(Camera camera, EventHandler<MouseEvent> rotateCamera,
                                 EventHandler<KeyEvent> moveCamera) {
        Objects.requireNonNull(rotateCamera);
        Objects.requireNonNull(moveCamera);
        Objects.requireNonNull(camera);

        this.camera              = camera;
        this.cameraMoveHandler   = moveCamera;
        this.cameraRotateHandler = rotateCamera;
    }

    public EventHandler<MouseEvent> getCameraRotateHandler() {
        if (cameraRotateHandler == null) {
            setupCameraRotateHandler();
        }
        return cameraRotateHandler;
    }

    public EventHandler<KeyEvent> getCameraMoveHandler() {
        if (cameraMoveHandler == null) {
            setupCameraMoveHandler();
        }
        return cameraMoveHandler;
    }

    public Scene setAgent(Agent agent) {
        this.agent = agent;
        return this;
    }

    private void setupCameraMoveHandler() {
        cameraMoveHandler = event -> {
            if (event.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                CAMERA_POS_STEP = 0.6f;
            } else {
                CAMERA_POS_STEP = 0.3f;
            }

            int x = 0, y = 0, z = 0;
            if (event.isKeyPressed(GLFW.GLFW_KEY_W)) {
                z = -1;
            } else if (event.isKeyPressed(GLFW.GLFW_KEY_S)) {
                z = 1;
            }

            if (event.isKeyPressed(GLFW.GLFW_KEY_A)) {
                x = -1;
            } else if (event.isKeyPressed(GLFW.GLFW_KEY_D)) {
                x = 1;
            }

            if (event.isKeyPressed(GLFW.GLFW_KEY_Z)) {
                y = -1;
            } else if (event.isKeyPressed(GLFW.GLFW_KEY_X)) {
                y = 1;
            }

            camera.movePosition(x * CAMERA_POS_STEP, y * CAMERA_POS_STEP, z * CAMERA_POS_STEP);
        };
    }

    private void setupCameraRotateHandler() {
        // 初始化last camera的位置信息。
        MouseEvent event = EngineThread.getThreadWindow().getMouseEvent();
        lastCameraX = event.getCursorPosX();
        lastCameraY = event.getCursorPosY();

        cameraRotateHandler = mouseEvent -> {
            if (mouseEvent.isHiddenCursor() && mouseEvent.isInWindow()) {
                double deltaX = mouseEvent.getCursorPosX() - lastCameraX;
                double deltaY = mouseEvent.getCursorPosY() - lastCameraY;
                if (deltaX != 0 || deltaY != 0) {
                    deltaX *= MOUSE_SENSITIVITY;
                    deltaY *= MOUSE_SENSITIVITY;
                    // X方向的偏移相当于绕Y轴旋转， Y轴偏移相当于绕X轴旋转
                    camera.rotateOffset(deltaY, deltaX);
                }
            }
            lastCameraX = mouseEvent.getCursorPosX();
            lastCameraY = mouseEvent.getCursorPosY();
        };
    }

    public void setupDefaultCamera() {

        setupCameraMoveHandler();
        setupCameraRotateHandler();
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public void addChunk(Chunk... chunks) {
        chunkList.addAll(Arrays.asList(chunks));
    }

    public List<Chunk> getChunkList() {
        return chunkList;
    }

    public void addStaticModel(String path, String textureDir) {
        Model model = new Model(path, textureDir);
        model.loadStaticModel();

        modelList.add(model);
    }

    public List<Model> getModels() {
        return modelList;
    }

    public void cleanUp() {
        skyBox.cleanUp();
    }
}
