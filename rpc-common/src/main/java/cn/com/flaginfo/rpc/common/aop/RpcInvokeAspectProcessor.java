package cn.com.flaginfo.rpc.common.aop;

import cn.com.flaginfo.exception.rpc.RpcRuntimeException;
import cn.com.flaginfo.rpc.common.configuration.RpcConfiguration;
import cn.com.flaginfo.rpc.common.domain.RpcLogInfo;
import cn.com.flaginfo.rpc.common.service.IRpcSlowlyHandle;
import com.alibaba.com.caucho.hessian.HessianException;
import com.alibaba.dubbo.rpc.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

/**
 * @author: Meng.Liu
 * @date: 2018/12/11 上午10:57
 */
@Slf4j
@Component
@Aspect
public class RpcInvokeAspectProcessor {

    @Autowired(required = false)
    private RpcConfiguration rpcConfiguration;

    @Autowired(required = false)
    private IRpcSlowlyHandle rpcSlowlyHandle;

    @PostConstruct
    private void init() {
        if (null == rpcConfiguration) {
            rpcConfiguration = new RpcConfiguration();
        }
    }

    @Pointcut("@within(cn.com.flaginfo.rpc.common.aop.RpcInterface)")
    private void annotation() {
    }

    @Around("annotation()")
    public Object aroundPoint(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Class<?> clazz = pjp.getTarget().getClass();
        RpcInterface rpcInterface = AnnotationUtils.findAnnotation(clazz, RpcInterface.class);
        if (null == rpcInterface) {
            log.warn("rpc interface is null.");
            return pjp.proceed();
        }
        RpcLogInfo.RpcLogInfoBuilder rpcLogInfoBuilder = null;
        boolean logOff = rpcConfiguration.getLogLevel() == RpcConfiguration.RpcLogLevel.Off;
        if (!logOff) {
            rpcLogInfoBuilder = RpcLogInfo.builder();
            rpcLogInfoBuilder.invokeStartTimestamp(start);
        }
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        if (!logOff) {
            if (StringUtils.isNotBlank(rpcInterface.serviceName())) {
                rpcLogInfoBuilder.serviceName(rpcInterface.serviceName());
            } else {
                rpcLogInfoBuilder.serviceName(rpcInterface.service().name());
            }
            rpcLogInfoBuilder.interfaceName(rpcInterface.serviceInterface().getSimpleName());
            rpcLogInfoBuilder.methodName(method.getName());
            if (rpcConfiguration.getLogLevel() == RpcConfiguration.RpcLogLevel.Detailed
                    || rpcConfiguration.getLogLevel() == RpcConfiguration.RpcLogLevel.Detailed_When_Slow) {
                rpcLogInfoBuilder.parameterTypes(method.getParameterTypes());
                rpcLogInfoBuilder.returnType(method.getReturnType());
                rpcLogInfoBuilder.args(pjp.getArgs());
            }
        }
        boolean isSuccess = true;
        long errorCode = rpcInterface.serviceCode() != Long.MIN_VALUE ? rpcInterface.serviceCode() : rpcInterface.service().errorCode().code();
        String errMessage = StringUtils.isNotBlank(rpcInterface.errorMessage()) ? rpcInterface.errorMessage() : rpcInterface.service().errorCode().message();
        try {
            Object obj = pjp.proceed();
            if (!logOff) {
                rpcLogInfoBuilder.result(obj);
            }
            return obj;
        } catch (HessianException e) {
            log.error("Rpc Service HessianException, service:{}, exception:{}", rpcInterface.serviceName(), e.getMessage());
            isSuccess = false;
            throw new RpcRuntimeException(errorCode, errMessage);
        } catch (RpcException e) {
            log.error("Rpc Service RpcException, service:{}, exception:{}", rpcInterface.serviceName(), e.getMessage());
            isSuccess = false;
            throw new RpcRuntimeException(errorCode, errMessage);
        } catch (Exception e) {
            log.error("Rpc called failed, cause : {}", e.getMessage());
            isSuccess = false;
            throw e;
        } finally {
            if (!logOff) {
                rpcLogInfoBuilder.isSuccess(isSuccess);
                rpcLogInfoBuilder.takeTime(System.currentTimeMillis());
            }
            this.doCallBack(rpcLogInfoBuilder, rpcConfiguration);
        }
    }

    private void doCallBack(RpcLogInfo.RpcLogInfoBuilder rpcLogInfoBuilder, RpcConfiguration rpcConfiguration) {
        RpcLogInfo rpcLogInfo = null == rpcLogInfoBuilder ? null : rpcLogInfoBuilder.build();
        if (null == rpcConfiguration
                || rpcConfiguration.getLogLevel() == RpcConfiguration.RpcLogLevel.Off
                || null == rpcLogInfo) {
            return;
        }
        if (null != rpcSlowlyHandle) {
            switch (rpcConfiguration.getLogLevel()) {
                case Detailed:
                    rpcSlowlyHandle.callbackIfConfigureAsDetails(rpcLogInfo, rpcConfiguration);
                    break;
                case Detailed_When_Slow:
                    if (rpcLogInfo.getTakeTime() > rpcConfiguration.getSlowThreshold()) {
                        rpcSlowlyHandle.callbackIfConfigureAsDetailsWhenSlowly(rpcLogInfo, rpcConfiguration);
                    }
                    break;
                case Succinct_When_Slow:
                    if (rpcLogInfo.getTakeTime() > rpcConfiguration.getSlowThreshold()) {
                        rpcSlowlyHandle.callbackIfConfigureAsSuccinctWhenSlowly(rpcLogInfo, rpcConfiguration);
                    }
                    break;
                case Succinct:
                    rpcSlowlyHandle.callbackIfConfigureAsSuccinct(rpcLogInfo, rpcConfiguration);
                default:
                    break;
            }
        }
        if (rpcLogInfo.getTakeTime() > rpcConfiguration.getSlowThreshold()) {
            log.warn("Rpc handle rpc is too slow, take {}ms.", rpcLogInfo.getTakeTime());
        }
    }
}
