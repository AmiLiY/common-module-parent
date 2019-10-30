package cn.com.flaginfo.rocketmq.message;

import cn.com.flaginfo.module.common.utils.TimeUtils;
import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.config.ConsumerType;
import cn.com.flaginfo.rocketmq.config.MqType;
import cn.com.flaginfo.rocketmq.constants.Constants;
import cn.com.flaginfo.rocketmq.domain.ConsumerResultDO;
import cn.com.flaginfo.rocketmq.domain.MqInvokeMethodDefine;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author: Meng.Liu
 * @date: 2018/11/26 上午11:44
 */
@Slf4j
public abstract class AbstractMqMessageAdapter<T> {

    private MqInvokeMethodDefine invokeMethodDefine;
    private RocketTopic rocketTopic;

    public AbstractMqMessageAdapter(RocketTopic rocketTopic, MqInvokeMethodDefine invokeMethodDefine) {
       this.invokeMethodDefine = invokeMethodDefine;
       this.rocketTopic = rocketTopic;
    }

    protected T invokeMessage(ConsumerType consumerType, MqMessage message) {
        if (null == message) {
            throw new MqRuntimeException("message is null.");
        }
        Method invokeMethod = invokeMethodDefine.getInvokeMethod();
        switch (consumerType) {
            case Compatible:
                if (this.getType() != MqType.ons) {
                    log.error("rocket pull message cannot do this invoke, topic:{}, tags:{}, msgId:{}, message:{}", message.getTopic(), message.getTag(), message.getMsgId(), message);
                    throw new MqRuntimeException("consumer type is not allow.");
                }
            case Push:
                RocketTopic pt = invokeMethodDefine.getRocketTopic();
                if (null != pt && pt.retryLimit() > 0 && message.getReconsumeTimes() > pt.retryLimit()) {
                    log.info("message over retryLimit, please check the msg:{}", message.getMessage());
                    this.recordFailMessage(message);
                    return this.successType();
                }
                break;
            default:
                log.error("unknown consumer type, topic:{}, tags:{}, msgId:{}, message:{}", message.getTopic(), message.getTag(), message.getMsgId(), message);
                throw new MqRuntimeException("unknown consumer type.");

        }
        try {
            ConsumerResultDO result = (ConsumerResultDO) invokeMethod.invoke(invokeMethodDefine.getInvokeBean(), this.getMethodArgs(invokeMethod, message));
            if (result != null && result.getRetry()) {
                log.info("msgId:{} will retry later, cause:{}", message.getMsgId(), result.getMessage());
                return this.retryType();
            }
        } catch (Exception e) {
            log.error("message invoke error, msgId:{} will retry later", message.getMsgId(), e);
            return this.retryType();
        }
        if (log.isDebugEnabled()) {
            log.debug("mq consume message end.");
        }
        return this.successType();
    }

    /**
     * 组装方法的参数 可初始更多的参数，待完善
     *
     * @return
     */
    private Object[] getMethodArgs(Method m, MqMessage message) {
        Class<?>[] cs = m.getParameterTypes();
        Object[] args;
        if (cs.length == 0) {
            return null;
        }
        args = new Object[cs.length];
        int i = 0;
        for (Class<?> c : cs) {
            if (c.isAssignableFrom(MqMessage.class)) {
                args[i] = message;
            } else if (c.getName().equals(ConsumerResultDO.class.getName())) {
                args[i] = new ConsumerResultDO();
            }
            i++;
        }
        return args;
    }

    /**
     * 记录失败日志
     *
     * @param mqMessage
     */
    private void recordFailMessage(MqMessage mqMessage) {
        String root = this.getClass().getResource("/").getPath()
                + File.pathSeparator + "spring-mq-consumer-failed-message-"
                + TimeUtils.time2Str(LocalDateTime.now(), "yy-MM-dd") + ".log";
        try {
            FileUtils.writeLines(new File(root), Collections.singleton(mqMessage.getTopic() + "," + mqMessage.getMessage()), true);
        } catch (IOException e) {
            log.error("", e);
        }

    }

    protected void setThreadTrace(String topic, String key) {
        MDC.put(Constants.LOG_TRACE_ID, "[" + topic
                + (key == null ? "]" : ("]-[" + key + "]")));
    }

    protected void removeThreadTrace() {
        MDC.remove(Constants.LOG_TRACE_ID);
    }


    /**
     * 消费成功的状态
     *
     * @return
     */
    public abstract T successType();

    /**
     * 重试状态
     *
     * @return
     */
    public abstract T retryType();

    /**
     * 返回客户端类型
     *
     * @return
     */
    public abstract MqType getType();

}
