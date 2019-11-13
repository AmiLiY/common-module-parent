package cn.com.flaginfo.module.reflect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author: LiuMeng
 * @date: 2019/11/12
 * TODO:
 */
public class PointUtils {

    /**
     * 获取aop切入点方法的参数类型
     *
     * @param point
     * @return
     */
    public static Class<?>[] getPointParameterTypes(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getParameterTypes();
    }


    /**
     * 获取aop切入点方法的参数类型
     *
     * @param point
     * @return
     */
    public static String[] getPointParameterNames(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getParameterNames();
    }

    /**
     * 获取aop切入点方法的返回对象
     *
     * @param point
     * @return
     */
    public static Class<?> getPointReturnType(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getReturnType();
    }


    /**
     * 获取aop切入点方法注解
     * @param point
     * @param aClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getAnnotation(ProceedingJoinPoint point, Class<A> aClass) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method, aClass);
    }
}


