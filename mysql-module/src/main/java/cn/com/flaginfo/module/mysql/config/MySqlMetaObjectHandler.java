package cn.com.flaginfo.module.mysql.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 自定义内容填充
 *
 * @author LiuMeng
 * @version 1.0
 * @className CustomMetaObjectHandler
 * @describe TODO
 * @date 2019/7/19 14:00
 */
public class MySqlMetaObjectHandler implements MetaObjectHandler {

    private ICurrentOperationUser currentOperationUser;

    public MySqlMetaObjectHandler(ICurrentOperationUser currentOperationUser) {
        this.currentOperationUser = currentOperationUser;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setIfNull("status", 1, metaObject);
        this.setIfNull("flag", true, metaObject);
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setIfNull("createBy", currentOperationUser.operationUser(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setIfNull("updateBy", currentOperationUser.operationUser(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setIfNull("updateBy", currentOperationUser.operationUser(), metaObject);
    }

    private void setIfNull(String fieldName, Object value, MetaObject metaObject) {
        if (null == this.getFieldValByName(fieldName, metaObject)) {
            this.setFieldValByName(fieldName, value, metaObject);
        }
    }
}
