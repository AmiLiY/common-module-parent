package cn.com.flaginfo.redis.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Getter
@Setter
@ToString(callSuper = true)
@ConfigurationProperties(prefix = "spring.redis.module")
public class RedisModuleConfiguration {

    /**
     * 缓存扫描根目录
     */
    private String cacheScanPackage;

    /**
     * 队列扫描目录
     */
    private String queueScanPackage;

    /**
     * 重试检测间隔 默认1秒
     */
    private long retryCheckInterval =  1000;

    /**
     * 重试间隔 默认1分钟
     */
    private long retryInterval =  60 * 1000;
}
