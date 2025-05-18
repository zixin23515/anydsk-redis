# AnySDK-Redis

## 项目介绍

AnySDK-Redis 是一个统一的 Redis 操作接口库，支持多种 Redis 客户端实现（Jedis、Lettuce、Redisson），提供简单易用的 API 来操作 Redis。

## 特性

- 统一的 Redis 操作接口，屏蔽底层实现差异
- 支持多种 Redis 客户端实现：Jedis、Lettuce、Redisson
- 简单的配置方式，易于集成和使用
- 提供代理工厂，支持监控和异常处理
- 完整的类型支持，包括字符串、哈希、列表、集合等

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>cn.com.anysdk</groupId>
    <artifactId>anysdk-redis</artifactId>
    <version>0.0.1</version>
</dependency>

<!-- 根据需要选择一个或多个客户端实现 -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>4.3.1</version>
</dependency>
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>6.2.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.20.0</version>
</dependency>
```

### 基本使用

```java
// 创建配置
JedisRedisConfig config = JedisRedisConfig.create("localhost", 6379, "");

// 创建服务实例
IRedisService redisService = RedisServiceFactory.createService(config);

// 使用服务
redisService.set("key", "value");
String value = redisService.get("key");

// 使用带监控的代理服务
IRedisService proxiedService = RedisServiceProxyFactory.createServiceProxy(config);
proxiedService.set("key", "value", 60, TimeUnit.SECONDS);
```

## 核心接口

### IRedisConfig

Redis 配置接口，用于管理不同 Redis 客户端的配置信息。

```java
public interface IRedisConfig {
    String getHost();
    int getPort();
    String getPassword();
    int getDatabase();
    int getConnectTimeout();
    int getOperationTimeout();
    int getMaxConnections();
    boolean isUseSsl();
    String getProvider();
}
```

### IRedisService

Redis 服务接口，定义了 Redis 服务的核心操作方法。

```java
public interface IRedisService {
    // 字符串操作
    void set(String key, String value);
    void set(String key, String value, long timeout, TimeUnit unit);
    String get(String key);
    
    // 键操作
    boolean delete(String key);
    long delete(List<String> keys);
    boolean expire(String key, long timeout, TimeUnit unit);
    boolean hasKey(String key);
    long getExpire(String key, TimeUnit unit);
    
    // 哈希操作
    void hSet(String key, String field, Object value);
    Object hGet(String key, String field);
    void hSetAll(String key, Map<String, Object> map);
    Map<Object, Object> hGetAll(String key);
    
    // 列表操作
    long lPush(String key, String value);
    long rPush(String key, String value);
    List<String> lRange(String key, long start, long end);
    
    // 集合操作
    long sAdd(String key, String... values);
    Set<String> sMembers(String key);
}
```

## 实现类

- `JedisRedisService`: 基于 Jedis 客户端的实现
- `LettuceRedisService`: 基于 Lettuce 客户端的实现
- `RedissonRedisService`: 基于 Redisson 客户端的实现

## 工厂类

- `RedisServiceFactory`: 用于创建不同类型的 Redis 服务实例
- `RedisServiceProxyFactory`: 用于创建带有监控和异常处理功能的 Redis 服务代理实例

## 配置类

- `JedisRedisConfig`: Jedis 客户端配置实现

## 异常处理

- `RedisException`: Redis 操作异常类，用于统一处理 Redis 操作中的异常