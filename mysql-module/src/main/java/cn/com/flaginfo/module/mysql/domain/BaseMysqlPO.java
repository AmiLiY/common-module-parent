package cn.com.flaginfo.module.mysql.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author LiuMeng
 * @version 1.0
 * @className BaseMysqlPO
 * @describe TODO
 * @date 2019/7/17 17:16
 */
@Getter
@Setter
@ToString
@KeySequence
public class BaseMysqlPO implements IBaseMysqlPO{

    @TableId(value = "id", type = IdType.INPUT)
    @JSONField(serializeUsing = com.alibaba.fastjson.serializer.ToStringSerializer.class)
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    /**
     * 数据状态
     */
    @TableField(value = "status", fill = FieldFill.INSERT)
    private Integer status;
    /**
     * 数据有效性，逻辑删除控制字段
     */
    @TableLogic
    @TableField(value = "flag", fill = FieldFill.INSERT)
    private Boolean flag;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
