package cn.com.flaginfo.redis.mq;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/8/22
 * TODO:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisQueueListenerHandler {

    /**
     * 是否默认
     * @return
     */
    boolean isDefault() default false;

}
