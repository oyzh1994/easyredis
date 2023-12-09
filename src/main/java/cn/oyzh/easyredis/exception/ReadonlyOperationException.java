package cn.oyzh.easyredis.exception;

/**
 * @author oyzh
 * @since 2023/12/09
 */
public class ReadonlyOperationException extends RuntimeException {

    public ReadonlyOperationException() {
        this("只读模式不支持此操作");
    }

    public ReadonlyOperationException(String msg) {
        super(msg);
    }
}
