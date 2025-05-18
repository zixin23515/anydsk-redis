package cn.com.anysdk.redis.factory;

import cn.com.anysdk.redis.api.IRedisConfig;
import cn.com.anysdk.redis.api.IRedisService;
import cn.com.anysdk.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Redis服务代理工厂
 * 用于创建带有监控和异常处理功能的Redis服务实例
 */
@Slf4j
public class RedisServiceProxyFactory {
    /**
     * 创建Redis服务代理实例
     * @param config Redis配置
     * @return Redis服务代理实例
     */
    public static IRedisService createServiceProxy(IRedisConfig config) {
        IRedisService redisService = RedisServiceFactory.createService(config);
        return createServiceProxy(redisService);
    }

    /**
     * 为现有Redis服务创建代理实例
     * @param redisService Redis服务实例
     * @return Redis服务代理实例
     */
    public static IRedisService createServiceProxy(IRedisService redisService) {
        if (redisService == null) {
            throw new RedisException("Redis service cannot be null");
        }

        return (IRedisService) Proxy.newProxyInstance(
                redisService.getClass().getClassLoader(),
                new Class<?>[] { IRedisService.class },
                new RedisServiceInvocationHandler(redisService)
        );
    }

    /**
     * Redis服务调用处理器
     * 用于在Redis服务方法调用前后添加监控和异常处理逻辑
     */
    private static class RedisServiceInvocationHandler implements InvocationHandler {
        private final IRedisService target;

        public RedisServiceInvocationHandler(IRedisService target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            long startTime = System.currentTimeMillis();
            
            try {
                // 执行目标方法
                Object result = method.invoke(target, args);
                
                // 记录执行时间
                long executionTime = System.currentTimeMillis() - startTime;
                if (executionTime > 100) { // 记录执行时间超过100ms的操作
                    log.warn("Redis operation [{}] took {}ms to execute", methodName, executionTime);
                } else {
                    log.debug("Redis operation [{}] took {}ms to execute", methodName, executionTime);
                }
                
                return result;
            } catch (Exception e) {
                log.error("Redis operation [{}] failed: {}", methodName, e.getMessage());
                
                // 如果是反射异常，则获取原始异常
                Throwable cause = e.getCause();
                if (cause != null) {
                    throw cause;
                } else {
                    throw e;
                }
            }
        }
    }
}