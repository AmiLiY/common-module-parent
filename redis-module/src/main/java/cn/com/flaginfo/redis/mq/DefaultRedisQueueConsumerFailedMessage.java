package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.redis.RedisUtils;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
public class DefaultRedisQueueConsumerFailedMessage implements IRedisQueueConsumerFailedMessage {

    @Override
    public RedisQueueMessage next() {
        Object messageObj = RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForSet().randomMember(FAILED_MESSAGE_CACHE);
        if (null == messageObj) {
            return null;
        }
        return (RedisQueueMessage) messageObj;
    }

    @Override
    public RedisQueueMessage remove() {
        Object messageObj = RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForSet().pop(FAILED_MESSAGE_CACHE);
        if (null == messageObj) {
            return null;
        }
        return (RedisQueueMessage) messageObj;
    }

    @Override
    public void add(RedisQueueMessage message) {
        RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForSet().add(FAILED_MESSAGE_CACHE, message);
    }

    @Override
    public Long count() {
        return RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForSet().size(FAILED_MESSAGE_CACHE);
    }

    @Override
    public int getDatabase() {
        return 0;
    }
}
