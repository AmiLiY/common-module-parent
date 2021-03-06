package cn.com.flaginfo.redis.cache;

import cn.com.flaginfo.module.common.domain.LocalCache;
import cn.com.flaginfo.module.reflect.PointUtils;
import cn.com.flaginfo.redis.RedisUtils;
import cn.com.flaginfo.redis.lock.jedis.impl.RedisLockNx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis的解析进程
 * 所有方法显示捕获缓存操作异常，防止因为缓存出现异常导致主流程阻塞
 *
 * @author: Meng.Liu
 * @date: 2018/11/21 下午2:44
 */
@Aspect
@Component
@Slf4j
public class RedisCachePointProcessor {

    private final Object[] formatterLock = new Object[0];

    private static final String DISTRIBUTED_LOCK = "DistributedLock";

    private static final Map<Class<? extends IRedisCacheKeyFormatter>, IRedisCacheKeyFormatter> FORMATTER_CACHE = new HashMap<>();

    static {
        FORMATTER_CACHE.put(RedisCacheKeyDefaultFormatter.class, new RedisCacheKeyDefaultFormatter());
    }

    @Pointcut("@annotation(cn.com.flaginfo.redis.cache.RedisCache)")
    private void annotationPoint() {
    }


    @Around("annotationPoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RedisCache redisCache = PointUtils.getAnnotation(point, RedisCache.class);
        if (null == redisCache) {
            return this.doDefault(point);
        }
        switch (redisCache.opsType()) {
            case READ:
                return this.doReadCache(redisCache, point);
            case UPDATE:
                return this.doUpdateCache(redisCache, point);
            case DELETE:
                return this.doDeleteCache(redisCache, point);
            default:
                return this.doDefault(point);
        }
    }

    /**
     * 执行读取操作
     *
     * @param redisCache
     * @param point
     * @return
     * @throws Throwable
     */
    private Object doReadCache(RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        String cacheKey = this.generateCacheKey(redisCache, point);
        return this.tryToGetFormLocalCache(cacheKey, redisCache, point);
    }

    /**
     * 执行更新操作
     *
     * @param redisCache
     * @param point
     * @return
     * @throws Throwable
     */
    private Object doUpdateCache(RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        String cacheKey = this.generateCacheKey(redisCache, point);
        return this.invokeAndCache(cacheKey, redisCache, point);
    }

    /**
     * 执行删除操作
     *
     * @param redisCache
     * @param point
     * @return
     * @throws Throwable
     */
    private Object doDeleteCache(RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        String cacheKey = this.generateCacheKey(redisCache, point);
        try {
            return point.proceed();
        } finally {
            this.deleteRedisCache(cacheKey, redisCache);
            this.deleteLocalCache(cacheKey, redisCache);
        }
    }

    private Object doDefault(ProceedingJoinPoint point) throws Throwable {
        return point.proceed();
    }

    /**
     * 从本地缓存中获取
     *
     * @return
     */
    @Nullable
    private Object tryToGetFormLocalCache(String cacheKey, RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        Object object = this.getFromLocal(cacheKey, redisCache);
        if (null != object) {
            return object;
        }
        return this.tryToGetFormRedis(cacheKey, redisCache, point);
    }

    private Object getFromLocal(String cacheKey, RedisCache redisCache) {
        try {
            LocalCache localCache = this.getLocalCacheOps(redisCache);
            if (null != localCache) {
                Object object = localCache.get(cacheKey);
                if (null != object) {
                    if (log.isDebugEnabled()) {
                        log.debug("get value form local cache.");
                    }
                    return object;
                }
            }
        } catch (Exception e) {
            log.error("read value form local cache exception, try to get from redis.", e);
        }
        return null;
    }

    /**
     * 从Redis缓存中获取
     *
     * @return
     */
    @Nullable
    private Object tryToGetFormRedis(String cacheKey, RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        try {
            //只要redis中存在该key，则直接返回
            if (getRedisOps(redisCache).hasKey(cacheKey)) {
                if (log.isDebugEnabled()) {
                    log.debug("get value form redis cache.");
                }
                Object cacheValue = getRedisOps(redisCache).getValue(cacheKey);
                this.cache2LocalCache(cacheKey, redisCache, cacheValue);
                return cacheValue;
            }
        } catch (Exception e) {
            log.error("read value form redis exception, try to invoke method.", e);
        }
        if (redisCache.singleLoader()) {
            return this.invokeAndCacheSingleLoader(cacheKey, redisCache, point);
        } else {
            return this.invokeAndCache(cacheKey, redisCache, point);
        }
    }


