package per.alone.engine.core;

import org.jboss.weld.environment.se.Weld;
import per.alone.engine.config.EngineConfig;
import per.alone.engine.config.EngineConfigLoader;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/10/7 16:57
 */
public class EngineContextFactory {

    private EngineContext engineContext;

    private EngineConfig engineConfig;

    public EngineContextFactory engineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
        return this;
    }

    public EngineContext build() {
        this.engineContext = new EngineContext();
        if (engineConfig == null) {
            engineConfig = EngineConfigLoader.load();
        }

        Weld weld = new Weld().disableDiscovery();

        weld.beanClasses(engineConfig.getBeanClasses().toArray(new Class<?>[]{}))
            .packages(engineConfig.getPackages().toArray(new Class<?>[]{}));
        engineContext.setWeld(weld);
        return engineContext;
    }
}
