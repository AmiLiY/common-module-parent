package cn.com.flaginfo.rocketmq.notification;

import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
@Configuration
public class MqNotificationAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnBean(RocketMqTemplate.class)
    public NotificationPointProcessor notificationPointProcessor(RocketMqTemplate rocketMqTemplate) {
        return new NotificationPointProcessor(rocketMqTemplate, applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
