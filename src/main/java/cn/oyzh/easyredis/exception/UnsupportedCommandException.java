package cn.oyzh.easyredis.exception;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * @author oyzh
 * @since 2023/7/31
 */
public class UnsupportedCommandException extends RedisException {

    public UnsupportedCommandException(String serverVersion, String supportedVersion, String command) {
        super(I18nResourceBundle.i18nString("base.cmd", "base.notSupport"));
        // super("指令:" + command + " 不支持，服务版本为:" + serverVersion + " 最低支持命令的服务版本为:" + supportedVersion);
    }
}
