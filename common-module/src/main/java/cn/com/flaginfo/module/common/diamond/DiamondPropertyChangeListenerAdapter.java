package cn.com.flaginfo.module.common.diamond;

import cn.com.flaginfo.module.common.utils.ObjectConvertUtils;
import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.module.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Slf4j
public class DiamondPropertyChangeListenerAdapter extends PropertyChangeListener implements InitializingBean {

    private static final Pattern EXPRESS_PATTERN = Pattern.compile("\\$\\{([^:]*):?.*\\}");

    /**
     * 是否正在监听
     */
    private volatile transient boolean listening;

    private transient Map<String, Field> fieldMap = new HashMap<>(32);

    private Class targetClass;

    @Override
    public Set<String> register() {
        if (CollectionUtils.isEmpty(fieldMap) || !this.listening) {
            return Collections.emptySet();
        } else {
            return fieldMap.keySet();
        }
    }

    /**
     * 加载所有配置属性字段
     */
    private void loadPropertyField() {
        if( null == targetClass ){
            targetClass = ClassUtils.getUserClass(this.getClass());
        }
        Field[] fields = targetClass.getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            Value value = field.getAnnotation(Value.class);
            if (null == value) {
                continue;
            }
            String valueExpress = value.value();
            if (StringUtils.isBlank(valueExpress)) {
                continue;
            }
            Matcher matcher = EXPRESS_PATTERN.matcher(valueExpress);
            if (!matcher.find()) {
                continue;
            }
            valueExpress = matcher.group(2);
            if (StringUtils.isBlank(valueExpress)) {
                continue;
            }
            this.putMap(valueExpress, field);
        }
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(this.getClass(), ConfigurationProperties.class);
        if (null == configurationProperties) {
            return;
        }
        String prefix = configurationProperties.prefix();
        if (StringUtils.isNoneBlank(prefix)) {
            prefix = prefix + StringPool.DOT;
        }
        for (Field field : fields) {
            String filedExpress = prefix + StringUtils.camels2Strikethrough(field.getName());
            this.putMap(filedExpress, field);
        }
    }

    private void putMap(String valueExpress, Field field) {
        valueExpress = valueExpress.trim();
        ReflectionUtils.makeAccessible(field);
        fieldMap.put(valueExpress, field);
    }

    @Override
    public void change(String key, Object oldValue, String newValue, Map<String, Object> allConfig) {
        if (!this.listening) {
            return;
        }
        try {
            newValue = this.beforeChangePropertySet(key, oldValue, newValue, allConfig);
        } catch (InterruptedException e) {
            if (log.isDebugEnabled()) {
                log.debug("set value of [{}] has been interrupted.", key);
            }
            return;
        }
        Field field = fieldMap.get(key);
        boolean ifSetSuccess;
        try {
            field.set(this, ObjectConvertUtils.toType(field.getType(), newValue));
            ifSetSuccess = true;
        } catch (Exception e) {
            log.error("set field [{}] value failed.", field.getName(), e);
            ifSetSuccess = false;
        }
        this.afterChangePropertySet(ifSetSuccess, key, oldValue, newValue, allConfig);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.loadPropertyField();
        DiamondProperties.addChangeListener(this);
        this.listening = true;
    }

    /**
     * 关闭监听
     */
    public synchronized void close() {
        listening = false;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    /**
     * 改变属性注入之前的回调
     *
     * @param key
     * @param oldValue
     * @param newValue
     * @param allConfig
     * @return 新的属性值
     */
    public String beforeChangePropertySet(String key, Object oldValue, String newValue, Map<String, Object> allConfig) throws InterruptedException {
        //do nothing
        return newValue;
    }

    /**
     * 改变属性注入之后的回调
     *
     * @param isSuccess 是否成功
     * @param key
     * @param oldValue
     * @param newValue
     * @param allConfig
     */
    public void afterChangePropertySet(boolean isSuccess, String key, Object oldValue, String newValue, Map<String, Object> allConfig) {
        //do nothing
    }
}
