package cn.com.flaginfo.module.common.mvc.wrap;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBodyFilter {

    /**
     * 返回的参数类型
     * @return
     */
    Class<?> type();

    /**
     * 排除的字段名
     *
     * @return
     */
    String[] exclude() default {};

    /**
     * 包含的字段名
     *
     * @return
     */
    String[] include() default {};
}
