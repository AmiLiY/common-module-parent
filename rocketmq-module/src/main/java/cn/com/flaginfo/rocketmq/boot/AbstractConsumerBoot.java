package cn.com.flaginfo.rocketmq.boot;

import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.domain.GroupMapping;
import cn.com.flaginfo.rocketmq.domain.MqInvokeMethodDefine;
import cn.com.flaginfo.rocketmq.domain.TopicMapping;
import cn.com.flaginfo.rocketmq.factory.IMqFactory;
import cn.com.flaginfo.rocketmq.loader.INameGenerator;
import cn.com.flaginfo.rocketmq.loader.MqConsumerLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 消费者启动器
 *
 * @author: Meng.Liu
 * @date: 2018/11/22 上午10:15
 */
@Slf4j
public abstract class AbstractConsumerBoot<PS, PL> {

    private transient volatile boolean isStarted = false;

    /**
     * push 消费组缓存，
     * value consumer
     */
    private final Set<PS> pushConsumerCache = new HashSet<>();

    /**
     * pull 消费组缓存，
     * value consumer
     */
    private final Set<PL> pullConsumerCache = new HashSet<>();

    private IMqFactory<PS, PL> mqFactory;

    protected MqConsumerLoader consumerLoader;

    protected INameGenerator nameGenerator;

    public AbstractConsumerBoot(IMqFactory<PS, PL> mqFactory, MqConsumerLoader consumerLoader, INameGenerator nameGenerator) {
        this.mqFactory = mqFactory;
        this.consumerLoader = consumerLoader;
        this.nameGenerator = nameGenerator;
        this.isStarted = false;
    }

    /**
     * 获取Push消费者
     *
     * @param groupName
     * @param model
     * @return
     */
    protected synchronized PS getPushConsumer(String groupName, String model) {
        PS ps = mqFactory.pushConsumer(groupName, model);
        pushConsumerCache.add(ps);
        return ps;
    }

    /**
     * 获取pull消费则
     *
     * @param groupName
     * @param model
     * @return
     */
    protected synchronized PL getPullConsumer(String groupName, String model) {
        PL pl = mqFactory.pullConsumer(groupName, model);
        pullConsumerCache.add(pl);
        return pl;
    }

    private void bindAction() {
        log.info("MQ consumer boot bing action start...");
        Map<String, GroupMapping> groupMappingMap = consumerLoader.getGroupMappingMap();
        if (CollectionUtils.isEmpty(groupMappingMap)) {
            return;
        }

        for (Map.Entry<String, GroupMapping> groupMappingEntry : groupMappingMap.entrySet()) {
            String groupName = groupMappingEntry.getKey();
            GroupMapping groupMapping = groupMappingEntry.getValue();
            if (null == groupMapping || CollectionUtils.isEmpty(groupMapping.getTopicMappingCache())) {
                continue;
            }
            for (Map.Entry<String, TopicMapping> topicMappingEntry : groupMapping.getTopicMappingCache().entrySet()) {
                TopicMapping topicMapping = topicMappingEntry.getValue();
                if (null == topicMapping || CollectionUtils.isEmpty(topicMapping.getTopicMethodCache())) {
                    continue;
                }
                for (Map.Entry<RocketTopic, MqInvokeMethodDefine> rocketTopicEntry : topicMapping.getTopicMethodCache().entrySet()) {
                    RocketTopic topic = rocketTopicEntry.getKey();
                    this.initConsumerWithRocketTopic(groupName, topic, rocketTopicEntry.getValue());
                    log.info("MQ consumer : {} subscribe : {}, {}", groupName, topic.title(), topic.tag());
                }
            }
        }
    }

    /**
     * 绑定事件
     */
    protected abstract void initConsumerWithRocketTopic(String groupName, RocketTopic topic, MqInvokeMethodDefine invokeMethodDefine);

    /**
     * 启动
     */
    public synchronized void start() {
        if (isStarted) {
            log.warn("the consumer boot had been started, cannot be start again.");
            return;
        }
        this.bindAction();
        isStarted = true;
    }

    /**
     * 停止
     */
    public synchronized void stop() {
        if (!isStarted) {
            log.warn("the consumer boot is not start yet, cannot stop.");
            return;
        }
        if (!CollectionUtils.isEmpty(pullConsumerCache)) {
            for (PL pl : pullConsumerCache) {
                this.stopPullConsumer(pl);
            }
        }
        if (!CollectionUtils.isEmpty(pushConsumerCache)) {
            for (PS ps : pushConsumerCache) {
                this.stopPushConsumer(ps);
            }
        }
        pushConsumerCache.clear();
        pullConsumerCache.clear();
        isStarted = false;
        log.warn("All consumer shutdown success.");
    }

    /**
     * 停止消费者
     */
    protected abstract void stopPushConsumer(PS ps);

    /**
     * 停止消费者
     */
    protected abstract void stopPullConsumer(PL pl);
}
