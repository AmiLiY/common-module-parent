package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.redis.RedisUtils;

import java.util.UUID;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public class RedisQueueTemplate {

    public <D> String send(String topic, D data) {
        return this.send(topic, 0, data);
    }

    public <D> String send(String topic, int database, D data) {
        return this.send(topic, database, data, null);
    }

    public <D> String send(String topic, int database, D data, Callback<D> callback) {
        RedisQueueMessage<D> message = new RedisQueueMessage<>();
        message.setMessageId(UUID.randomUUID().toString());
        message.setTopic(topic);
        message.setTryTimes(1);
        message.setDatabase(database);
        message.setTimestamp(System.currentTimeMillis());
        message.setMessage(data);
        RedisUtils.selectDatabase(database).getTemplate().opsForList().leftPush(topic, message);
        if( null != callback ){
            callback.callback(message);
        }
        return message.getMessageId();
    }

    public interface Callback<D> {
        /**
         * 消息发送回调
         * @param message
         */
        void callback(RedisQueueMessage<D> message);
    }

}
