package cn.com.anysdk.redis.impl;

import cn.com.anysdk.redis.api.IRedisConfig;
import cn.com.anysdk.redis.api.IRedisService;
import cn.com.anysdk.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Jedis实现的Redis服务
 */
@Slf4j
public class JedisRedisService implements IRedisService {
    private final JedisPool jedisPool;
    private final IRedisConfig config;

    public JedisRedisService(IRedisConfig config) {
        this.config = config;
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getMaxConnections());
        poolConfig.setMaxIdle(config.getMaxConnections() / 4);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        
        this.jedisPool = new JedisPool(
                poolConfig,
                config.getHost(),
                config.getPort(),
                config.getConnectTimeout(),
                config.getPassword().isEmpty() ? null : config.getPassword(),
                config.getDatabase(),
                config.isUseSsl()
        );
        
        log.info("Initialized Jedis Redis service with host: {}, port: {}", config.getHost(), config.getPort());
    }

    @Override
    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to set value: " + e.getMessage(), e);
        }
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, (int) unit.toSeconds(timeout), value);
        } catch (Exception e) {
            throw new RedisException("Failed to set value with expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            throw new RedisException("Failed to get value: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key) > 0;
        } catch (Exception e) {
            throw new RedisException("Failed to delete key: " + e.getMessage(), e);
        }
    }

    @Override
    public long delete(List<String> keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys.toArray(new String[0]));
        } catch (Exception e) {
            throw new RedisException("Failed to delete keys: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, (int) unit.toSeconds(timeout)) == 1;
        } catch (Exception e) {
            throw new RedisException("Failed to set expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasKey(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            throw new RedisException("Failed to check key existence: " + e.getMessage(), e);
        }
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            long seconds = jedis.ttl(key);
            return seconds > 0 ? unit.convert(seconds, TimeUnit.SECONDS) : seconds;
        } catch (Exception e) {
            throw new RedisException("Failed to get expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSet(String key, String field, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, field, String.valueOf(value));
        } catch (Exception e) {
            throw new RedisException("Failed to set hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public Object hGet(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            throw new RedisException("Failed to get hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> stringMap = new HashMap<>();
            map.forEach((k, v) -> stringMap.put(k, String.valueOf(v)));
            jedis.hmset(key, stringMap);
        } catch (Exception e) {
            throw new RedisException("Failed to set all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> map = jedis.hgetAll(key);
            Map<Object, Object> result = new HashMap<>();
            map.forEach(result::put);
            return result;
        } catch (Exception e) {
            throw new RedisException("Failed to get all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public long lPush(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public long rPush(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            throw new RedisException("Failed to get list range: " + e.getMessage(), e);
        }
    }

    @Override
    public long sAdd(String key, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, values);
        } catch (Exception e) {
            throw new RedisException("Failed to add to set: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> sMembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            throw new RedisException("Failed to get set members: " + e.getMessage(), e);
        }
    }
}