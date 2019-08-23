package cn.com.flaginfo.redis.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
@Getter
@Setter
@ToString
public class RedisQueueMessageCache implements Serializable {

    /**
     * 消息缓存Id
     */
    private String cacheId;
    /**
     * 开始处理消息的时间戳
     */
    private long timestamp;
    /**
     * topic
     */
    private String topic;
    /**
     * database
     */
    private int database;
    /**
     * 数据对象
     */
    private RedisQueueMessage message;

}
