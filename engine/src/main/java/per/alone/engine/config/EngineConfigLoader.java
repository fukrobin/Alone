package per.alone.engine.config;

import lombok.extern.slf4j.Slf4j;
import per.alone.engine.util.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * 从 "META-INF/engine.config" 路径加载配置，配置文件为 properties 文件，
 * value 可以用 ',' 分割传递多个值。
 *
 * @author fkrobin
 * @date 2021/10/3 18:06
 */
@Slf4j
public final class EngineConfigLoader {

    /**
     * The location to look for factories.
     * <p>Can be present in multiple JAR files.
     */
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/engine.config";

    private EngineConfigLoader() {
    }

    public static EngineConfig load() {
        return load(EngineConfigLoader.class.getClassLoader(), new EngineConfig());
    }

    public static EngineConfig load(ClassLoader classLoader, EngineConfig engineConfig) {
        Class<EngineConfig> engineConfigClass = EngineConfig.class;
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = Utils.loadProperties(url);
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String configName = ((String) entry.getKey()).trim();

                    try {
                        Field field = engineConfigClass.getDeclaredField(configName);
                        field.setAccessible(true);
                        getMethod(engineConfigClass, field).invoke(engineConfig, entry.getValue());
                    } catch (ReflectiveOperationException ignored) {

                    }
                }
            }

            return engineConfig;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load engine config from location [" +
                                               FACTORIES_RESOURCE_LOCATION + "]", ex);
        }
    }

    private static Method getMethod(Class<?> clazz, Field field) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(field.getName(), String.class);
    }
}
