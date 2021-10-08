package per.alone.engine.ui;

import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;
import lombok.Data;
import lombok.ToString;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;
import per.alone.engine.core.EngineContextEvent;
import per.alone.engine.renderer.RendererManager;
import per.alone.event.AloneEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/10/6 17:24
 */
@Singleton
public class Hk2Test {

    @Inject
    private AloneEventListener<EngineContextEvent> aloneEventListener;

    @Test
    void test() {
        Weld weld = new Weld()
                .disableDiscovery()
                .beanClasses(RendererManager.class)
                .packages(User.class);

        WeldContainer container = weld.initialize();
        // ParameterizedType type = new ParameterizedTypeImpl(AloneEventListener.class, EngineEvent.class);

        System.out.println(container.select(Hk2Test.class).get().aloneEventListener);

        container.shutdown();
    }


    private interface Entity {

    }

    private interface JpaEntity extends Entity {

    }

    @Qualifier
    @Target({TYPE, METHOD, PARAMETER, FIELD})
    @Retention(RUNTIME)
    private @interface UserEntity {

    }

    @Data
    @ToString
    private static class User implements JpaEntity {
        private String name;

        private String password;
    }

    @Data
    @ToString
    private static class Phone implements JpaEntity {
        private String number;
    }
}
