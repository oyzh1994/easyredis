package cn.oyzh.easyredis.exception;

/**
 * @author oyzh
 * @since 2023/8/14
 */
public class DataTooBigException extends RuntimeException {

    public DataTooBigException() {
        this("数据太大");
    }

    public DataTooBigException(String msg) {
        super(msg);
    }
}
