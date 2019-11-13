package cn.com.flaginfo.rocketmq.notification;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqNotification {

    /**
     * 消息类型
     * @return
     */
    String topic();

    /**
     * 消息key
     * @return
     */
    String keys() default "";

    /**
     * 消息标签
     * @return
     */
    String tags() default "";

    /**
     * 消息体，支持ognl表达式
     * @return
     */
    String[] message() default {};

    /**
     * 消息处理器的SpringBean名称
     * 该bean必须为{@link INotificationMessageHandler}的实现类
     * @return
     */
    String messageHandlerBeanName() default "";

    /**
     * 方法执行前的消息体，支持ognl表达式，优先级高于message
     * @return
     */
    String[] beforeMessage() default {};

    /**
     * 前置消息处理器的SpringBean名称
     * 该bean必须为{@link INotificationMessageHandler}的实现类
     * @return
     */
    String beforeMessageHandlerBeanName() default "";

    /**
     * 方法执行后的消息体，支持ognl表达式，优先级高于message
     * @return
     */
    String[] afterMessage() default {};

    /**
     * 后置消息处理器的SpringBean名称
     * 该bean必须为{@link INotificationMessageHandler}的实现类
     * @return
     */
    String afterMessageHandlerBeanName() default "";

    /**
     * 是否在方法调用之前发送
     */
    boolean isSendBeforeInvoke() default false;

    /**
     * 是否在方法调用之后发送
     */
    boolean isSendAfterInvoke() default true;

    /**
     * 是否从方法的返回值中获取消息体
     */
    boolean isGetAfterInvokeMessageFromReturn() default false;

    /**
     * 消息发送成功之后的回调的bean的名称
     * 该bean必须为{@link IAfterNotificationSendCallback}的子类
     * @return
     */
    String afterMessageSendCallbackBeanName() default "";
}
