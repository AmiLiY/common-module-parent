package cn.com.flaginfo.module.common.mvc;

import cn.com.flaginfo.module.common.mvc.wrap.CustomResponseBodyAdapterWrap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public class CustomMvcAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CustomResponseBodyAdapterWrap.class)
    public CustomResponseBodyAdapterWrap customResponseBodyAdapterWrap() {
        return new CustomResponseBodyAdapterWrap();
    }

    @Bean
    public CustomMvcAdapterAdvice customMvcAdapterAdvice(RequestMappingHandlerAdapter adapter, List<ICustomMvcAdapterWrap> customMvcAdapters) {
        return new CustomMvcAdapterAdvice(adapter, customMvcAdapters);
    }

}
