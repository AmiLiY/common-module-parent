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
public abstract class AbstractRedisQueueMessageRetry implements IRedisQueueMessageRetry, ApplicationListener<SpringApplicationStartedEvent> {

    @Autowired
    private RedisModuleConfiguration moduleConfiguration;

    @Override
    public void onApplicationEvent(SpringApplicationStartedEvent event) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "RedisQueueRetryCheckThread");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(new AbstractRedisQueueMessageRetry.RetryCheck(this),
                0L,
                moduleConfiguration.getRetryCheckInterval(),
                TimeUnit.MILLISECONDS);
    }

    @Slf4j
    private static class RetryCheck implements Runnable {

        private IRedisQueueMessageRetry redisQueueMessageRetry;

        RetryCheck(IRedisQueueMessageRetry redisQueueMessageRetry) {
            this.redisQueueMessageRetry = redisQueueMessageRetry;
        }

        @Override
        public void run() {
            try {
                Set<RedisQueueMessage> retryMessageSet = redisQueueMessageRetry.getNeedRetryMessages();
                if (CollectionUtils.isEmpty(retryMessageSet)) {
                    return;
                }
                for (RedisQueueMessage message : retryMessageSet) {
                    if (null != message) {
                        boolean isSuccess = redisQueueMessageRetry.retryAgain(message);
                        if (isSuccess) {
                            redisQueueMessageRetry.removeFromRetryQueue(message);
                        }
                    }
                }
            } catch (Exception e) {
                //do nothing
            }
        }
    }
}
