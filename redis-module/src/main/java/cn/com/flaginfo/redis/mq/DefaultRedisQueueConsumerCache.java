package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.redis.RedisUtils;
import org.springframework.data.redis.core.HashOperations;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public class DefaultRedisQueueConsumerCache extends AbstractRedisQueueConsumerCache {

    private IRedisQueueMessageRetry redisQueueMessageRetry;

    public DefaultRedisQueueConsumerCache(IRedisQueueMessageRetry redisQueueMessageRetry) {
        this.redisQueueMessageRetry = redisQueueMessageRetry;
    }

    @Override
    public String add(@NotNull RedisQueueMessageCache cacheMessage) {
        String cacheId = UUID.randomUUID().toString();
        cacheMessage.setCacheId(cacheId);
        RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForHash().put(MESSAGE_CACHE, cacheId, cacheMessage);
        return cacheId;
    }

    @Override
    public void remove(@NotNull String cacheId) {
        RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForHash().delete(MESSAGE_CACHE, cacheId);
    }

    @Override
    public Long count() {
        return RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForHash().size(MESSAGE_CACHE);
    }

    @Override
    public Set<String> getAllCacheId() {
        HashOperations<String, String, Object> operations = RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForHash();
        return operations.keys(MESSAGE_CACHE);
    }

    @Override
    public RedisQueueMessageCache get(String cacheId) {
        Object object = RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForHash().get(MESSAGE_CACHE, cacheId);
        if (null == object) {
            return null;
        }
        return (RedisQueueMessageCache) object;
    }

    @Override
    public void timeoutCallback(RedisQueueMessageCache cache) {
        this.remove(cache.getCacheId());
        redisQueueMessageRetry.addToRetryQueue(cache.getMessage());
    }

    @Override
    public int getDatabase() {
        return 0;
    }
}
