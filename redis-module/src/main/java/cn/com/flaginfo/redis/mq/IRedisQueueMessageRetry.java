package cn.com.flaginfo.redis.mq;

import java.util.Set;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public interface IRedisQueueMessageRetry extends IRedisDatabase {

    String RETRY_MESSAGE_QUEUE = "__@@RedisQueueMessageRetryQueue__";

    /**
     * 增加至重试队列
     * @param message
     */
    void addToRetryQueue(RedisQueueMessage message);

    /**
     * 重试
     * @param message
     * @return  true:成功
     */
    boolean retryAgain(RedisQueueMessage message);

    /**
     * 删除
     * @param message
     */
    void removeFromRetryQueue(RedisQueueMessage message);

    /**
     * 获取需要重试的消息列表
     */
    Set<RedisQueueMessage> getNeedRetryMessages();

    /**
     * 是否失败
     * @param current 当前次数
     * @param threshold 重试阈值
     * @return true:失败，false:重试
     */
    boolean isFailed(int current, int threshold);
}
