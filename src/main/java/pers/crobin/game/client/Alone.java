package pers.crobin.game.client;

import pers.crobin.engine.event.EventManager;
import pers.crobin.engine.event.IEvent;
import pers.crobin.engine.kernel.BaseEngine;
import pers.crobin.engine.kernel.EngineThread;
import pers.crobin.engine.kernel.Linker;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.scene.Scene;
import pers.crobin.engine.scene.SkyBox;
import pers.crobin.engine.scene.voxel.Chunk;
import pers.crobin.engine.scene.voxel.VoxelBehavior;
import pers.crobin.engine.scene.voxel.VoxelUtil;
import pers.crobin.engine.ui.GuiManager;
import pers.crobin.game.gui.GuiMainMenu;

import java.util.Random;

/**
 * @Author CRobin
 * @Date 2020/4/4 21:25
 **/
public class Alone extends BaseEngine {

    public static void main(String[] args) {
        launch(args);
    }

    private void setScene(Linker linker) {
        SkyBox skyBox = new SkyBox(new String[]{
                "runtime/texture/panorama_0.png", "runtime/texture/panorama_2.png",
                "runtime/texture/panorama_4.png", "runtime/texture/panorama_5.png",
                "runtime/texture/panorama_3.png", "runtime/texture/panorama_1.png",});
        Scene scene = new Scene();
        byte  stone;
        byte  dirt;
        try {
            skyBox.init();
            stone = VoxelBehavior.getNewVoxelBehavior("runtime/texture/blocks/stone1.png");
            dirt  = VoxelBehavior.getNewVoxelBehavior("runtime/texture/blocks/dirt.png");
            Random  random     = new Random();
            int     voxelCount = 0;
            Chunk[] chunks     = new Chunk[1];
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

        scene.addStaticModel("models/player/player.obj", "models/player/");

        linker.getSceneRenderer().setTarget(scene);
    }

    @Override
    public void errorCallback(Exception e) {

    }

    @Override
    public void start(String[] args, Linker linker, Window window) {
        window.setTitle("Scene Model Test");
        window.setSize(1000, 700);
        window.setCenter();
//        setScene(linker);

        GuiManager guiManager = linker.getGuiManager();
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
        EventManager manager = EngineThread.getEventManager();
        manager.addEvent(new CustomEvent());
        manager.register(event -> System.out.println("自定义事件处理程序触发"), CustomEvent.class);

        window.show();
    }

    @Override
    public void run() {

    }

    @Override
    protected void cleanup() {

    }

    public static class CustomEvent implements IEvent {
        private int count = 0;

        @Override
        public boolean isFired() {
            return (count % 30) == 0;
        }

        @Override
        public void update() {
            count++;
            System.out.println("自定义事件更新。");
        }
    }
}
