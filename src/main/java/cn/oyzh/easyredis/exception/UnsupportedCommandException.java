package cn.oyzh.easyredis.exception;

/**
 * @author oyzh
 * @since 2023/7/31
 */
public class UnsupportedCommandException extends RedisException {

    public UnsupportedCommandException(String serverVersion, String supportedVersion, String command) {
        super("指令:" + command + " 不支持，服务版本为:" + serverVersion + " 最低支持命令的服务版本为:" + supportedVersion);
    }
}
