package cn.com.anysdk.redis.api;

/**
 * Redis配置接口
 * 定义了Redis服务所需的基本配置项
 */
public interface IRedisConfig {
    /**
     * 获取Redis服务器地址
     */
    String getHost();

    /**
     * 获取Redis服务器端口
     */
    int getPort();

    /**
     * 获取Redis密码
     */
    String getPassword();

    /**
     * 获取Redis数据库索引
     */
    int getDatabase();

    /**
     * 获取连接超时时间（毫秒）
     */
    int getConnectTimeout();

    /**
     * 获取操作超时时间（毫秒）
     */
    int getOperationTimeout();

    /**
     * 获取最大连接数
     */
    int getMaxConnections();

    /**
     * 是否启用SSL连接
     */
    boolean isUseSsl();

    /**
     * 获取Redis客户端提供商
     * @return 客户端提供商名称（jedis, lettuce, redisson等）
     */
    String getProvider();
}