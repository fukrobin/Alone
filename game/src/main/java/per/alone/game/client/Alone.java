package per.alone.game.client;

import per.alone.engine.core.BaseEngine;
import per.alone.engine.core.EngineContext;
import per.alone.engine.scene.Scene;
import per.alone.engine.scene.SkyBox;
import per.alone.engine.scene.voxel.Chunk;
import per.alone.engine.scene.voxel.VoxelBehavior;
import per.alone.engine.scene.voxel.VoxelUtil;
import per.alone.engine.ui.GuiManager;
import per.alone.game.gui.GuiMainMenu;
import per.alone.stage.Window;

import java.util.Random;

/**
 * @author fkobin
 * @date 2020/4/4 21:25
 **/
public class Alone extends BaseEngine {

    public static void main(String[] args) {
        launch(args);
    }

    private void setScene(EngineContext engineContext) {
        SkyBox skyBox = new SkyBox(new String[]{
                "runtime/texture/panorama_0.png", "runtime/texture/panorama_2.png",
                "runtime/texture/panorama_4.png", "runtime/texture/panorama_5.png",
                "runtime/texture/panorama_3.png", "runtime/texture/panorama_1.png",});
        Scene scene = new Scene();
        byte stone;
        byte dirt;
        try {
            skyBox.init();
            stone = VoxelBehavior.getNewVoxelBehavior("runtime/texture/blocks/stone1.png");
            dirt  = VoxelBehavior.getNewVoxelBehavior("runtime/texture/blocks/dirt.png");
            Random random = new Random();
            int voxelCount = 0;
            Chunk[] chunks = new Chunk[1];
            for (short i = 0; i < chunks.length; i++) {
                chunks[i] = VoxelUtil.getNewChunk(i, (short) 0);
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                            double data = random.nextDouble();
                            voxelCount++;
                            if (data > 0.2) {
                                chunks[i].addVoxel(x, y, z, dirt);
                            } else {
                                chunks[i].addVoxel(x, y, z, stone);
                            }
                        }
                    }
                }
            }
            System.out.println("voxelCount = " + voxelCount);
            scene.addChunk(chunks);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scene.setSkyBox(skyBox);
        scene.setupDefaultCamera();
        scene.getCamera().setCameraPos(0, 0, -10);
        scene.getCamera().setRotation(0, 180);

        scene.addStaticModel("asserts/models/player/player.obj", "asserts/models/player/");

        engineContext.getSceneRenderer().setTarget(scene);
    }

    @Override
    public void errorCallback(Exception e) {

    }

    @Override
    public void start(String[] args, EngineContext engineContext, Window window) {
        window.setTitle("Scene Model Test");
        window.setSize(1000, 700);
        window.setCenter();
//        setScene(linker);

        GuiManager guiManager = engineContext.getGuiManager();
        guiManager.addGui(new GuiMainMenu());

//        SoundManager soundManager = linker.getSoundManager();
//        soundManager.init();
//        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);R
//        SoundBuffer buffBack;
//        try {
//            buffBack = new SoundBuffer("runtime/sounds/background.ogg");
//            soundManager.addSoundBuffer(buffBack);
//            SoundSource sourceBack = new SoundSource(true, true);
//            sourceBack.setBuffer(buffBack.getBufferId());
//            soundManager.addSoundSource("background-music", sourceBack);
//
//            sourceBack.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        window.show();
    }

    @Override
    public void run() {

    }

    @Override
    protected void cleanup() {

    }
}
