package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.module.common.utils.StringUtils;
import cn.com.flaginfo.redis.RedisUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Slf4j
@Getter
@Setter
@ToString
public class QueueTask implements Runnable {

    private IRedisQueueConsumerFailedMessage redisQueueConsumerFailedMessage;

    private IRedisQueueConsumerCache redisQueueConsumerCache;

    private IRedisQueueMessageRetry redisQueueMessageRetry;

    private transient AtomicInteger invalidCount = new AtomicInteger(0);

    /**
     * 监听的redis库
     */
    private int database;
    /**
     * 监听的topic
     */
    private String topic;
    /**
     * 获取超时timeout
     */
    private Long timeout;
    /**
     * 监听的topic
     */
    private TimeUnit timeUnit;
    /**
     * 消息处理对象
     */
    private Object taskTarget;
    /**
     * 消息处理方法
     */
    private Method taskMethod;
    /**
     * 消息失败时的重试次数
     */
    private int retryTimes;
    /**
     * 消息实体
     */
    private Class<?>[] methodParamTypes;

    private volatile transient boolean isRunning = false;

    public QueueTask(int database, String topic, Object taskTarget, Method taskMethod) {
        this.database = database;
        this.topic = topic;
        this.taskTarget = taskTarget;
        this.taskMethod = taskMethod;
        methodParamTypes = taskMethod.getParameterTypes();
    }

    @Override
    public void run() {
        isRunning = true;
        if (StringUtils.isBlank(this.topic)) {
            log.warn("topic is null cannot start to listener. class:[{}], method:[{}]", this.taskTarget.getClass().getCanonicalName(), taskMethod.getName());
            isRunning = false;
            return;
        }
        if (null == timeout) {
            this.runTask();
        } else {
            this.runTaskWithTimeout();
        }
    }

    private void runTask() {
        while (isRunning) {
            //处理消息
            try {
                Object message = RedisUtils.selectDatabase(this.database).getTemplate().opsForList().rightPop(this.topic);
                this.invokeMethod(message);
            } catch (Exception e) {
                log.error("get mq message error, will try again after 5 seconds.", e);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void runTaskWithTimeout() {
        if (null == timeUnit) {
            throw new IllegalArgumentException("TimeUnit must be set. class:[" + this.taskTarget.getClass().getCanonicalName() + "], method:[" + taskMethod.getName() + "]");
        }
        while (isRunning) {
            //处理消息
            try {
                Object message = RedisUtils.selectDatabase(this.database).getTemplate().opsForList().rightPop(this.topic, timeout, timeUnit);
                this.invokeMethod(message);
            } catch (Exception e) {
                log.error("get mq message error, will try again after 5 seconds.", e);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private void invokeMethod(Object messageObj) {
        if (null == messageObj) {
            log.trace("the message is invalid. topic:{}", topic);
            if (invalidCount.getAndIncrement() > 3) {
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
            return;
        }
        invalidCount.set(0);
        if (!(messageObj instanceof RedisQueueMessage)) {
            if (log.isDebugEnabled()) {
                log.warn("the message is invalid. topic:{}", topic);
            }
            return;
        }
        RedisQueueMessage message = (RedisQueueMessage) messageObj;
        if (log.isDebugEnabled()) {
            log.debug("redis queue receiver message:{}", message);
        }
        if (redisQueueMessageRetry.isFailed(message.getTryTimes(), retryTimes)) {
            log.warn("the message which message id is {} had been retry {} times, this message will be send to failed message queue.", message.getMessageId(), message.getTryTimes());
            redisQueueConsumerFailedMessage.add(message);
            return;
        }
        String cacheId = null;
        try {
            cacheId = this.putToCacheQueue(message);
            taskMethod.invoke(taskTarget, message);
        } catch (Exception e) {
            log.error("invoke message error, will be retry again", e);
            message.setTryTimes(message.getTryTimes() + 1);
            redisQueueMessageRetry.addToRetryQueue(message);
        } finally {
            if (null != cacheId) {
                redisQueueConsumerCache.remove(cacheId);
            }
        }
    }

    public void close() {
        if (isRunning) {
            synchronized (this) {
                if (isRunning) {
                    isRunning = false;
                    log.info("the queue task for topic {} has been close.", topic);
                }
            }
        }
    }

    /**
     * 添加消息到缓存队列
     *
     * @param message
     * @return
     */
    private String putToCacheQueue(RedisQueueMessage message) {
        RedisQueueMessageCache cache = new RedisQueueMessageCache();
        cache.setTimestamp(System.currentTimeMillis());
        cache.setMessage(message);
        cache.setTopic(topic);
        cache.setDatabase(database);
        return redisQueueConsumerCache.add(cache);
    }
}
