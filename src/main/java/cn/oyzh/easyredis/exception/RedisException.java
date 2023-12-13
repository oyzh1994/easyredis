package cn.oyzh.easyredis.exception;

/**
 * redis异常
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class RedisException extends RuntimeException {

    public RedisException() {
        super();
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Exception ex) {
        super(ex);
    }
}
