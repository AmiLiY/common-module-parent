package cn.com.flaginfo.rocketmq.loader;

import cn.com.flaginfo.rocketmq.constants.Constants;

/**
 * @author: LiuMeng
 * @date: 2019/10/18
 * TODO:
 */
public class DefaultNameGenerator implements INameGenerator {

    @Override
    public String generateGroupName(Class<?> clazz) {
        return clazz.getPackage().getName().replaceAll("\\.", "-");
    }

}
