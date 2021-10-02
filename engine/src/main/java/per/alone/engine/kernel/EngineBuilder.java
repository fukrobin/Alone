package per.alone.engine.kernel;

import per.alone.engine.context.EngineContextEvent;
import per.alone.engine.context.EngineContextListener;
import per.alone.engine.renderer.RendererComponent;

import java.util.List;

public class EngineBuilder {

    private BaseEngine engine;

    private String[] args;

    private List<EngineComponent> engineComponents;

    private List<RendererComponent> rendererComponents;

    private List<EngineContextListener<? extends EngineContextEvent>> engineContextListeners;

    public EngineBuilder engine(BaseEngine engine) {
        this.engine = engine;
        return this;
    }

    public EngineBuilder args(String[] args) {
        this.args = args;
        return this;
    }

    public EngineBuilder engineComponents(List<EngineComponent> engineComponents) {
        this.engineComponents = engineComponents;
        return this;
    }

    public EngineBuilder rendererComponents(List<RendererComponent> rendererComponents) {
        this.rendererComponents = rendererComponents;
        return this;
    }

    public EngineBuilder engineContextListeners(
            List<EngineContextListener<? extends EngineContextEvent>> engineContextListeners) {
        this.engineContextListeners = engineContextListeners;
        return this;
    }

    public BaseEngine build() {
        if (engine == null) {
            throw new IllegalStateException("Engine cannot be null");
        }

        if (args != null) {
            engine.setArgs(args);
        }

        if (engineComponents != null) {
            engine.addEngineComponents(engineComponents);
        }

        if (rendererComponents != null) {
            engine.addRendererComponents(rendererComponents);
        }

        if (engineContextListeners != null) {
            engine.addEngineContextListeners(engineContextListeners);
        }

        return engine;
    }
}
