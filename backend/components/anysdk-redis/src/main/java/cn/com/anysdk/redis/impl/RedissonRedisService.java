package cn.com.anysdk.redis.impl;

import cn.com.anysdk.redis.api.IRedisConfig;
import cn.com.anysdk.redis.api.IRedisService;
import cn.com.anysdk.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redisson实现的Redis服务
 */
@Slf4j
public class RedissonRedisService implements IRedisService {
    private final RedissonClient redissonClient;
    private final IRedisConfig config;

    public RedissonRedisService(IRedisConfig config) {
        this.config = config;
        
        Config redissonConfig = new Config();
        String address = "redis://" + config.getHost() + ":" + config.getPort();
        
        if (config.isUseSsl()) {
            address = "rediss://" + config.getHost() + ":" + config.getPort();
        }
        
        redissonConfig.useSingleServer()
                .setAddress(address)
                .setDatabase(config.getDatabase())
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionPoolSize(config.getMaxConnections());
                
        if (!config.getPassword().isEmpty()) {
            redissonConfig.useSingleServer().setPassword(config.getPassword());
        }
        
        this.redissonClient = Redisson.create(redissonConfig);
        
        log.info("Initialized Redisson Redis service with host: {}, port: {}", config.getHost(), config.getPort());
    }

    @Override
    public void set(String key, String value) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set(value);
        } catch (Exception e) {
            throw new RedisException("Failed to set value: " + e.getMessage(), e);
        }
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set(value, timeout, unit);
        } catch (Exception e) {
            throw new RedisException("Failed to set value with expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String key) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            return bucket.get();
        } catch (Exception e) {
            throw new RedisException("Failed to get value: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            return redissonClient.getBucket(key).delete();
        } catch (Exception e) {
            throw new RedisException("Failed to delete key: " + e.getMessage(), e);
        }
    }

    @Override
    public long delete(List<String> keys) {
        try {
            return redissonClient.getKeys().delete(keys.toArray(new String[0]));
        } catch (Exception e) {
            throw new RedisException("Failed to delete keys: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redissonClient.getBucket(key).expire(timeout, unit);
        } catch (Exception e) {
            throw new RedisException("Failed to set expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return redissonClient.getBucket(key).isExists();
        } catch (Exception e) {
            throw new RedisException("Failed to check key existence: " + e.getMessage(), e);
        }
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        try {
            return redissonClient.getBucket(key).remainTimeToLive(unit);
        } catch (Exception e) {
            throw new RedisException("Failed to get expiration: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSet(String key, String field, Object value) {
        try {
            RMap<String, Object> map = redissonClient.getMap(key);
            map.put(field, value);
        } catch (Exception e) {
            throw new RedisException("Failed to set hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public Object hGet(String key, String field) {
        try {
            RMap<String, Object> map = redissonClient.getMap(key);
            return map.get(field);
        } catch (Exception e) {
            throw new RedisException("Failed to get hash field: " + e.getMessage(), e);
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            RMap<String, Object> rMap = redissonClient.getMap(key);
            rMap.putAll(map);
        } catch (Exception e) {
            throw new RedisException("Failed to set all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            RMap<Object, Object> map = redissonClient.getMap(key);
            return new HashMap<>(map);
        } catch (Exception e) {
            throw new RedisException("Failed to get all hash fields: " + e.getMessage(), e);
        }
    }

    @Override
    public long lPush(String key, String value) {
        try {
            RList<String> list = redissonClient.getList(key);
            list.add(0, value);
            return list.size();
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public long rPush(String key, String value) {
        try {
            RList<String> list = redissonClient.getList(key);
            list.add(value);
            return list.size();
        } catch (Exception e) {
            throw new RedisException("Failed to push to list: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        try {
            RList<String> list = redissonClient.getList(key);
            // Redisson的索引是从0开始的，如果end是-1，表示到列表末尾
            int size = list.size();
            int realEnd = (int) (end < 0 ? size + end + 1 : Math.min(end + 1, size));
            int realStart = (int) Math.max(start, 0);
            
            if (realStart >= size || realStart >= realEnd) {
                return new ArrayList<>();
            }
            
            return new ArrayList<>(list.subList(realStart, realEnd));
        } catch (Exception e) {
            throw new RedisException("Failed to get list range: " + e.getMessage(), e);
        }
    }

    @Override
    public long sAdd(String key, String... values) {
        try {
            RSet<String> set = redissonClient.getSet(key);
            long added = 0;
            for (String value : values) {
                if (set.add(value)) {
                    added++;
                }
            }
            return added;
        } catch (Exception e) {
            throw new RedisException("Failed to add to set: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> sMembers(String key) {
        try {
            RSet<String> set = redissonClient.getSet(key);
            return set.readAll();
        } catch (Exception e) {
            throw new RedisException("Failed to get set members: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (redissonClient != null && !redissonClient.isShutdown()) {
                redissonClient.shutdown();
            }
        } finally {
            super.finalize();
        }
    }
}