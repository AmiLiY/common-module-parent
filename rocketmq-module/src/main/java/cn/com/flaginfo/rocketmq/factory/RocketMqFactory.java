package cn.com.flaginfo.rocketmq.factory;

import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.rocketmq.config.MqConfiguration.Rocket;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import cn.com.flaginfo.rocketmq.factory.producer.RocketMqProducer;
import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.UUID;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
@Slf4j
public class RocketMqFactory implements IMqFactory<DefaultMQPushConsumer, MQPullConsumerScheduleService> {

    private Rocket rocketMqConfig;

    public RocketMqFactory(Rocket rocketMqConfig) {
        this.rocketMqConfig = rocketMqConfig;
    }

    /**
     * 生产者
     *
     * @return
     */
    @Override
    public RocketMqTemplate producer() {
        try {
            System.setProperty("client.logFileMaxIndex", "10");
            DefaultMQProducer rocketMqProducer = new DefaultMQProducer();
            rocketMqProducer.setProducerGroup(rocketMqConfig.getProducerGroup());
            if (log.isDebugEnabled()) {
                log.debug("rocket producer name address:{}", rocketMqConfig.getAddress());
            }
            rocketMqProducer.setNamesrvAddr(rocketMqConfig.getAddress());
            rocketMqProducer.setInstanceName(RocketMqProducer.class.getSimpleName() + StringPool.AT + UUID.randomUUID().hashCode());
            rocketMqProducer.setHeartbeatBrokerInterval(rocketMqConfig.getHeartbeatBrokerInterval());
            rocketMqProducer.setMaxMessageSize(rocketMqConfig.getMaxMessageSize());
            rocketMqProducer.setVipChannelEnabled(rocketMqConfig.getVipChannelEnabled());
            rocketMqProducer.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    rocketMqProducer.shutdown();
                    log.info("rocket mq producer shutdown");
                    super.run();
                }
            });
            if (log.isDebugEnabled()) {
                log.debug("init rocket mq success");
            }
            return new RocketMqProducer(rocketMqProducer);
        } catch (MQClientException e) {
            log.error("MQClientException", e);
            throw new MqRuntimeException("init rocket mq producer error, please check your config.");
        }
    }

    /**
     * 按照groupName生成Consumer
     *
     * @param groupName
     * @return
     */
    @Override
    public synchronized DefaultMQPushConsumer pushConsumer(String groupName, String messageModel) {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer();
        pushConsumer.setVipChannelEnabled(rocketMqConfig.getVipChannelEnabled());
        pushConsumer.setConsumerGroup(groupName);
        pushConsumer.setNamesrvAddr(rocketMqConfig.getAddress());
        log.info("init mq push consumer, address:{}, group:{}", rocketMqConfig.getAddress(), groupName);
        pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        pushConsumer.setInstanceName("MQ_PUSH_" + UUID.randomUUID().toString());
        pushConsumer.setMessageModel(this.messageModel(messageModel));
        pushConsumer.setConsumeThreadMin(rocketMqConfig.getMinThread());
        pushConsumer.setConsumeThreadMax(rocketMqConfig.getMinThread());
        pushConsumer.setConsumeMessageBatchMaxSize(1);
        return pushConsumer;
    }

    /**
     * 按照groupName生成Consumer
     *
     * @param groupName
     * @return
     */
    public synchronized MQPullConsumerScheduleService pullConsumer(String groupName, String messageModel) {
        MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService(groupName);
        DefaultMQPullConsumer defaultMQPullConsumer = scheduleService.getDefaultMQPullConsumer();
        defaultMQPullConsumer.setVipChannelEnabled(rocketMqConfig.getVipChannelEnabled());
        defaultMQPullConsumer.setNamesrvAddr(rocketMqConfig.getAddress());
        defaultMQPullConsumer.setMessageModel(this.messageModel(messageModel));
        defaultMQPullConsumer.setInstanceName("MQ_PULL_" + UUID.randomUUID().toString());
        scheduleService.setMessageModel(this.messageModel(messageModel));
        scheduleService.setPullThreadNums(rocketMqConfig.getPullThreadNum());
        return scheduleService;
    }

    private MessageModel messageModel(String model){
        for (MessageModel messageModel : MessageModel.values()) {
            if( messageModel.getModeCN().equals(model) ){
                return messageModel;
            }
        }
        throw new MqRuntimeException("this message module ["+ model +"] is not support, it must be one of org.apache.rocketmq.common.protocol.heartbeat.MessageModel");
    }

}
