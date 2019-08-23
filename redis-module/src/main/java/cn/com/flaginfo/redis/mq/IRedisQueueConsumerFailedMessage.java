package cn.com.flaginfo.redis.mq;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public interface IRedisQueueConsumerFailedMessage extends IRedisDatabase {

    String FAILED_MESSAGE_CACHE = "__@@ConsumerFailedRedisQueueMessage__";

    /**
     * 随机获取一个失败消息对象
     * @return
     */
    RedisQueueMessage next();

    /**
     * 随机获取一个失败消息对象
     * @return
     */
    RedisQueueMessage remove();

    /**
     * 放入一个对象
     * @param message
     */
    void add(RedisQueueMessage message);

    /**
     * 失败总数
     * @return
     */
    Long count();
}
