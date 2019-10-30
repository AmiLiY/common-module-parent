package cn.com.flaginfo.redis.mq;

import javax.validation.constraints.Null;
import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisQueueListener {
    /**
     * 监听的top
     *
     * @return
     */
    String topic();

    /**
     * 监听的redis库
     *
     * @return
     */
    int database() default 0;

    /**
     * 超时时长
     *
     * @return
     */
    long timeout() default 10;

    /**
     * 超时时长单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 重试次数，超过重试次数的消息会被放入失败队列
     * -1表示永久重试
     * @return
     */
    int retryTimes() default -1;
}
