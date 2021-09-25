package per.alone.engine.scene;

import per.alone.engine.scene.voxel.Agent;
import per.alone.engine.scene.voxel.Chunk;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author fkobin
 * @date 2020/4/5 01:00
 **/
public class VoxelScene extends Scene {
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static final float CAMERA_POS_STEP = 0.30f;

    private final LinkedList<Chunk> chunkList;

    private final LinkedList<Model> modelList;

    private SkyBox skyBox;

    private Agent agent;

    // TODO private EventHandler<KeyEvent> cameraMoveHandler;

    // TODO private EventHandler<MouseEvent> cameraRotateHandler;

    private double lastCameraX, lastCameraY;

    public VoxelScene() {
        chunkList = new LinkedList<>();
        modelList = new LinkedList<>();
        camera    = new PerspectiveCamera(60);
    }

    public Camera getCamera() {
        return camera;
    }

    public void bindPlayerCamera(PerspectiveCamera camera) {
        Objects.requireNonNull(camera);

        this.camera = camera;
    }

    public VoxelScene setAgent(Agent agent) {
        this.agent = agent;
        return this;
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
