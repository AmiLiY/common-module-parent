package cn.com.flaginfo.redis.mq;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisQueueEnabledRegister.class)
public @interface EnableRedisMQ {
}
