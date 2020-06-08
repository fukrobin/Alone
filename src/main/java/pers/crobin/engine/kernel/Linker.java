package pers.crobin.engine.kernel;

import pers.crobin.engine.renderer.GuiRenderer;
import pers.crobin.engine.renderer.RendererManager;
import pers.crobin.engine.renderer.SceneRenderer;
import pers.crobin.engine.sound.SoundManager;
import pers.crobin.engine.ui.GuiManager;

/**
 * @Author CRobin
 * @Date 2020/4/4 21:24
 **/
public final class Linker {
    private final GuiRenderer   guiRenderer;
    private final SceneRenderer sceneRenderer;

    private final GuiManager      guiManager;
    private final RendererManager rendererManager;
    private final SoundManager    soundManager;

    /**
     * 不应该被实例化
     */
    Linker() {
        guiManager      = new GuiManager();
        rendererManager = new RendererManager();
        sceneRenderer   = new SceneRenderer();
        guiRenderer     = new GuiRenderer(guiManager.getGuiList());
        soundManager    = new SoundManager();
    }

    protected void init() {
        guiManager.init();
        soundManager.init();

        rendererManager.bindSceneRenderer(sceneRenderer);
        rendererManager.bindGuiRenderer(guiRenderer);

        rendererManager.init();
    }

    ////////////////////
    //// Manager
    ///////////////////

    public RendererManager getRendererManager() {
        return rendererManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    ////////////////////
    //// Renderer
    ////////////////////

//    GuiRenderer cannot be accessed in the current version.
//    public GuiRenderer getGuiRenderer() {
//        return guiRenderer;
//    }

    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public void cleanup() {
        soundManager.cleanup();
        guiManager.cleanup();
        rendererManager.cleanup();
    }
}
