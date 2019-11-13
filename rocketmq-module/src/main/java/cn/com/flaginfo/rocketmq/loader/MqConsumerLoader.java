package cn.com.flaginfo.rocketmq.loader;

import cn.com.flaginfo.module.common.diamond.SystemProperty;
import cn.com.flaginfo.rocketmq.annotation.RocketTopic;
import cn.com.flaginfo.rocketmq.domain.ConsumerResultDO;
import cn.com.flaginfo.rocketmq.domain.GroupMapping;
import cn.com.flaginfo.rocketmq.domain.TopicMapping;
import cn.com.flaginfo.rocketmq.exception.MqRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 下午2:38
 */
@Slf4j
@Getter
public class MqConsumerLoader implements BeanPostProcessor {

    private Map<String, GroupMapping> groupMappingMap = new ConcurrentHashMap<>();

    private INameGenerator groupNameGenerator;

    public MqConsumerLoader(INameGenerator groupNameGenerator) {
        this.groupNameGenerator = groupNameGenerator;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        this.loadRocketMqTopic(bean);
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 添加消费监听事件
     *
     * @param bean
     */
    public synchronized void loadRocketMqTopic(Object bean) {
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            final RocketTopic rocketTopic = AnnotationUtils.findAnnotation(method, RocketTopic.class);
            if (rocketTopic == null || (StringUtils.isBlank(rocketTopic.title()) && StringUtils.isBlank(rocketTopic.titleParam()))) {
                continue;
            }
            if (!method.getReturnType().isAssignableFrom(ConsumerResultDO.class)) {
                throw new MqRuntimeException("mq consumer method must return an object of ConsumerResultDO. class: [" + ClassUtils.getUserClass(clazz) + "], method: [" + method.getName() + "]");
            }
            String title=rocketTopic.title();
            String titleParam=rocketTopic.titleParam();
            if(title.equals("") && titleParam.toString().length()>0){
                log.info("rocketTopic is {},the title is {},the titleParam is {}",rocketTopic,rocketTopic.title(),rocketTopic.titleParam());
                // 获取代理处理器
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(rocketTopic);
                // 获取私有 memberValues 属性
                Field f = null;
                try {
                    f = invocationHandler.getClass().getDeclaredField("memberValues");
                    f.setAccessible(true);
                    // 获取实例的属性map
                    Map<String, Object> memberValues = (Map<String, Object>) f.get(invocationHandler);
                    log.info("memberValues----{}",memberValues.toString());
                    // 修改属性值
                    memberValues.put("title", SystemProperty.getString(titleParam));
                    log.info("memberValues----{}",memberValues.toString());

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                log.info("rocketTopic is {}", rocketTopic);
//                log.info("rocketTopic is {},the title is {},the titleParam is {}",rocketTopic,rocketTopic.title(),rocketTopic.titleParam());
            }
            String groupName = rocketTopic.group();
            if (StringUtils.isBlank(groupName)) {
                groupName = this.groupNameGenerator.generateGroupName(clazz);
            }
            GroupMapping groupMapping = groupMappingMap.get(groupName);
            if (null == groupMapping) {
                groupMapping = new GroupMapping(groupName);
                groupMappingMap.put(groupName, groupMapping);
            }
            TopicMapping action = groupMapping.getTopicMapping(rocketTopic.title());
            action.registerTopic(rocketTopic, bean, method);
        }
    }

}
