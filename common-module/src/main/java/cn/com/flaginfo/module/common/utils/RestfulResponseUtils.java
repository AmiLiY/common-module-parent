package cn.com.flaginfo.module.common.utils;

import cn.com.flaginfo.exception.ErrorCode;
import cn.com.flaginfo.module.common.domain.restful.HttpResponseVO;
import cn.com.flaginfo.module.common.domain.restful.PageResponseVO;
import cn.com.flaginfo.module.common.domain.restful.RestfulResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: Meng.Liu
 * @date: 2018/11/12 上午9:49
 */
@Slf4j
public class RestfulResponseUtils {

    /**
     * 成功，返回空数据
     *
     * @return
     */
    public static RestfulResponse success() {
        return success(emptyHttpResponseVO());
    }

    /**
     * 操作成功，返回空数据
     *
     * @return
     */
    public static RestfulResponse operationSuccess() {
        return success(emptyHttpResponseVO(), ErrorCode.OPERATION_SUCCESS.message());
    }

    /**
     * 设置成功，返回空数据
     *
     * @return
     */
    public static RestfulResponse setupSuccess() {
        return success(emptyHttpResponseVO(), ErrorCode.SETUP_SUCCESS.message());
    }

    /**
     * 保存成功，返回空数据
     *
     * @return
     */
    public static RestfulResponse saveSuccess() {
        return success(emptyHttpResponseVO(), ErrorCode.SAVE_SUCCESS.message());
    }
    
    /**
     * 更新成功，返回空数据
     *
     * @return
     */
    public static RestfulResponse updateSuccess() {
        return success(emptyHttpResponseVO(), ErrorCode.UPDATE_SUCCESS.message());
    }
    
    /**
     * 成功返回Object
     *
     * @param data
     * @return
     */
    public static <T> RestfulResponse<T> success(T data) {
        return success(data, ErrorCode.SUCCESS.message());
    }

    /**
     * 成功返回Object数据
     *
     * @param data
     * @param message
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> RestfulResponse<T> success(T data, String message) {
        RestfulResponse.RestfulResponseBuilder<T> responseBuilder = RestfulResponse.successBuilder();
        return (RestfulResponse<T>)responseBuilder.setData(data)
                .setMessage(message).build();
    }

    /**
     * 成功返回列表
     *
     * @param data
     * @param dataCount
     * @return
     */
    public static RestfulResponse success(List<Object> data, int dataCount) {
        return success(data, dataCount, ErrorCode.SUCCESS.message());
    }

    /**
     * 成功，返回查询列表
     *
     * @param data
     * @param dataCount
     * @param message
     * @return
     */
    public static <T> RestfulResponse<T> success(List<Object> data, int dataCount, String message) {
        PageResponseVO<List<Object>> responseVO = new PageResponseVO<>();
        responseVO.setData(data);
        responseVO.setDataCount(dataCount);
        RestfulResponse.RestfulResponseBuilder responseBuilder = RestfulResponse.successBuilder();
        return responseBuilder.setData(responseVO)
                .setMessage(message).build();
    }

    /**
     * 失败
     *
     * @param restfulCode
     * @return
     */
    public static <T> RestfulResponse<T> error(ErrorCode restfulCode) {
        return error(restfulCode.code(), restfulCode.message());
    }

    /**
     * 失败
     *
     * @param message
     * @return
     */
    public static <T> RestfulResponse<T> error(String message) {
        return error(ErrorCode.SYS_BUSY.code(), message);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> RestfulResponse<T> error(long code, String message) {
        return (RestfulResponse<T>)RestfulResponse.builder()
                .setCode(code)
                .setMessage(message)
                .build();
    }

    /**
     * 判断是否成功
     *
     * @param restfulResponse
     * @return
     */
    public static boolean isSuccess(RestfulResponse restfulResponse) {
        if (null == restfulResponse) {
            return false;
        }
        return ErrorCode.SUCCESS.code().equals(restfulResponse.getCode());
    }


    private static final HttpResponseVO EMPTY_HTTP_RESPONSE_VO = new EmptyHttpResponseVO<>();

    /**
     * 获取空数据对象
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> HttpResponseVO<T> emptyHttpResponseVO(){
        return (HttpResponseVO<T>)EMPTY_HTTP_RESPONSE_VO;
    }

    /**
     * 空数据对象
     * @author: Meng.Liu
     * @date: 2018/11/9 下午3:04
     */
    private static class EmptyHttpResponseVO<T> extends HttpResponseVO<T> {

        @Override
        public String toString() {
            return this.getClass().getName() + ":[Empty Data]";
        }

        /**
         * 是否为空
         * @return
         */
        public boolean isEmpty(){
            return true;
        }

        /**
         * 是否为空
         * @return
         */
        public boolean isNull(){
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if( null == obj ){
                return true;
            }
            return obj instanceof EmptyHttpResponseVO;
        }
    }
}
