package cn.oyzh.easyredis;

import java.io.File;

/**
 * redis常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */

public class RedisConst {

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easyredis" + File.separator;

    /**
     * 缓存保存路径
     */
    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

    /**
     * 键缓存路径
     */
    public static final String KEY_CACHE_PATH = CACHE_PATH + "key_cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/redis_no_bg.png";

    /**
     * 托盘图标，windows专用
     */
    public final static String ICON_24_PATH = "/image/redis_24.png";

    /**
     * 任务栏图标，windows专用
     */
    public final static String ICON_32_PATH = "/image/redis_32.png";

}
