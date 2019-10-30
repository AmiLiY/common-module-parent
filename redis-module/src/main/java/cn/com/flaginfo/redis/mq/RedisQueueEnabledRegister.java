package cn.com.flaginfo.redis.mq;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public class RedisQueueEnabledRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder redisAutoConfiguration = BeanDefinitionBuilder.rootBeanDefinition(RedisQueueAutoConfiguration.class);
        registry.registerBeanDefinition(RedisQueueAutoConfiguration.class.getSimpleName(), redisAutoConfiguration.getBeanDefinition());
    }
}
