package cn.oyzh.easyredis.exception;

/**
 * @author oyzh
 * @since 2023/08/06
 */
public class SentinelOperationException extends RedisException {

    public SentinelOperationException() {
        this("哨兵连接不支持此操作");
    }

    public SentinelOperationException(String msg) {
        super(msg);
    }
}
