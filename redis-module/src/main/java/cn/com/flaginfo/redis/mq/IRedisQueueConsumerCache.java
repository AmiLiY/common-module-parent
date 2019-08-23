package cn.com.flaginfo.redis.mq;

import java.util.Set;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public interface IRedisQueueConsumerCache extends IRedisDatabase {

    String MESSAGE_CACHE = "__@@RedisQueueMessageCache__";

    /**
     * 存入一个对象
     * @return cacheId
     */
    String add(RedisQueueMessageCache cacheMessage);

    /**
     * 删除一个对象
     */
    void remove(String cacheId);

    /**
     * 获取所有的缓存ID
     */
    Set<String> getAllCacheId();

    /**
     * 根据cacheId获取对象
     * @param cacheId
     * @return
     */
    RedisQueueMessageCache get(String cacheId);

    /**
     * 超时回调
     * @param cache
     */
    void timeoutCallback(RedisQueueMessageCache cache);

    /**
     * 当前记录数
     * @return
     */
    Long count();
}
