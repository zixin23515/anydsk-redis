package cn.com.anysdk.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis服务接口
 * 定义了Redis服务的核心操作方法
 */
public interface IRedisService {
    /**
     * 设置字符串值
     * @param key 键
     * @param value 值
     */
    void set(String key, String value);

    /**
     * 设置字符串值并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, String value, long timeout, TimeUnit unit);

    /**
     * 获取字符串值
     * @param key 键
     * @return 值
     */
    String get(String key);

    /**
     * 删除键
     * @param key 键
     * @return 是否成功
     */
    boolean delete(String key);

    /**
     * 批量删除键
     * @param keys 键集合
     * @return 成功删除的数量
     */
    long delete(List<String> keys);

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 获取过期时间
     * @param key 键
     * @param unit 时间单位
     * @return 过期时间
     */
    long getExpire(String key, TimeUnit unit);

    /**
     * 哈希表设置字段值
     * @param key 键
     * @param field 字段
     * @param value 值
     */
    void hSet(String key, String field, Object value);

    /**
     * 哈希表获取字段值
     * @param key 键
     * @param field 字段
     * @return 值
     */
    Object hGet(String key, String field);

    /**
     * 哈希表设置多个字段值
     * @param key 键
     * @param map 字段值映射
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     * 哈希表获取所有字段值
     * @param key 键
     * @return 字段值映射
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 列表左侧添加元素
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    long lPush(String key, String value);

    /**
     * 列表右侧添加元素
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    long rPush(String key, String value);

    /**
     * 获取列表指定范围的元素
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 元素列表
     */
    List<String> lRange(String key, long start, long end);

    /**
     * 集合添加元素
     * @param key 键
     * @param values 值
     * @return 添加成功的数量
     */
    long sAdd(String key, String... values);

    /**
     * 获取集合所有元素
     * @param key 键
     * @return 元素集合
     */
    Set<String> sMembers(String key);
}