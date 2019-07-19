package cn.com.flaginfo.redis.config.properties;

import cn.com.flaginfo.module.common.BaseProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: LiuMeng
 * @Describe:
 * @Time: 2018/10/15 16:34
 */
@Setter
@Getter
public class RedisProperties extends BaseProperties {

    /**
     * redis类型
     */
    private RedisType type = RedisType.Jedis;
    /**
     * 选择库
     */
    private int database;
    /**
     * 服务器地址
     */
    private String host;
    /**
     * 服务器端口
     */
    private int port;
    /**
     * 服务器密码
     */
    private String password;

    public enum RedisType {
        /**
         * jedis
         */
        Jedis,
        /**
         * lettuce
         */
        Lettuce
    }
}
