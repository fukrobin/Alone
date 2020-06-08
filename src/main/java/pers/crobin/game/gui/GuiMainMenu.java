package pers.crobin.game.gui;

import pers.crobin.engine.kernel.EngineThread;
import pers.crobin.engine.kernel.Linker;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.scene.Scene;
import pers.crobin.engine.scene.SkyBox;
import pers.crobin.engine.scene.voxel.Chunk;
import pers.crobin.engine.scene.voxel.VoxelBehavior;
import pers.crobin.engine.scene.voxel.VoxelUtil;
import pers.crobin.engine.ui.BaseGui;
import pers.crobin.engine.ui.control.Button;
import pers.crobin.engine.ui.control.IconText;
import pers.crobin.engine.ui.control.TextField;
import pers.crobin.engine.ui.text.Text;
import pers.crobin.engine.util.Utils;

import java.util.Random;

/**
 * @author Administrator
 */
public class GuiMainMenu extends BaseGui {
    private Button create;
    private Button cancel;

    public GuiMainMenu() {
        super();

        parent.setSize(400, 500).setPosition(100, 100);
        parent.setBackground("#d5f3f4");
    }

    @Override
    public void start() {
        create = new Button("Create")
                .setPosition(50, 420)
                .setSize(120, 40)
                .setBackground("#0d9c9e");
        cancel = new Button("Cancel")
                .setPosition(230, 420)
                .setSize(120, 40)
                .setBackground("#0d9c9e");

        create.getTextControl().getFont().setFontSize(20);
        cancel.getTextControl().getFont().setFontSize(20);
        parent.addChildren(create, cancel);

        TextField worldName = new TextField()
                .setBackground(204, 228, 255, 255)
                .setBorderColor(12, 104, 204, 255)
                .setSize(300, 30)
                .setPosition(50, 50);
        TextField worldSeed = new TextField()
                .setBackground(204, 228, 255, 255)
                .setBorderColor(12, 104, 204, 255)
                .setSize(300, 30)
                .setPosition(50, 110);

        Text worldNameTip = new Text("Will be saved in: ").setPosition(50, 85);
        Text worldSeedTip = new Text("Leave blank for a random seed").setPosition(50, 145);
        Text cheatTip     = new Text("Commands like /gamemode, /xp").setPosition(80, 300);
        Text bonusBoxTip  = new Text("Some basic items comes with birth").setPosition(80, 340);
        worldNameTip.getFont().setFontSize(20).setColor(0, 0, 0, 255);
        worldSeedTip.getFont().setFontSize(20).setColor(0, 0, 0, 255);
        cheatTip.getFont().setColor(0, 0, 0, 255);
        bonusBoxTip.getFont().setColor(0, 0, 0, 255);
        parent.addChildren(worldName, worldSeed, worldNameTip, worldSeedTip, cheatTip, bonusBoxTip);

        IconText bonusBox     = new IconText(0xf046, "Bonus box", 20).setPosition(50, 315);
        IconText structure    = new IconText(0xf046, "Generate Structures", 20).setPosition(50, 245);
        IconText cheat        = new IconText(0xf096, "Allow Cheats", 20).setPosition(50, 275);
        IconText defaultWorld = new IconText(0xf192, "Default", 20).setPosition(50, 363);
        IconText flatWorld    = new IconText(0xf10c, "Flat", 20).setPosition(160, 363);
        IconText magnifyWorld = new IconText(0xf10c, "Magnify", 20).setPosition(250, 363);
        bonusBox.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        structure.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        cheat.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        defaultWorld.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        flatWorld.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        magnifyWorld.getFont().setColor(Utils.hexColorToRgba("#1585ff"));
        parent.addChildren(bonusBox, structure, cheat, defaultWorld, flatWorld, magnifyWorld);

        setEvent();
    }

    private void setEvent() {
        Window window = EngineThread.getThreadWindow();
        create.setMouseClickedEvent(event -> {
            setScene(EngineThread.getLinker());
            window.disableCursor();
            setDisable(true);
        });

        cancel.setOnAction(event -> System.out.println("Cancel clicked"));

        window.addMouseButtonCallback((window1, button, action, mods) -> {
            if (window.cursorIsVisible() && disable) {
                window.disableCursor();
            }
        });
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
    public void draw(long context, Window window) {
        float x = (window.getWidth() - parent.getSize().x) * 0.5f;
        float y = (window.getHeight() - parent.getSize().y) * 0.5f;
        parent.setPosition(x, y);

        parent.draw(context, 0, 0);
    }

    @Override
    public void cleanup() {

    }
}
