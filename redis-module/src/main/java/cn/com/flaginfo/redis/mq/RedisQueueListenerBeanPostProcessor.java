package cn.com.flaginfo.redis.mq;

import cn.com.flaginfo.module.common.SpringApplicationStartedEvent;
import cn.com.flaginfo.module.common.utils.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Slf4j
public class RedisQueueListenerBeanPostProcessor implements BeanPostProcessor, ApplicationListener<SpringApplicationStartedEvent> {

    @Setter
    private ThreadPoolExecutor taskExecutor;

    private Map<RedisQueueListener, QueueTask> queueTaskMap = new ConcurrentHashMap<>();

    private IRedisQueueConsumerFailedMessage redisQueueConsumerFailedMessage;

    private IRedisQueueConsumerCache redisQueueConsumerCache;

    private IRedisQueueMessageRetry redisQueueMessageRetry;

    public RedisQueueListenerBeanPostProcessor(IRedisQueueConsumerCache redisQueueConsumerCache,
                                               IRedisQueueConsumerFailedMessage redisQueueConsumerFailedMessage,
                                               IRedisQueueMessageRetry redisQueueMessageRetry) {
        this.redisQueueConsumerFailedMessage = redisQueueConsumerFailedMessage;
        this.redisQueueConsumerCache = redisQueueConsumerCache;
        this.redisQueueMessageRetry = redisQueueMessageRetry;
    }

    /**
     * 初始化线程池
     */
    private void initTaskExecutor() {
        if (null != taskExecutor) {
            return;
        }
        log.info("init redis queue listener pool by default.");
        taskExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadFactory() {
            AtomicInteger number = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("RedisListenerThread-" + number.incrementAndGet());
                return thread;
            }
        });
    }

    private void startQueueListener() {
        if (CollectionUtils.isEmpty(queueTaskMap)) {
            log.info("redis queue listener task map is empty.");
            return;
        }
        this.initTaskExecutor();
        for (Map.Entry<RedisQueueListener, QueueTask> entry : queueTaskMap.entrySet()) {
            QueueTask queueTask = entry.getValue();
            this.taskExecutor.execute(queueTask);
        }
    }

    public void shutdown() {
        log.info("shut down redis queue listener...");
        if (!CollectionUtils.isEmpty(queueTaskMap)) {
            queueTaskMap.values().forEach(QueueTask::close);
        }
        if (null != taskExecutor) {
            taskExecutor.shutdownNow();
        }
    }

    @PreDestroy
    public void destroy(){
        this.shutdown();
    }

    @Override
    public void onApplicationEvent(SpringApplicationStartedEvent event) {
        this.startQueueListener();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        this.findListenerMethod(bean);
        return bean;
    }

    private void findListenerMethod(Object bean) {
        Class<?> beanClass = bean.getClass();
        RedisQueueListener classAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), RedisQueueListener.class);
        Method[] methods = beanClass.getDeclaredMethods();
        if (methods.length == 0) {
            return;
        }
        for (Method method : methods) {
            RedisQueueListener methodAnnotation = AnnotationUtils.findAnnotation(method, RedisQueueListener.class);
            if (null == methodAnnotation) {
                if (null != classAnnotation) {
                    RedisQueueListenerHandler redisQueueListenerHandler = AnnotationUtils.findAnnotation(method, RedisQueueListenerHandler.class);
                    if (null != redisQueueListenerHandler && redisQueueListenerHandler.isDefault()) {
                        this.addQueueTask(classAnnotation, bean, method);
                    }
                }
            } else {
                this.addQueueTask(methodAnnotation, bean, method);
            }
        }
    }

    private void addQueueTask(RedisQueueListener annotation, Object target, Method method) {
        if (StringUtils.isBlank(annotation.topic())) {
            log.warn("register redis listener failed, topic is empty. class [{}], method [{}]", target.getClass().getCanonicalName(), method.getName());
            return;
        }
        if( method.getParameterTypes().length == 0 || method.getParameterTypes()[0] != RedisQueueMessage.class ){
            log.warn("register redis listener failed, invoke method param type error, the param type must be RedisQueueMessage.class. class [{}], method [{}]", target.getClass().getCanonicalName(), method.getName());
            return;
        }
        QueueTask queueTask = new QueueTask(annotation.database(), annotation.topic(), target, method);
        queueTask.setTimeout(annotation.timeout());
        queueTask.setTimeUnit(annotation.timeUnit());
        queueTask.setRetryTimes(annotation.retryTimes());
        queueTask.setRedisQueueConsumerCache(redisQueueConsumerCache);
        queueTask.setRedisQueueConsumerFailedMessage(redisQueueConsumerFailedMessage);
        queueTask.setRedisQueueMessageRetry(redisQueueMessageRetry);
        queueTaskMap.put(annotation, queueTask);
        log.info("register redis listener success: topic [{}], database : [{}], class [{}], method : [{}]", annotation.topic(), annotation.database(), target.getClass().getCanonicalName(), method.getName());
    }
}
