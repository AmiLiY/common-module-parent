package cn.com.flaginfo.rocketmq.loader;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
public interface INameGenerator {

    /**
     * 生成消费组
     * @param clazz
     * @return
     */
    String generateGroupName(Class<?> clazz);

}