    /**
     * 单一加载器执行
     *
     * @param cacheKey
     * @param redisCache
     * @param point
     * @return
     * @throws Throwable
     */
    private Object invokeAndCacheSingleLoader(String cacheKey, RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("cache expire, single loader will invoke method...");
        }
        String lockKey = RedisUtils.buildKey(DISTRIBUTED_LOCK, cacheKey);
        RedisLockNx lockNx = new RedisLockNx(lockKey, redisCache.lockExpire(), redisCache.tryLockTimeout());
        if (!lockNx.lock()) {
            log.error("get lock [{}] timeout, return null.", lockKey);
            return null;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("lock [{}] success.", lockKey);
            }
            Object cacheValue = this.getFromLocal(cacheKey, redisCache);
            if (null != cacheValue) {
                return cacheValue;
            } else {
                if (getRedisOps(redisCache).hasKey(cacheKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("get value form redis cache.");
                    }
                    cacheValue = getRedisOps(redisCache).getValue(cacheKey);
                    this.cache2LocalCache(cacheKey, redisCache, cacheValue);
                    return cacheValue;
                } else {
                    return this.invokeAndCache(cacheKey, redisCache, point);
                }
            }
        } finally {
            lockNx.unlock();
            if (log.isDebugEnabled()) {
                log.debug("unlock {}.", lockKey);
            }
        }
    }

    private Object invokeAndCache(String cacheKey, RedisCache redisCache, ProceedingJoinPoint point) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("cache expire, invoke method...");
        }
        Object obj = point.proceed();
        this.cache2Redis(cacheKey, redisCache, obj);
        this.cache2LocalCache(cacheKey, redisCache, obj);
        return obj;
    }

    /**
     * 缓存至Redis
     *
     * @param cacheKey
     * @param redisCache
     * @param obj
     */
    private void cache2Redis(String cacheKey, RedisCache redisCache, Object obj) {
        try {
            if (null != obj || !redisCache.ignoreNull()) {
                if (redisCache.expire() == -1) {
                    getRedisOps(redisCache).addValue(cacheKey, obj);
                } else {
                    getRedisOps(redisCache).addValue(cacheKey, obj, redisCache.expire(), redisCache.timeUnit());
                }
                if (log.isDebugEnabled()) {
                    log.debug("cache redis cache [{}]", cacheKey);
                }
            }
        } catch (Exception e) {
            log.error("cache redis cache error.", e);
        }
    }

    /**
     * 缓存至本地缓存
     *
     * @param cacheKey
     * @param redisCache
     * @param obj
     */
    private void cache2LocalCache(String cacheKey, RedisCache redisCache, Object obj) {
        try {
            if (null != obj) {
                LocalCache localCache = this.getLocalCacheOps(redisCache);
                if (null != localCache) {
                    localCache.put(cacheKey, obj);
                    if (log.isDebugEnabled()) {
                        log.debug("cache local cache [{}]", cacheKey);
                    }
                }
            }
        } catch (Exception e) {
            log.error("cache local cache error.", e);
        }
    }

    /**
     * 删除本地缓存
     *
     * @param cacheKey
     * @param redisCache
     */
    private void deleteRedisCache(String cacheKey, RedisCache redisCache) {
        try {
            getRedisOps(redisCache).delete(cacheKey);
            if (log.isDebugEnabled()) {
                log.debug("delete redis cache [{}]", cacheKey);
            }
        } catch (Exception e) {
            log.error("delete redis cache error.", e);
        }
    }

    /**
     * 删除本地缓存
     *
     * @param cacheKey
     * @param redisCache
     */
    private void deleteLocalCache(String cacheKey, RedisCache redisCache) {
        try {
            LocalCache localCache = this.getLocalCacheOps(redisCache);
            if (null != localCache) {
                localCache.remove(cacheKey);
                if (log.isDebugEnabled()) {
                    log.debug("delete local cache [{}]", cacheKey);
                }
            }
        } catch (Exception e) {
            log.error("delete local cache error.", e);
        }
    }

    /**
     * 获取本地缓存操作对象
     *
     * @param redisCache
     * @return
     */
    @Nullable
    private LocalCache getLocalCacheOps(RedisCache redisCache) {
        if (!redisCache.enableLocalCache()) {
            return null;
        }
        return RedisLocalCacheFactory.getLocalCache(redisCache);
    }

    /**
     * 获取redis操作对象
     *
     * @param redisCache
     * @return
     */
    private RedisUtils getRedisOps(RedisCache redisCache) {
        if (!StringUtils.isBlank(redisCache.dataSource())) {
            RedisUtils.selectSource(redisCache.dataSource());
        }
        if (-1 != redisCache.database()) {
            RedisUtils.selectDatabase(redisCache.database());
        }
        return RedisUtils.select();
    }

    /**
     * 生成缓存Key
     *
     * @param cache
     * @param point
     * @return
     */
    private String generateCacheKey(RedisCache cache, ProceedingJoinPoint point) {
        return this.getFormatterClass(cache.cacheKeyFormatter()).formatter(cache, PointUtils.getPointParameterNames(point), point.getArgs(), PointUtils.getPointReturnType(point));
    }

    /**
     * 获取处理函数
     *
     * @param formatterClass
     * @return
     */
    private IRedisCacheKeyFormatter getFormatterClass(Class<? extends IRedisCacheKeyFormatter> formatterClass) {
        if (!FORMATTER_CACHE.containsKey(formatterClass)) {
            synchronized (formatterLock) {
                if (!FORMATTER_CACHE.containsKey(formatterClass)) {
                    IRedisCacheKeyFormatter redisCacheKeyFormatter = null;
                    try {
                        redisCacheKeyFormatter = formatterClass.newInstance();
                    } catch (Exception e) {
                      throw new IllegalClassException("cannot instance formatter class which one : " + formatterClass.getName());
                    }
                    FORMATTER_CACHE.put(formatterClass, redisCacheKeyFormatter);
                    return redisCacheKeyFormatter;
                }
            }
        }
        return FORMATTER_CACHE.get(formatterClass);
    }

    public static void main(String[] args) {
        System.out.println(RedisCachePointProcessor.class.getName());
    }
}
