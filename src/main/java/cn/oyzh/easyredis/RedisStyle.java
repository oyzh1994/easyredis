package cn.oyzh.easyredis;

import cn.oyzh.fx.plus.FXStyle;
import lombok.experimental.UtilityClass;

/**
 * redis样式文件常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */
@UtilityClass
public class RedisStyle {

    /**
     * 通用样式文件
     */
    // public final static String COMMON = FXStyle.EASY_FX;
    public final static String COMMON = FXStyle.CONTROL_FX + ";" + FXStyle.BOOTSTRAP_FX ;

    /**
     * 主页样式文件
     */
    // public final static String MAIN = COMMON + ";/css/main.css";
    public final static String MAIN = FXStyle.JMETRO + ";" + FXStyle.JMETRO_LIGHT_THEME + ";" + COMMON + ";/css/main.css";

}
