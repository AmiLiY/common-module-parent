package cn.com.flaginfo.module.mysql.config;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * 利用雪花算法生成分布式唯一Id
 * @author LiuMeng
 * @version 1.0
 * @className MySqlKeyGenerator
 * @describe TODO
 * @date 2019/7/19 13:50
 */
public class MySqlKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "select " + IdWorker.getId() + " from dual";
    }
}
