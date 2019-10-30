package cn.com.flaginfo.rocketmq.boot;

import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.config.ConsumerType;
import cn.com.flaginfo.rocketmq.domain.MqInvokeMethodDefine;
import cn.com.flaginfo.rocketmq.domain.TopicMapping;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import cn.com.flaginfo.rocketmq.factory.IMqFactory;
import cn.com.flaginfo.rocketmq.loader.INameGenerator;
import cn.com.flaginfo.rocketmq.loader.MqConsumerLoader;
import cn.com.flaginfo.rocketmq.message.RocketMqMessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 下午3:28
 */
@Slf4j
public class RocketMqConsumerBoot extends AbstractConsumerBoot<DefaultMQPushConsumer, MQPullConsumerScheduleService> {

    private Set<MQPushConsumer> pushCache = new HashSet<>();

    private Set<MQPullConsumerScheduleService> pullCache = new HashSet<>();

    public RocketMqConsumerBoot(IMqFactory<DefaultMQPushConsumer, MQPullConsumerScheduleService> iMqFactory, MqConsumerLoader consumerLoader, INameGenerator nameGenerator) {
        super(iMqFactory, consumerLoader, nameGenerator);
    }

    @Override
    protected void initConsumerWithRocketTopic(String groupName, RocketTopic topic, MqInvokeMethodDefine invokeMethodDefine) {
        switch (topic.consumerType()) {
            case Push:
                this.initPushConsumer(groupName, topic, invokeMethodDefine);
                break;
            case Pull:
                this.initPullConsumer(groupName, topic, invokeMethodDefine);
                break;
            default:
                log.warn("consumer type is not support, topic: [{}], tag: [{}]", topic.title(), topic.tag());
        }
    }

    private void initPullConsumer(String groupName, RocketTopic topic, MqInvokeMethodDefine invokeMethodDefine) {
        MQPullConsumerScheduleService scheduleService = this.getPullConsumer(groupName, topic.messageModel());
        scheduleService.registerPullTaskCallback(topic.title(), new RocketMqPullTask(topic, invokeMethodDefine));
        try {
            scheduleService.start();
        } catch (MQClientException e) {
            log.error("start schedule error.");
            throw new MqRuntimeException(e);
        }
    }

    private void initPushConsumer(String groupName, RocketTopic topic, MqInvokeMethodDefine invokeMethodDefine) {
        try {
            DefaultMQPushConsumer pushConsumer = this.getPushConsumer(groupName, topic.messageModel());
            log.info("RocketMQ push consumer : {}, subscribe : {}, {}", groupName, topic.title(), topic.tag());
            pushConsumer.subscribe(topic.title(), topic.tag());
            pushConsumer.registerMessageListener(new RocketMqMessageAdapter(ConsumerType.Push, topic, invokeMethodDefine));
            pushConsumer.start();
        } catch (MQClientException e) {
            log.error("", e);
            throw new MqRuntimeException(e);
        }
    }

    @Override
    protected void stopPushConsumer(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.shutdown();
    }

    @Override
    protected void stopPullConsumer(MQPullConsumerScheduleService mqPullConsumerScheduleService) {
        mqPullConsumerScheduleService.shutdown();
    }
}
