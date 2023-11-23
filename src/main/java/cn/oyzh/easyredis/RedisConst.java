package cn.oyzh.easyredis;

import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * redis常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */
@UtilityClass
public class RedisConst {

    /**
     * fxml基础地址
     */
    public final static String FXML_BASE_PATH = "/views/";

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easyredis" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/redis.png";

}
