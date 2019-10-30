package cn.com.flaginfo.module.common.mvc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * 适配器构造器
 *
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public class CustomMvcAdapterAdvice implements InitializingBean {

    private RequestMappingHandlerAdapter adapter;

    private List<ICustomMvcAdapterWrap> customMvcAdapters;

    public CustomMvcAdapterAdvice(RequestMappingHandlerAdapter adapter,
                                  List<ICustomMvcAdapterWrap> customMvcAdapters) {
        this.adapter = adapter;
        this.customMvcAdapters = customMvcAdapters;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerAdapterAdvice();
    }

    private void registerAdapterAdvice() {
        if (CollectionUtils.isEmpty(customMvcAdapters)) {
            return;
        }
        for (ICustomMvcAdapterWrap customMvcAdapter : customMvcAdapters) {
            customMvcAdapter.register(adapter);
        }
    }
}
