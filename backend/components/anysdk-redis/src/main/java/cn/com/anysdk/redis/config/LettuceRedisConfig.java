package cn.com.anysdk.redis.config;

import cn.com.anysdk.redis.api.IRedisConfig;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Lettuce Redis配置实现
 */
@Data
@Accessors(chain = true)
public class LettuceRedisConfig implements IRedisConfig {
    private String host = "localhost";
    private int port = 6379;
    private String password = "";
    private int database = 0;
    private int connectTimeout = 3000;
    private int operationTimeout = 3000;
    private int maxConnections = 8;
    private boolean useSsl = false;
    private final String provider = "lettuce";

    /**
     * 创建默认配置
     * @return 默认配置
     */
    public static LettuceRedisConfig createDefault() {
        return new LettuceRedisConfig();
    }

    /**
     * 创建自定义配置
     * @param host 主机地址
     * @param port 端口
     * @param password 密码
     * @return 自定义配置
     */
    public static LettuceRedisConfig create(String host, int port, String password) {
        return new LettuceRedisConfig()
                .setHost(host)
                .setPort(port)
                .setPassword(password);
    }
}