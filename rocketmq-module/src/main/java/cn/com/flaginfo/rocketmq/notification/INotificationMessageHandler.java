package cn.com.flaginfo.rocketmq.notification;

/**
 * 通知消息处理器
 * 如果注解配置了该处理，在解析ognl表达式后会调用该方法来处理解析参数
 * 返回的String即为最终的消息发送体
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
public interface INotificationMessageHandler {

    /**
     * 处理消息参数
     * 该方法线程不安全
     * @param args
     * @return
     */
    String handle(Object[] args);

}
