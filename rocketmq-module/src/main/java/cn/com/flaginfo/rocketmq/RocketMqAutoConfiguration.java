package cn.com.flaginfo.rocketmq;

import cn.com.flaginfo.module.common.SpringApplicationStartedEvent;
import cn.com.flaginfo.rocketmq.boot.AbstractConsumerBoot;
import cn.com.flaginfo.rocketmq.boot.OnsConsumerBoot;
import cn.com.flaginfo.rocketmq.boot.RocketMqConsumerBoot;
import cn.com.flaginfo.rocketmq.config.MqConfiguration;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import cn.com.flaginfo.rocketmq.factory.IMqFactory;
import cn.com.flaginfo.rocketmq.factory.OnsMqFactory;
import cn.com.flaginfo.rocketmq.factory.RocketMqFactory;
import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;
import cn.com.flaginfo.rocketmq.loader.DefaultNameGenerator;
import cn.com.flaginfo.rocketmq.loader.INameGenerator;
import cn.com.flaginfo.rocketmq.loader.MqConsumerLoader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * 消费者工厂类
 *
 * @author: Meng.Liu
 * @date: 2018/11/29 上午10:56
 */
@Configuration
@Slf4j
@Setter
@ConditionalOnProperty(prefix = "spring.mq", name = "type")
@Import({MqConfiguration.class})
public class RocketMqAutoConfiguration implements ApplicationListener<SpringApplicationStartedEvent> {

    @Autowired
    private MqConfiguration mqConfiguration;

    private AbstractConsumerBoot abstractConsumerBoot;

    @Bean
    public IMqFactory mqFactory() {
        switch (mqConfiguration.getType()) {
            case ons:
                return new OnsMqFactory(mqConfiguration.getOns());
            case rocket:
                return new RocketMqFactory(mqConfiguration.getRocket());
        }
        throw new MqRuntimeException("init mq factory failed, unknown mq type.");
    }

    @Bean
    public RocketMqTemplate rocketMqProducer(IMqFactory mqFactory) {
        return mqFactory.producer();
    }

    @Bean
    @ConditionalOnMissingBean(INameGenerator.class)
    public INameGenerator nameGenerator() {
        return new DefaultNameGenerator();
    }

    @Bean
    public MqConsumerLoader mqConsumerLoader() {
        return new MqConsumerLoader(this.nameGenerator());
    }

    @Bean
    public AbstractConsumerBoot consumerBoot(IMqFactory iMqFactory, MqConsumerLoader mqConsumerLoader, INameGenerator nameGenerator) {
        switch (mqConfiguration.getType()) {
            case rocket:
                log.info("init Rocket Mq consumer boot.");
                abstractConsumerBoot = new RocketMqConsumerBoot(iMqFactory, mqConsumerLoader, nameGenerator);
                return abstractConsumerBoot;
            case ons:
                log.info("init Ons Mq consumer boot.");
                abstractConsumerBoot = new OnsConsumerBoot(iMqFactory, mqConsumerLoader, nameGenerator);
                return abstractConsumerBoot;
        }
        throw new MqRuntimeException("init mq factory failed, unknown mq type.");
    }

    @Override
    public void onApplicationEvent(SpringApplicationStartedEvent startedEvent) {
        log.info("start mq consumer.");
        this.abstractConsumerBoot.start();
    }
}
