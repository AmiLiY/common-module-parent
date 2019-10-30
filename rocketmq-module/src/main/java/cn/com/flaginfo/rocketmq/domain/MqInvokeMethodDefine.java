package cn.com.flaginfo.rocketmq.domain;

import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MqInvokeMethodDefine {

    private Class<?> invokeClazz;

    private Method invokeMethod;

    private Object invokeBean;

    private RocketTopic rocketTopic;

    public MqInvokeMethodDefine(Object invokeBean, Method invokeMethod, RocketTopic rocketTopic) {
        this.invokeBean = invokeBean;
        this.invokeMethod = invokeMethod;
        this.invokeClazz = invokeBean.getClass();
        this.rocketTopic = rocketTopic;
    }

}
