package cn.com.flaginfo.rocketmq.boot;

import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.config.ConsumerType;
import cn.com.flaginfo.rocketmq.domain.MqInvokeMethodDefine;
import cn.com.flaginfo.rocketmq.domain.TopicMapping;
import cn.com.flaginfo.rocketmq.factory.IMqFactory;
import cn.com.flaginfo.rocketmq.loader.INameGenerator;
import cn.com.flaginfo.rocketmq.loader.MqConsumerLoader;
import cn.com.flaginfo.rocketmq.message.OnsMqMessageAdapter;
import com.aliyun.openservices.ons.api.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 上午10:42
 */
@Slf4j
public class OnsConsumerBoot extends AbstractConsumerBoot<Consumer, Object> {

    public OnsConsumerBoot(IMqFactory<Consumer, Object> iMqFactory, MqConsumerLoader consumerLoader, INameGenerator nameGenerator) {
        super(iMqFactory, consumerLoader, nameGenerator);
    }

    @Override
    protected void initConsumerWithRocketTopic(String groupName, RocketTopic topic, MqInvokeMethodDefine invokeMethodDefine) {
        Consumer pushConsumer = this.getPushConsumer(groupName, topic.messageModel());
        pushConsumer.subscribe(topic.title(), topic.tag(), new OnsMqMessageAdapter(ConsumerType.Compatible, topic, invokeMethodDefine));
        pushConsumer.start();
    }

    @Override
    protected void stopPushConsumer(Consumer consumer) {
        if( !consumer.isClosed() ){
            consumer.shutdown();
        }
    }

    @Override
    protected void stopPullConsumer(Object o) {
        return;
    }
}
