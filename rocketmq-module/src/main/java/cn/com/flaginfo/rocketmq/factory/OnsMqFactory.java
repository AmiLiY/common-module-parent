package cn.com.flaginfo.rocketmq.factory;

import cn.com.flaginfo.rocketmq.config.MqConfiguration.Ons;
import cn.com.flaginfo.rocketmq.constants.Constants;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import cn.com.flaginfo.rocketmq.factory.producer.OnsMqProducer;
import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.UUID;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
@Slf4j
public class OnsMqFactory implements IMqFactory<Consumer, Object> {

    private Ons onsMqConfig;

    public OnsMqFactory(Ons onsMqConfig) {
        this.onsMqConfig = onsMqConfig;
    }

    @Override
    public synchronized RocketMqTemplate producer() {
        String groupName;
        if (null == onsMqConfig || StringUtils.isBlank(onsMqConfig.getProducerGroupName())) {
            groupName = this.getProducerId(UUID.randomUUID().toString());
        } else {
            groupName = this.getProducerId(onsMqConfig.getProducerGroupName());
        }
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsMqConfig.getAddress());
        properties.put(PropertyKeyConst.AccessKey, onsMqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsMqConfig.getSecretKey());
        properties.put(PropertyKeyConst.GROUP_ID, groupName);
        log.info("ons register producer group : [{}]", groupName);
        properties.put(PropertyKeyConst.isVipChannelEnabled, onsMqConfig.getVipChannelEnabled());
        Producer producer = ONSFactory.createProducer(properties);
        producer.start();
        log.info("init ons producer success.");
        return new OnsMqProducer(producer);
    }

    /**
     * 按照groupName生成Consumer
     *
     * @param groupName
     * @return
     */
    @Override
    public synchronized Consumer pushConsumer(String groupName, String messageModel) {
        Properties properties = new Properties();
        groupName = this.getConsumerId(groupName);
        properties.put(PropertyKeyConst.GROUP_ID, groupName);
        log.info("ons register consumer group : [{}]", groupName);
        properties.put(PropertyKeyConst.AccessKey, onsMqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsMqConfig.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsMqConfig.getAddress());
        properties.put(PropertyKeyConst.ConsumeThreadNums, onsMqConfig.getConsumeThreadNumber());
        properties.put(PropertyKeyConst.MessageModel, this.messageModel(messageModel).getModeCN());
        properties.put(PropertyKeyConst.isVipChannelEnabled, onsMqConfig.getVipChannelEnabled());
        return ONSFactory.createConsumer(properties);
    }

    @Override
    public Object pullConsumer(String groupName, String messageModel) {
        throw new MqRuntimeException("ons mq is not support pull model.");
    }

    private MessageModel messageModel(String model){
        for (MessageModel messageModel : MessageModel.values()) {
            if( messageModel.getModeCN().equals(model) ){
                return messageModel;
            }
        }
        throw new MqRuntimeException("this message module ["+ model +"] is not support, it must be one of com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel");
    }


    /**
     * 获取生产者Id
     *
     * @param groupName
     * @return
     */
    public String getProducerId(String groupName) {
        return Constants.PRODUCER_ID_PREFIX + groupName;
    }

    /**
     * 获取消费者ID
     *
     * @param groupName
     * @return
     */
    public String getConsumerId(String groupName) {
        return Constants.CONSUMER_ID_PREFIX + groupName;
    }
}
