package cn.com.flaginfo.rocketmq.notification;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqNotificationImportRegister.class)
public @interface EnableMqNotification {
}
