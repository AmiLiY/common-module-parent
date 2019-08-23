package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.module.common.SpringApplicationStartedEvent;
import cn.com.flaginfo.redis.config.RedisModuleConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Set;
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
        Thread thread = new Thread(new AbstractRedisQueueMessageRetry.RetryCheck(this, moduleConfiguration.getRetryCheckInterval()));
        thread.setDaemon(true);
        thread.setName("RedisQueueRetryCheckThread");
        thread.start();
    }

    @Slf4j
    private static class RetryCheck implements Runnable {

        private IRedisQueueMessageRetry redisQueueMessageRetry;
        private long timeout;

        RetryCheck(IRedisQueueMessageRetry redisQueueMessageRetry, long timeout) {
            this.redisQueueMessageRetry = redisQueueMessageRetry;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Set<RedisQueueMessage> retryMessageSet = redisQueueMessageRetry.getNeedRetryMessages();
                    if (CollectionUtils.isEmpty(retryMessageSet)) {
                        continue;
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
                } finally {
                    try {
                        TimeUnit.MILLISECONDS.sleep(timeout);
                    } catch (InterruptedException e) {
                        //do nothing
                    }
                }
            }
        }
    }
}
