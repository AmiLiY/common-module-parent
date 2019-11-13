package cn.com.flaginfo.rocketmq.annotation;

import cn.com.flaginfo.rocketmq.config.ConsumerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Pull
@Push
public @interface RocketTopic {

    /**
     * 消费模式，当使用RocketMQ时，Compatible不生效，会自动匹配为Push模式
     * @return
     */
    ConsumerType consumerType() default ConsumerType.Push;

    /**
     * 所属组
     *
     * @return
     */
    String group() default "";

    /**
     * 订购的主题
     *
     * @return
     */
    String title() default "";

    /**
     *
     *
     */
    String titleParam() default "";

    /**
     * 订购的tag 可以为空
     *
     * @return
     */
    String tag() default "*";

    /**
     * 消息模式，默认为集群
     *  必须为MessageModel中的枚举项值
     * @return
     */
    String messageModel() default "CLUSTERING";

    /**
     * 定时拉取的线程数，默认1个
     *
     * @return
     */
    @AliasFor(annotation = Pull.class)
    int threadNum() default 1;

    /**
     * 定时拉取，启动后执行等待时间 默认1秒
     *
     * @return
     */
    @AliasFor(annotation = Pull.class)
    int initialDelay() default 60;// 1s

    /**
     * 定时拉取，执行一次后等待时间，默认60s
     *
     * @return
     */
    @AliasFor(annotation = Pull.class)
    int delay() default 60; // 60s

    /**
     * pull模式
     * 定时拉取消息池中的最大数据数量
     *
     * @return
     */
    @AliasFor(annotation = Pull.class)
    int maxNum() default 1000;


    /**
     * push模式使用
     * retry限制, 不配置或-1表示不限制直到过期不消费， 见MessageListenerAdapter
     *
     * @return
     */
    @AliasFor(annotation = Push.class)
    int retryLimit() default -1;
}
