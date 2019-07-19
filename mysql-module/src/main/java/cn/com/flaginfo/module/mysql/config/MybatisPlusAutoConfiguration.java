package cn.com.flaginfo.module.mysql.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus插件配置
 *
 * @author LiuMeng
 * @version 1.0
 * @className MybatisPlusAutoConfiguration
 * @describe TODO
 * @date 2019/7/17 17:21
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusAutoConfiguration {

    /**
     * TODO 分页插件
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(PaginationInterceptor.class)
    public PaginationInterceptor mysqlPaginationInterceptor() {
        return new PaginationInterceptor();
    }


    /**
     * TODO 默认Id生成器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IKeyGenerator.class)
    public IKeyGenerator mysqlKeyGenerator(){
        return new MySqlKeyGenerator();
    }

    /**
     * TODO 默认通用内容填充器
     * @param currentOperationUser
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler mysqlMetaObjectHandler(ICurrentOperationUser currentOperationUser) {
       return new MySqlMetaObjectHandler(currentOperationUser);
    }

    /**
     * TODO 默认操作用户处理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ICurrentOperationUser.class)
    public ICurrentOperationUser mysqlCurrentOperationUser() {
        return new ICurrentOperationUser.DefaultCurrentOperationUser();
    }
}
