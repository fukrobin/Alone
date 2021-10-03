package per.alone.engine;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import per.alone.engine.annotation.AnnotationOrderComparator;
import per.alone.engine.util.Utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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

    static final LoadingCache<ClassLoader, Map<String, List<String>>> cache =
            CacheBuilder.newBuilder().weakValues().build(new CacheLoader<ClassLoader, Map<String, List<String>>>() {
                @Override
                public Map<String, List<String>> load(ClassLoader classLoader) {
                    return doLoadFactories(classLoader);
                }
            });

    private EngineConfigLoader() {
    }

    public static <T> List<T> loadClass(Class<T> factoryType, @Nullable ClassLoader classLoader) {
        Objects.requireNonNull(factoryType, "'factoryType' must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = EngineConfigLoader.class.getClassLoader();
        }
        List<String> factoryImplementationNames = loadFactoryNames(factoryType, classLoaderToUse);
        log.trace("Loaded [" + factoryType.getName() + "] names: " + factoryImplementationNames);
        List<T> result = new ArrayList<>(factoryImplementationNames.size());
        for (String factoryImplementationName : factoryImplementationNames) {
            result.add(instantiateFactory(factoryImplementationName, factoryType, classLoaderToUse));
        }
        AnnotationOrderComparator.sort(result);
        return result;
    }

    public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = EngineConfigLoader.class.getClassLoader();
        }
        String factoryTypeName = factoryType.getName();
        return loadFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList());
    }

    private static Map<String, List<String>> loadFactories(ClassLoader classLoader) {
        try {
            return cache.get(classLoader);
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to load factories from location [" +
                                       FACTORIES_RESOURCE_LOCATION + "]", e);
        }
    }

    private static Map<String, List<String>> doLoadFactories(ClassLoader classLoader) {
        Map<String, List<String>> result = new HashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = Utils.loadProperties(url);
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String factoryTypeName = ((String) entry.getKey()).trim();
                    Iterable<String> factoryImplementationNames = Splitter.on(',')
                                                                          .trimResults()
                                                                          .omitEmptyStrings()
                                                                          .split((String) entry.getValue());
                    for (String factoryImplementationName : factoryImplementationNames) {
                        result.computeIfAbsent(factoryTypeName, key -> new ArrayList<>())
                              .add(factoryImplementationName.trim());
                    }
                }
            }

            // Replace all lists with unmodifiable lists containing unique elements
            result.replaceAll((factoryType, implementations) ->
                                      implementations.stream()
                                                     .distinct()
                                                     .collect(collectingAndThen(toList(),
                                                                                Collections::unmodifiableList)));
            return result;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [" +
                                               FACTORIES_RESOURCE_LOCATION + "]", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateFactory(String factoryImplementationName, Class<T> factoryType,
                                            ClassLoader classLoader) {
        try {
            Class<?> factoryImplementationClass = Class.forName(factoryImplementationName, true, classLoader);
            if (!factoryType.isAssignableFrom(factoryImplementationClass)) {
                throw new IllegalArgumentException(
                        "Class [" + factoryImplementationName + "] is not assignable to factory type [" +
                        factoryType.getName() + "]");
            }
            Constructor<?> constructor = factoryImplementationClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Throwable ex) {
            throw new IllegalArgumentException(
                    "Unable to instantiate factory class [" + factoryImplementationName + "] for factory type [" +
                    factoryType.getName() + "]",
                    ex);
        }
    }
}
