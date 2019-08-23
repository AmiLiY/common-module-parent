package cn.com.flaginfo.redis.mq;

/**
 * @author: LiuMeng
 * @date: 2019/8/23
 * TODO:
 */
public interface IRedisDatabase {

    /**
     * 获取默认的数据库编号
     * @return
     */
    int getDatabase();

}
