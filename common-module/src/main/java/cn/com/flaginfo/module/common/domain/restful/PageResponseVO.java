package cn.com.flaginfo.module.common.domain.restful;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Meng.Liu
 * @date: 2018/11/12 上午9:53
 */
@Data
@ApiModel(description = "通用Restful分页请求结果数据")
public class PageResponseVO<D> extends HttpResponseVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("数据总量")
    private Integer dataCount;

    @ApiModelProperty("分页数据结果")
    private D data;

}
