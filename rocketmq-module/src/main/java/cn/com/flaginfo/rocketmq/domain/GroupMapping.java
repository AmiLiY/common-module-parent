package cn.com.flaginfo.rocketmq.domain;

import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.module.common.utils.StringUtils;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
@Getter
@Setter
@ToString(callSuper = true)
public class GroupMapping {

    private String groupName;

    private Map<String, TopicMapping> topicMappingCache = new HashMap<>();

    public GroupMapping(String groupName){
        this.groupName = groupName;
    }

    /**
     * 获取Topic的配置
     * @param topicName
     * @return
     */
    public synchronized TopicMapping getTopicMapping(String topicName){
        TopicMapping topicMapping = topicMappingCache.get(topicName);
        if( null == topicMapping ){
            topicMapping = new TopicMapping(topicName);
            topicMappingCache.put(topicName, topicMapping);
        }
        return topicMapping;
    }
}
