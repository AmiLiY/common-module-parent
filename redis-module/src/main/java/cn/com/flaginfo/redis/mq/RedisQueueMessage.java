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
@ToString(callSuper = true)
public class RedisQueueMessage<D> implements Serializable {
    /**
     * 消息Id
     */
    private String messageId;
    /**
     * 消息发送时间戳
     */
    private Long timestamp;
    /**
     * 消息发送时间戳
     */
    private String topic;
    /**
     * 消息发送时间戳
     */
    private int database;
    /**
     * 消息重试次数
     */
    private Integer tryTimes;
    /**
     * 数据对象
     */
    private D message;
}
