package cn.com.flaginfo.module.common.mvc;

import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public interface ICustomMvcAdapterWrap extends HandlerMethodReturnValueHandler {
    /**
     * 注册
     * @param adapter
     */
    void register(RequestMappingHandlerAdapter adapter);
}
