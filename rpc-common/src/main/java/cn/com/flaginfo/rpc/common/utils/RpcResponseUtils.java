package cn.com.flaginfo.rpc.common.utils;

import cn.com.flaginfo.exception.rpc.RpcErrorException;
import cn.com.flaginfo.exception.rpc.RpcException;
import cn.com.flaginfo.exception.rpc.RpcNoResponseException;
import cn.com.flaginfo.exception.rpc.RpcNullException;
import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.api.common.base.PagableResponse;
import cn.com.flaginfo.platform.api.common.base.PageInfo;
import cn.com.flaginfo.rpc.common.domain.IRpcDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author: Meng.Liu
 * @date: 2018/11/9 下午5:37
 */
public class RpcResponseUtils {

    private static final Long SUCCESS = 200L;
    private static final Long ERROR_CODE = 400000L;

    private static final String SUCCESS_MSG = "请求成功";

    /**
     * 成功，返回空数据
     * @return
     */
    public static BaseResponse success(){
        return success(IRpcDTO.emptyDTO());
    }

    /**
     * 成功返回Object
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return success(data, SUCCESS_MSG);
    }

    /**
     * 成功返回Object数据
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data, String message){
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setData(data);
        baseResponse.setMessage(message);
        return baseResponse;
    }

    /**
     * 成功返回列表
     * @param data
     * @param dataCount
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<List<T>> success(List<T> data, long dataCount){
        return success(data, dataCount, SUCCESS_MSG);
    }

    public static <D> PagableResponse<List<D>> success(PageInfo pageInfo, List<D> data, long total){
        PagableResponse<List<D>> pagableResponse = new PagableResponse<>();
        if(CollectionUtils.isEmpty(data)){
            data = Collections.emptyList();
        }
        BeanUtils.copyProperties(pageInfo, pagableResponse);
        pagableResponse.setData(data);
        pagableResponse.setDataCount(data.size());
        pagableResponse.setTotal((int)total);
        pagableResponse.setCode(SUCCESS);
        pagableResponse.setMessage(SUCCESS_MSG);
        return pagableResponse;
    }

    public static PagableResponse message(String message){
        return error(ERROR_CODE, message);
    }

    public static PagableResponse error(Long code, String message){
        PagableResponse pagableResponse = new PagableResponse();
        pagableResponse.setCode(code);
        pagableResponse.setMessage(message);
        return pagableResponse;
    }

    /**
     * 成功，返回查询列表
     * @param data
     * @param dataCount
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<List<T>> success(List<T> data, long dataCount, String message){
        BaseResponse<List<T>> baseResponse = new BaseResponse<>();
        baseResponse.setData(data);
        baseResponse.setDataCount((int)dataCount);
        baseResponse.setMessage(message);
        return baseResponse;
    }

    /**
     * 失败
     * @param message
     * @return
     */
    public static BaseResponse error(String message){
        return error(ERROR_CODE, message);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(long code, String message){
        return BaseResponse.error(code, message);
    }

    /**
     * 判断是否成功
     * @param baseResponse
     * @return
     */
    public static boolean isSuccess(BaseResponse baseResponse){
        if( null == baseResponse ){
            return false;
        }
        return SUCCESS.equals(baseResponse.getCode());
    }


    /**
     * 获取返回结果
     * @param baseResponse
     * @param <T>
     * @return
     * @throws RpcException
     */
    public static <T> T getResponseData(BaseResponse<T> baseResponse) throws RpcNullException, RpcErrorException, RpcNoResponseException {
        return getResponseData(baseResponse, false);
    }

    /**
     * 获取返回结果
     * @param baseResponse
     * @param canBeNull 是否可以返回空
     * @param <T>
     * @return
     * @throws RpcException
     */
    public static <T> T getResponseData(BaseResponse<T> baseResponse, boolean canBeNull) throws RpcNullException, RpcErrorException, RpcNoResponseException {
        if( null == baseResponse ){
            if( canBeNull ){
                return null;
            }
            throw new RpcNoResponseException("rpc response is null, cannot get data.");
        }
        if( !isSuccess(baseResponse) ){
            if( canBeNull ){
                return null;
            }
            throw new RpcErrorException(baseResponse.getCode(), baseResponse.getMessage());
        }
        T t = baseResponse.getData();
        if( null == t ){
            if( canBeNull ){
                return null;
            }
            throw new RpcNullException("rpc response data is null.");
        }
        return t;
    }
}
