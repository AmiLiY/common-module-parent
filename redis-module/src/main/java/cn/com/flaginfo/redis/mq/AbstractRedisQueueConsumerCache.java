package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.module.common.SpringApplicationStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
@Slf4j
public abstract class AbstractRedisQueueConsumerCache implements IRedisQueueConsumerCache, ApplicationListener<SpringApplicationStartedEvent> {

    /**
     * 超时时长
     */
    private long timeout = 24 * 60 * 60 * 1000;

    @Override
    public void onApplicationEvent(SpringApplicationStartedEvent event) {
        Thread thread = new Thread(new InvokeTimeOutCheck(this, timeout));
        thread.setDaemon(true);
        thread.setName("RedisQueueConsumerTimeoutCheckThread");
        thread.start();
    }

    @Slf4j
    private static class InvokeTimeOutCheck implements Runnable {

        private IRedisQueueConsumerCache redisQueueConsumerCache;
        private long timeout;

        InvokeTimeOutCheck(IRedisQueueConsumerCache redisQueueConsumerCache, long timeout) {
            this.redisQueueConsumerCache = redisQueueConsumerCache;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Set<String> cacheIdSet = redisQueueConsumerCache.getAllCacheId();
                    if (CollectionUtils.isEmpty(cacheIdSet)) {
                        continue;
                    }
                    long now = System.currentTimeMillis();
                    for (String cacheId : cacheIdSet) {
                        RedisQueueMessageCache redisQueueMessageCache = redisQueueConsumerCache.get(cacheId);
                        if (null == redisQueueMessageCache) {
                            continue;
                        }
                        if (now - redisQueueMessageCache.getTimestamp() > timeout) {
                            log.info("the message has timeout, will try again.");
                            redisQueueConsumerCache.timeoutCallback(redisQueueMessageCache);
                        }
                    }
                } catch (Exception e) {
                    //do nothing
                }
            }
        }
    }
}
