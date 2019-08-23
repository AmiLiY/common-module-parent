package cn.com.flaginfo.module.common.scan;

import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.module.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO: 根据自定义注解生成bean
 */
@Slf4j
public abstract class AbstractAnnotationBeanPostProcessor<A extends Annotation> implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanClassLoaderAware {

    public static final String ANNOTATION_BEAN_NAME = "annotationBean@";

    /**
     * spring 类加载器
     */
    private ClassLoader classLoader;

    /**
     * spring环境变量
     */
    private Environment environment;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        String scanPackage = this.getScanPackage(environment);
        if (StringUtils.isBlank(scanPackage)) {
            log.info("scan package is blank.");
            return;
        }
        // 扫描的包名
        AnnotationScanner annotationScanner = new AnnotationScanner(beanDefinitionRegistry, this.getAnnotation());
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        String[] packages = scanPackage.split(",");
        for (String pcg : packages) {
            if (StringUtils.isNotBlank(pcg)) {
                beanDefinitions.addAll(annotationScanner.findCandidateComponents(pcg));
            }
        }
        if (CollectionUtils.isEmpty(beanDefinitions)) {
            log.info("no beans for annotation {}.", this.getAnnotation());
            return;
        }
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = this.resolveClass(beanDefinition);
            A annotation = AnnotationUtils.findAnnotation(beanClass, this.getAnnotation());
            if (null == annotation) {
                continue;
            }
            if( !this.beforeBeanRegistryFilter(annotation, beanClass, beanDefinition) ){
                continue;
            }
            String beanName = this.generateBeanName(annotation, beanClass);
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            this.afterBeanRegistry(annotation, beanClass, beanName, beanDefinition);
        }
    }

    /**
     * 加载类对象
     *
     * @param beanDefinition
     * @return
     */
    private Class<?> resolveClass(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        if (null == beanClassName) {
            log.warn("bean class name is null.");
            return null;
        }
        return ClassUtils.resolveClassName(beanClassName, this.classLoader);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取需要扫描的bean
     *
     * @return
     */
    public abstract Class<A> getAnnotation();

    /**
     * 获取需要扫描目录
     *
     * @return
     */
    public abstract String getScanPackage(Environment environment);

    /**
     * 生成bean名称
     *
     * @param annotation
     * @param beanClass
     * @return
     */
    public String generateBeanName(A annotation, Class<?> beanClass){
        return ANNOTATION_BEAN_NAME + annotation.getClass().getName() + StringPool.AT + beanClass.getName();
    }

    /**
     * bean注册过滤器
     *
     * @param annotation
     * @param beanClass
     * @return true：注册  false：不注册
     */
    public boolean beforeBeanRegistryFilter(A annotation, Class<?> beanClass, BeanDefinition beanDefinition) {
        return true;
    }

    /**
     * bean注册之后的回调
     *
     * @param annotation
     * @param beanClass
     * @param beanName
     * @param beanDefinition
     */
    public void afterBeanRegistry(A annotation, Class<?> beanClass, String beanName, BeanDefinition beanDefinition) {

    }
}
