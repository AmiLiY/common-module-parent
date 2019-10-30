package cn.com.flaginfo.rocketmq.domain;

import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.module.common.utils.StringUtils;
import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 下午2:31
 */
@Slf4j
@Getter
@Setter
@ToString
public class TopicMapping implements Serializable {

    private String topic;

    /**
     * topic对应的方法
     */
    private Map<RocketTopic, MqInvokeMethodDefine> topicMethodCache = new HashMap<>();

    public TopicMapping(String topic) {
        this.topic = topic;
    }

    public MqInvokeMethodDefine findDefineMethodByRocketTopic(RocketTopic rocketTopic){
        return topicMethodCache.get(rocketTopic);
    }

    public synchronized void registerTopic(RocketTopic rocketTopic, Object invokeBean, Method invokeMethod) {
        String tags = rocketTopic.tag();
        if (StringUtils.isBlank(tags)) {
            tags = StringPool.ASTERISK;
        }
        if (topicMethodCache.containsKey(rocketTopic)) {
            throw new MqRuntimeException("the tags of push topic cannot register twice. topic: [" + rocketTopic.title() + "], tags: [" + tags + "]");
        }
        log.debug("rocket mq register topic: [{}], tags: [{}]", topic, tags);
        MqInvokeMethodDefine methodDefine = new MqInvokeMethodDefine(invokeBean, invokeMethod, rocketTopic);
        topicMethodCache.put(rocketTopic, methodDefine);
    }
}
