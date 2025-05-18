package cn.com.anysdk.redis.factory;

import cn.com.anysdk.redis.api.IRedisService;
import cn.com.anysdk.redis.api.IRedisConfig;
import cn.com.anysdk.redis.exception.RedisException;
import cn.com.anysdk.redis.impl.JedisRedisService;
import cn.com.anysdk.redis.impl.LettuceRedisService;
import cn.com.anysdk.redis.impl.RedissonRedisService;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis服务工厂
 * 用于创建不同类型的Redis服务实例
 */
@Slf4j
public class RedisServiceFactory {
    /**
     * 创建Redis服务实例
     * @param config Redis配置
     * @return Redis服务实例
     */
    public static IRedisService createService(IRedisConfig config) {
        if (config == null) {
            throw new RedisException("Redis config cannot be null");
        }

        String provider = config.getProvider();
        switch (provider.toLowerCase()) {
            case "jedis":
                return new JedisRedisService(config);
            case "lettuce":
                return new LettuceRedisService(config);
            case "redisson":
                return new RedissonRedisService(config);
            default:
                throw new RedisException("Unsupported Redis provider: " + provider);
        }
    }
}