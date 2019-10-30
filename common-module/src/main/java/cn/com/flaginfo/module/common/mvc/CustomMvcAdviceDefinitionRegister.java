package cn.com.flaginfo.module.common.mvc;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public class CustomMvcAdviceDefinitionRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomMvcAdapterAutoConfiguration.class);
        registry.registerBeanDefinition(CustomMvcAdapterAutoConfiguration.class.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());
    }

}
