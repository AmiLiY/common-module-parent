package cn.com.flaginfo.rocketmq.notification;

import cn.com.flaginfo.module.reflect.AnnotationResolver;
import cn.com.flaginfo.module.reflect.PointUtils;
import cn.com.flaginfo.rocketmq.domain.SendResultDO;
import cn.com.flaginfo.rocketmq.factory.producer.RocketMqTemplate;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
@Aspect
@Slf4j
public class NotificationPointProcessor{

    private RocketMqTemplate rocketMqTemplate;

    private ApplicationContext applicationContext;

    private static final INotificationMessageHandler EMPTY_HANDLER = new EmptyNotificationMessageHandler();

    private static final IAfterNotificationSendCallback EMPTY_CALLBACK = new EmptyAfterNotificationSendCallback();

    public NotificationPointProcessor(RocketMqTemplate rocketMqTemplate, ApplicationContext applicationContext) {
        this.rocketMqTemplate = rocketMqTemplate;
        this.applicationContext = applicationContext;
    }

    /**
     * 消息处理器缓存
     */
    private final Map<String, INotificationMessageHandler> handlerCache = new ConcurrentHashMap<>();

    /**
     * 消息处理器缓存
     */
    private final Map<String, IAfterNotificationSendCallback> callbackCache = new ConcurrentHashMap<>();

    @Pointcut("@annotation(cn.com.flaginfo.rocketmq.notification.MqNotification)")
    private void annotationPoint() {

    }

    @Around("annotationPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MqNotification mqNotification = PointUtils.getAnnotation(pjp, MqNotification.class);
        if (null == mqNotification) {
            return pjp.proceed();
        }
        this.sendNotificationBeforeInvoke(pjp, mqNotification);
        Object object = pjp.proceed();
        this.sendNotificationAfterInvoke(pjp, mqNotification, object);
        return object;
    }


    private void sendNotificationBeforeInvoke(ProceedingJoinPoint pjp, MqNotification mqNotification) {
        if (!mqNotification.isSendBeforeInvoke()) {
            return;
        }
        String[] messageParams = mqNotification.beforeMessage().length == 0 ? mqNotification.message() : mqNotification.beforeMessage();
        String messageBody = null;
        if (messageParams.length > 0) {
            String[] paramsName = PointUtils.getPointParameterNames(pjp);
            Object[] args = pjp.getArgs();
            Object[] objects = new Object[messageParams.length];
            for (int i = 0; i < messageParams.length; i++) {
                objects[i] = AnnotationResolver.resolverValue(messageParams[i], paramsName, args);
            }
            if (StringUtils.isNotBlank(mqNotification.beforeMessageHandlerBeanName())) {
                INotificationMessageHandler messageHandler = this.asyncGetNotificationHandler(mqNotification.beforeMessageHandlerBeanName());
                try {
                    messageBody = messageHandler.handle(objects);
                } catch (IllegalClassException e) {
                    log.error("send before message failed, because invoke callback failed.", e);
                    return;
                }
            } else {
                messageBody = JSONObject.toJSONString(Arrays.asList(objects));
            }
        }
        this.sendMessage(true, mqNotification, messageBody);
    }

    private void sendNotificationAfterInvoke(ProceedingJoinPoint pjp, MqNotification mqNotification, Object returnValue) {
        if (!mqNotification.isSendAfterInvoke()) {
            return;
        }
        String[] messageParams = mqNotification.afterMessage().length == 0 ? mqNotification.message() : mqNotification.afterMessage();
        String messageBody = null;
        if (messageParams.length > 0) {
            String[] paramsName = PointUtils.getPointParameterNames(pjp);
            Object[] objects = new Object[messageParams.length];
            if (mqNotification.isGetAfterInvokeMessageFromReturn()) {
                for (int i = 0; i < paramsName.length; i++) {
                    objects[i] = AnnotationResolver.complexResolver(paramsName[i], returnValue);
                }
            } else {
                Object[] args = pjp.getArgs();
                for (int i = 0; i < messageParams.length; i++) {
                    objects[i] = AnnotationResolver.resolverValue(messageParams[i], paramsName, args);
                }
            }
            if (StringUtils.isNotBlank(mqNotification.afterMessageHandlerBeanName())) {
                INotificationMessageHandler messageHandler = this.asyncGetNotificationHandler(mqNotification.afterMessageHandlerBeanName());
                try {
                    messageBody = messageHandler.handle(objects);
                } catch (IllegalClassException e) {
                    log.error("send before message failed, because invoke callback failed.", e);
                    return;
                }
            } else {
                messageBody = JSONObject.toJSONString(Arrays.asList(objects));
            }
        }
        this.sendMessage(false, mqNotification, messageBody);
    }

    private void sendMessage(boolean isBeforeInvoke, MqNotification mqNotification, String messageBody) {
        SendResultDO sendResultDO = rocketMqTemplate.sendMessage(mqNotification.topic(),
                StringUtils.isBlank(mqNotification.tags()) ? null : mqNotification.tags(),
                StringUtils.isBlank(mqNotification.keys()) ? null : mqNotification.keys(),
                messageBody);
        if (StringUtils.isNotBlank(mqNotification.afterMessageHandlerBeanName())) {
            IAfterNotificationSendCallback callback = this.asyncGetAfterNotificationSendCallback(mqNotification.afterMessageHandlerBeanName());
            try {
                callback.callBack(isBeforeInvoke, mqNotification, messageBody, sendResultDO);
            } catch (IllegalClassException e) {
                log.error("invoke callback failed, because {}", e.getMessage());
            }
        }
    }

    private INotificationMessageHandler asyncGetNotificationHandler(String beanName) {
        INotificationMessageHandler notificationMessageHandler = handlerCache.get(beanName);
        if (null == notificationMessageHandler) {
            synchronized (handlerCache) {
                notificationMessageHandler = handlerCache.get(beanName);
                if (null == notificationMessageHandler) {
                    try {
                        notificationMessageHandler = applicationContext.getBean(beanName, INotificationMessageHandler.class);
                    } catch (BeansException e) {
                        log.error("cannot find bean with name {} for class {}", beanName, INotificationMessageHandler.class);
                        notificationMessageHandler = EMPTY_HANDLER;
                    }
                    handlerCache.put(beanName, notificationMessageHandler);
                }
            }
        }
        return notificationMessageHandler;
    }

    private IAfterNotificationSendCallback asyncGetAfterNotificationSendCallback(String beanName) {
        IAfterNotificationSendCallback callback = callbackCache.get(beanName);
        if (null == callback) {
            synchronized (callbackCache) {
                callback = callbackCache.get(beanName);
                if (null == callback) {
                    try {
                        callback = applicationContext.getBean(beanName, IAfterNotificationSendCallback.class);
                    } catch (BeansException e) {
                        log.error("cannot find bean with name {} for class {}", beanName, INotificationMessageHandler.class);
                        callback = EMPTY_CALLBACK;
                    }
                    callbackCache.put(beanName, callback);
                }
            }
        }
        return callback;
    }

    private static class EmptyNotificationMessageHandler implements INotificationMessageHandler {
        @Override
        public String handle(Object[] args) {
            throw new IllegalClassException("[the bean can not be found by the set bean name.] cannot invoke message handler with empty handler.");
        }
    }

    private static class EmptyAfterNotificationSendCallback implements IAfterNotificationSendCallback {
        @Override
        public void callBack(boolean isBeforeInvoke, MqNotification mqNotification, String messageBody, SendResultDO sendResultDO) {
            throw new IllegalClassException("[the bean can not be found by the set bean name.] cannot invoke callback with empty callback.");
        }
    }
}
