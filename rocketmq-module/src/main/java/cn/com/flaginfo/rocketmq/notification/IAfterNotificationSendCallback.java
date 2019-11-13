package cn.com.flaginfo.rocketmq.notification;

import cn.com.flaginfo.rocketmq.domain.SendResultDO;

/**
 * 通知消息发送之后的回调
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
public interface IAfterNotificationSendCallback {

    /**
     * 消息发送回调方法
     * @param isBeforeInvoke 是否为执行前发送的通知
     * @param mqNotification 通知注解
     * @param messageBody 消息体
     * @param sendResultDO 消息结果
     */
    void callBack(boolean isBeforeInvoke, MqNotification mqNotification, String messageBody, SendResultDO sendResultDO);

}
