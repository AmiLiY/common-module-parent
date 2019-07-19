package cn.com.flaginfo.module.mysql.config;

/**
 * 当前用户的操作对象
 *
 * @author LiuMeng
 * @version 1.0
 * @className ICurrentOperationUser
 * @describe TODO
 * @date 2019/7/17 17:46
 */
public interface ICurrentOperationUser {

    String UNKNOWN_USER = "Unknown";

    /**
     * 获取操作用户
     *
     * @return
     */
    String operationUser();

    /**
     * 默认操作用户
     */
    class DefaultCurrentOperationUser implements ICurrentOperationUser {

        @Override
        public String operationUser() {
            return UNKNOWN_USER;
        }
    }
}
