package cn.com.flaginfo.module.common.mvc.wrap;

import cn.com.flaginfo.module.common.mvc.ICustomMvcAdapterWrap;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
public class CustomResponseBodyAdapterWrap implements ICustomMvcAdapterWrap {

    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    private static final Map<ResponseBodyFilter, CustomerJsonSerializerFilter> consumerJsonFilter = new ConcurrentHashMap<>();

    @Override
    public void register(RequestMappingHandlerAdapter adapter) {
        List<HandlerMethodReturnValueHandler> unModifyHandlers = adapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers;
        if (CollectionUtils.isEmpty(unModifyHandlers)) {
            handlers = new ArrayList<>();
        } else {
            handlers = new ArrayList<>(unModifyHandlers);
        }
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                this.requestResponseBodyMethodProcessor = (RequestResponseBodyMethodProcessor) handler;
                handlers.set(handlers.indexOf(handler), this);
            }
        }
        adapter.setReturnValueHandlers(handlers);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.requestResponseBodyMethodProcessor.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        ResponseBodyFilter responseBodyFilter = returnType.getMethodAnnotation(ResponseBodyFilter.class);
        if (null != returnValue && null != responseBodyFilter) {
            this.doCustomHandlerReturnValue(responseBodyFilter, returnValue, returnType, mavContainer, webRequest);
        } else {
            this.requestResponseBodyMethodProcessor.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }
    }

    private void doCustomHandlerReturnValue(ResponseBodyFilter responseBodyFilter, Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        CustomerJsonSerializerFilter jsonSerializer = syncGetConsumerJsonSerializerFilter(responseBodyFilter);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String json = jsonSerializer.toJson(returnValue);
        response.getWriter().write(json);
    }

    private CustomerJsonSerializerFilter syncGetConsumerJsonSerializerFilter(ResponseBodyFilter responseBodyFilter) {
        CustomerJsonSerializerFilter customerJsonSerializerFilter = consumerJsonFilter.get(responseBodyFilter);
        if (null == customerJsonSerializerFilter) {
            synchronized (consumerJsonFilter) {
                customerJsonSerializerFilter = consumerJsonFilter.get(responseBodyFilter);
                if (null == customerJsonSerializerFilter) {
                    customerJsonSerializerFilter = new CustomerJsonSerializerFilter(responseBodyFilter);
                    consumerJsonFilter.put(responseBodyFilter, customerJsonSerializerFilter);
                }
            }
        }
        return customerJsonSerializerFilter;
    }

    private static class CustomerJsonSerializerFilter {

        private static final String REWRITE_INCLUDE = "_@$include$@_";

        private static final String REWRITE_FILTER = "_@$prepareMapper$@_";

        private final ObjectMapper mapper = new ObjectMapper();

        @JsonFilter(REWRITE_INCLUDE)
        interface RewriteInclude {
        }

        @JsonFilter(REWRITE_FILTER)
        interface RewriteFilter {
        }

        private ResponseBodyFilter responseBodyFilter;

        CustomerJsonSerializerFilter(ResponseBodyFilter responseBodyFilter) {
            this.responseBodyFilter = responseBodyFilter;
            this.prepareMapper();
        }

        void prepareMapper() {
            if (null == responseBodyFilter) {
                return;
            }
            if (responseBodyFilter.include().length > 0) {
                mapper.setFilterProvider(new SimpleFilterProvider().addFilter(REWRITE_INCLUDE,
                        SimpleBeanPropertyFilter.filterOutAllExcept(responseBodyFilter.include())));
                mapper.addMixIn(responseBodyFilter.type(), RewriteInclude.class);
            }
            if (responseBodyFilter.exclude().length > 0) {
                mapper.setFilterProvider(new SimpleFilterProvider().addFilter(REWRITE_FILTER,
                        SimpleBeanPropertyFilter.serializeAllExcept(responseBodyFilter.exclude())));
                mapper.addMixIn(responseBodyFilter.type(), RewriteFilter.class);
            }
            mapper.setSerializationInclusion(responseBodyFilter.jsonInclude());
        }

        /**
         * 序列化成Json
         *
         * @param object
         * @return
         * @throws JsonProcessingException
         */
        String toJson(Object object) throws JsonProcessingException {
            return mapper.writeValueAsString(object);
        }
    }
}
