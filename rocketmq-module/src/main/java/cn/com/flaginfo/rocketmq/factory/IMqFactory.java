package cn.com.flaginfo.rocketmq.factory;

import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
public interface IMqFactory<PS, PL> {

    /**
     * 生产者
     * @return
     */
    RocketMqTemplate producer();


    /**
     * 消费者
     * @param groupName
     * @param messageModel
     * @return
     */
    PL pullConsumer(String groupName, String messageModel);

    /**
     * 消费者
     * @param groupName
     * @param messageModel
     * @return
     */
    PS pushConsumer(String groupName, String messageModel);
}
