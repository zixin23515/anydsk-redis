package cn.com.anysdk.redis.exception;

/**
 * Redis操作异常
 */
public class RedisException extends RuntimeException {
    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 构造函数
     * @param message 错误信息
     */
    public RedisException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * @param message 错误信息
     * @param cause 原始异常
     */
    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public RedisException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原始异常
     */
    public RedisException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
}