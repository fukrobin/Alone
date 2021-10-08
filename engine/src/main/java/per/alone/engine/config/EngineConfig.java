package per.alone.engine.config;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

/**
 * 引擎配置
 *
 * @author fkrobin
 * @date 2021/10/7 20:10
 */
@Setter
@Getter
public class EngineConfig {
    private static final Splitter splitter = Splitter.on(',')
                                                     .trimResults()
                                                     .omitEmptyStrings();

    private Collection<Class<?>> beanClasses = new HashSet<>();

    private Collection<Class<?>> packages = new HashSet<>();

    void beanClasses(String beanClasses) {
        Iterable<String> configValues = splitter.split(beanClasses);
        for (String beanClass : configValues) {
            try {
                this.beanClasses.add(Class.forName(beanClass));
            } catch (ClassNotFoundException ignored) {

            }
        }
    }

    void packages(String packages) {
        Iterable<String> configValues = splitter.split(packages);
        for (String packageClass : configValues) {
            try {
                this.packages.add(Class.forName(packageClass));
            } catch (ClassNotFoundException ignored) {

            }
        }
    }
}
