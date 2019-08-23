package cn.com.flaginfo.module.common.scan;

import lombok.Setter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 注解扫描器
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public class AnnotationScanner extends ClassPathBeanDefinitionScanner {

    @Setter
    private Class<? extends Annotation> filterType;

    public AnnotationScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> clazz) {
        super(registry);
        this.filterType = clazz;
        this.setBeanNameGenerator(new AnnotationBeanNameGenerator());
        AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
        this.addIncludeFilter(new AnnotationTypeFilter(clazz));
    }

    // 以下为初始化后调用的方法
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition)
                && beanDefinition.getMetadata().hasAnnotation(this.filterType.getName());
    }
}
