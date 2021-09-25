package per.alone.engine.context;

import lombok.Getter;
import lombok.Setter;
import per.alone.engine.kernel.DebugInfo;
import per.alone.engine.renderer.CompositeRenderer;
import per.alone.engine.renderer.SceneRenderer;
import per.alone.engine.sound.SoundManager;
import per.alone.engine.ui.GuiRenderer;
import per.alone.event.EventHandlerManager;
import per.alone.event.EventQueue;

import java.io.Closeable;

/**
 * @author fkobin
 * @date 2020/4/4 21:24
 **/
@Getter
@Setter
public final class EngineContext implements Closeable {
    private final GuiRenderer guiRenderer;

    private final CompositeRenderer compositeRenderer;

    private final SceneRenderer sceneRenderer;

    private final SoundManager soundManager;

    private EventQueue eventQueue;

    private EventHandlerManager eventHandlerManager;

    private DebugInfo debugInfo;

    private int fps;

    private int ups;

    public EngineContext() {
        guiRenderer         = new GuiRenderer();
        compositeRenderer   = new CompositeRenderer();
        sceneRenderer       = new SceneRenderer();
        soundManager        = new SoundManager();
        eventHandlerManager = new EventHandlerManager();
        eventQueue          = new EventQueue(eventHandlerManager);
        debugInfo           = DebugInfo.getInstance();
    }

    @Override
    public void close() {
        soundManager.close();
        compositeRenderer.close();
    }
}
