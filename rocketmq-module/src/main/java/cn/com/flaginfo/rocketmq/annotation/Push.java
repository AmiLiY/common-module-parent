package cn.com.flaginfo.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 上午10:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Push {

    /**
     * push模式使用
     * retry限制, 不配置或-1表示不限制直到过期不消费， 见MessageListenerAdapter
     *
     * @return
     */
    int retryLimit() default -1;
}
