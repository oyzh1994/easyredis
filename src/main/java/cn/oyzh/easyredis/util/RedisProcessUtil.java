package cn.oyzh.easyredis.util;

import cn.oyzh.common.util.JarUtil;
import cn.oyzh.common.util.ProcessUtil;
import cn.oyzh.easyredis.EasyRedisBootstrap;
import cn.oyzh.fx.plus.window.StageManager;
import lombok.experimental.UtilityClass;

/**
 * @author oyzh
 * @since 2024-12-18
 */
@UtilityClass
public class RedisProcessUtil {

    /**
     * 重启应用
     */
    public static void restartApplication() {
        // jar中
        if (JarUtil.isInJar()) {
            ProcessUtil.restartApplication("org.springframework.boot.loader.JarLauncher", 10, StageManager::exit);
        } else {// 正常环境
            ProcessUtil. restartApplication(EasyRedisBootstrap.class, 10, StageManager::exit);
        }
    }
}
