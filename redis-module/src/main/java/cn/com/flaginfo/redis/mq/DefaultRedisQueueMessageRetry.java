package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.redis.RedisUtils;
import cn.com.flaginfo.redis.config.RedisModuleConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
@Slf4j
public class DefaultRedisQueueMessageRetry extends AbstractRedisQueueMessageRetry {

    @Autowired
    private RedisModuleConfiguration moduleConfiguration;

    @Override
    public void addToRetryQueue(RedisQueueMessage message) {
        if (null == message) {
            return;
        }
        long nextRetryTime = System.currentTimeMillis() + moduleConfiguration.getRetryInterval();
        RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForZSet().add(RETRY_MESSAGE_QUEUE, message, nextRetryTime);
    }

    @Override
    public boolean retryAgain(RedisQueueMessage message) {
        log.warn("the message which message id is {} will be retry again, retry times is {} ", message.getMessageId(), message.getTryTimes());
        RedisUtils.selectDatabase(message.getDatabase()).getTemplate().opsForList().leftPush(message.getTopic(), message);
        return true;
    }

    @Override
    public void removeFromRetryQueue(RedisQueueMessage message) {
        RedisUtils.selectDatabase(message.getDatabase()).getTemplate().opsForZSet().remove(RETRY_MESSAGE_QUEUE, message);
    }

    @Override
    public Set<RedisQueueMessage> getNeedRetryMessages() {
        long checkTime = System.currentTimeMillis();
        Set objectSet = RedisUtils.selectDatabase(this.getDatabase()).getTemplate().opsForZSet().rangeByScore(RETRY_MESSAGE_QUEUE, 0, checkTime);
        if (CollectionUtils.isEmpty(objectSet)) {
            return Collections.emptySet();
        } else {
            return (Set<RedisQueueMessage>) objectSet;
        }
    }

    @Override
    public boolean isFailed(int current, int threshold) {
        if (-1 == threshold) {
            return false;
        }
        return current > threshold;
    }

    @Override
    public int getDatabase() {
        return 0;
    }
}
