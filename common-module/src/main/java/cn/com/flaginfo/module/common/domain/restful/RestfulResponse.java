package cn.com.flaginfo.module.common.domain.restful;

import cn.com.flaginfo.exception.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author: Meng.Liu
 * @date: 2018/11/12 上午9:41
 */
@Data
@Slf4j
@ApiModel(description = "通用Restful响应结构体")
public class RestfulResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("请求状态码")
    private Long code;

    @ApiModelProperty("请求响应消息")
    private String message;

    @ApiModelProperty("请求结果数据")
    private T data;

    private RestfulResponse() {
    }

    public static class RestfulResponseBuilder<T>{

        private Long code;
        private String message;
        private T data;

        private RestfulResponseBuilder(){ }

        public RestfulResponseBuilder setCode(long code){
            this.code = code;
            return this;
        }

        public RestfulResponseBuilder setMessage(String message){
            this.message = message;
            return this;
        }

        public RestfulResponseBuilder setData(T data){
            this.data = data;
            return this;
        }

        private T createEmptyData(){
            log.warn("response is success, but there is no explicit setting of data. this is not recommended.");
            return null;
        }

        public RestfulResponse<T> build() throws NullPointerException{
            if( null == this.code ){
                throw new NullPointerException("response code cannot be null.");
            }
            if(ErrorCode.SUCCESS.code().equals(this.code) && null == this.data ){
                this.data = this.createEmptyData();
            }
            RestfulResponse<T> restfulResponse = new RestfulResponse<>();
            restfulResponse.setCode(this.code);
            restfulResponse.setData(this.data);
            restfulResponse.setMessage(this.message);
            return restfulResponse;
        }

    }

    public static <T> RestfulResponseBuilder<T> builder(){
        return new RestfulResponseBuilder<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> RestfulResponseBuilder<T> successBuilder(){
        RestfulResponseBuilder<T> responseBuilder = new RestfulResponseBuilder<>();
        return responseBuilder.setCode(ErrorCode.SUCCESS.code());
    }

    @SuppressWarnings("unchecked")
    public static <T> RestfulResponseBuilder<T> errorBuilder(){
        RestfulResponseBuilder<T> responseBuilder = new RestfulResponseBuilder<>();
        return responseBuilder.setCode(ErrorCode.SYS_BUSY.code());
    }
}