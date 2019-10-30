package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.module.common.SpringApplicationStartedEvent;
import cn.com.flaginfo.redis.config.RedisModuleConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
@Slf4j
public abstract class AbstractRedisQueueConsumerCache implements IRedisQueueConsumerCache, ApplicationListener<SpringApplicationStartedEvent> {

    @Autowired
    private RedisModuleConfiguration moduleConfiguration;

    @Override
    public void onApplicationEvent(SpringApplicationStartedEvent event) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "RedisQueueConsumerTimeoutCheckThread");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(new InvokeTimeOutCheck(this, moduleConfiguration.getConsumerTimeout()),
                0L,
                moduleConfiguration.getConsumerTimeoutCheckInterval(),
                TimeUnit.MILLISECONDS);
    }

    @Slf4j
    private static class InvokeTimeOutCheck implements Runnable {

        private IRedisQueueConsumerCache redisQueueConsumerCache;
        private long consumerTimeout;

        InvokeTimeOutCheck(IRedisQueueConsumerCache redisQueueConsumerCache, long consumerTimeout) {
            this.redisQueueConsumerCache = redisQueueConsumerCache;
            this.consumerTimeout = consumerTimeout;
        }

        @Override
        public void run() {
            try {
                Set<String> cacheIdSet = redisQueueConsumerCache.getAllCacheId();
                if (CollectionUtils.isEmpty(cacheIdSet)) {
                    return;
                }
                long now = System.currentTimeMillis();
                for (String cacheId : cacheIdSet) {
                    RedisQueueMessageCache redisQueueMessageCache = redisQueueConsumerCache.get(cacheId);
                    if (null == redisQueueMessageCache) {
                        continue;
                    }
                    if (now - redisQueueMessageCache.getTimestamp() > consumerTimeout) {
                        log.info("the message has consumerTimeout, will try again.");
                        redisQueueConsumerCache.timeoutCallback(redisQueueMessageCache);
                    }
                }
            } catch (Exception e) {
                //do nothing
            }
        }
    }
}
