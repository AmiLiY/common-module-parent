package cn.com.flaginfo.module.common.mvc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: LiuMeng
 * @date: 2019/10/30
 * TODO:
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomMvcAdviceDefinitionRegister.class)
public @interface EnableCustomMvcAdapter {
}
