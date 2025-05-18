package cn.com.anysdk.redis.impl;

import cn.com.anysdk.redis.api.IRedisConfig;
import cn.com.anysdk.redis.api.IRedisService;
import cn.com.anysdk.redis.exception.RedisException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Lettuce实现的Redis服务
 */
@Slf4j
public class LettuceRedisService implements IRedisService {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> commands;
    private final IRedisConfig config;

    public LettuceRedisService(IRedisConfig config) {
        this.config = config;
        
        RedisURI redisURI = RedisURI.builder()
                .withHost(config.getHost())
                .withPort(config.getPort())
                .withDatabase(config.getDatabase())
                .withTimeout(Duration.ofMillis(config.getOperationTimeout()))
                .withSsl(config.isUseSsl())
                .build();
                
        if (!config.getPassword().isEmpty()) {
            redisURI.setPassword(config.getPassword());
        }
        
        this.redisClient = RedisClient.create(redisURI);
        this.connection = redisClient.connect();
        this.commands = connection.sync();
        
        log.info("Initialized Lettuce Redis service with host: {}, port: {}", config.getHost(), config.getPort());
    }

    @Override
    public void set(String key, String value) {
        try {
            commands.set(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to set value: " + e.getMessage(), e);
        }
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            SetArgs args = SetArgs.Builder.px(unit.toMillis(timeout));
            commands.set(key, value, args);
        } catch (Exception e) {
            throw new RedisException("Failed to set value with expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String key) {
        try {
            return commands.get(key);
        } catch (Exception e) {
            throw new RedisException("Failed to get value: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            return commands.del(key) > 0;
        } catch (Exception e) {
            throw new RedisException("Failed to delete key: " + e.getMessage(), e);
        }
    }

    @Override
    public long delete(List<String> keys) {
        try {
            return commands.del(keys.toArray(new String[0]));
        } catch (Exception e) {
            throw new RedisException("Failed to delete keys: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return commands.expire(key, unit.toSeconds(timeout));
        } catch (Exception e) {
            throw new RedisException("Failed to set expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return commands.exists(key) > 0;
        } catch (Exception e) {
            throw new RedisException("Failed to check key existence: " + e.getMessage(), e);
        }
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        try {
            long seconds = commands.ttl(key);
            return seconds > 0 ? unit.convert(seconds, TimeUnit.SECONDS) : seconds;
        } catch (Exception e) {
            throw new RedisException("Failed to get expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSet(String key, String field, Object value) {
        try {
            commands.hset(key, field, String.valueOf(value));
        } catch (Exception e) {
            throw new RedisException("Failed to set hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public Object hGet(String key, String field) {
        try {
            return commands.hget(key, field);
        } catch (Exception e) {
            throw new RedisException("Failed to get hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            Map<String, String> stringMap = new HashMap<>();
            map.forEach((k, v) -> stringMap.put(k, String.valueOf(v)));
            commands.hmset(key, stringMap);
        } catch (Exception e) {
            throw new RedisException("Failed to set all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            Map<String, String> map = commands.hgetall(key);
            Map<Object, Object> result = new HashMap<>();
            map.forEach(result::put);
            return result;
        } catch (Exception e) {
            throw new RedisException("Failed to get all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public long lPush(String key, String value) {
        try {
            return commands.lpush(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public long rPush(String key, String value) {
        try {
            return commands.rpush(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        try {
            return commands.lrange(key, start, end);
        } catch (Exception e) {
            throw new RedisException("Failed to get list range: " + e.getMessage(), e);
        }
    }

    @Override
    public long sAdd(String key, String... values) {
        try {
            return commands.sadd(key, values);
        } catch (Exception e) {
            throw new RedisException("Failed to add to set: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> sMembers(String key) {
        try {
            return commands.smembers(key);
        } catch (Exception e) {
            throw new RedisException("Failed to get set members: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (connection != null) {
                connection.close();
            }
            if (redisClient != null) {
                redisClient.shutdown();
            }
        } finally {
            super.finalize();
        }
    }
}