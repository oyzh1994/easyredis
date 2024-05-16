package cn.oyzh.easyredis.exception;

import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * @author oyzh
 * @since 2023/8/14
 */
public class DataTooBigException extends RedisException {

    public DataTooBigException() {
        this(I18nHelper.dataTooLarge());
        // this("数据太大");
    }

    public DataTooBigException(String msg) {
        super(msg);
    }
}
