package cn.com.flaginfo.redis.config.datasource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: LiuMeng
 * @Describe:
 * @Time: 2018/10/15 16:35
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.first")
@ConditionalOnProperty(name = "spring.data.multi.redis.enabled", havingValue = "true", matchIfMissing = true)
@Order(1)
public class FirstRedisSourceTemplate extends AbstractRedisTemplate {

    @Primary
    @Override
    public @Bean({"firstRedisTemplate", "redisTemplate"})
    RedisTemplate<String, Object> getRedisTemplate() throws Exception {
        return this.instanceRedisTemplate();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws Exception {
        return getRedisTemplate().getConnectionFactory();
    }
}
