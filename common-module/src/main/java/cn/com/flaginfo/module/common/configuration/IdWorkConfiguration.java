package cn.com.flaginfo.module.common.configuration;

import cn.com.flaginfo.module.common.utils.IdWorker;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiuMeng
 * @version 1.0
 * @className IdWorkConfiguration
 * @describe TODO
 * @date 2019/6/20 11:41
 */
@Configuration
@ConfigurationProperties("spring.id.sequence")
@Data
public class IdWorkConfiguration implements InitializingBean {

    private Long workerId;

    private Long dataCenterId;

    @Override
    public void afterPropertiesSet() throws Exception {
        if( null != this.workerId && null != this.dataCenterId ){
            IdWorker.initSequence(this.workerId, this.dataCenterId);
        }
    }
}
