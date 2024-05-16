package cn.oyzh.easyredis.fx;


import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;

import java.util.List;
import java.util.Locale;

/**
 * redis 键类型下拉框
 *
 * @author oyzh
 * @since 2023/8/11
 */
public class RedisKeyTypeComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {


    /**
     * 获取类型
     *
     * @return RedisKeyType
     */
    public RedisKeyType getType() {
        String type = this.getValue();
        if (type != null) {
            return RedisKeyType.valueOf(type.substring(0, type.indexOf("(")));
        }
        return null;
    }

    /**
     * 选择类型
     *
     * @param type 类型
     */
    public void select(RedisKeyType type) {
        if (type != null) {
            this.select(type.ordinal());
        }
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        for (RedisKeyType value : RedisKeyType.values()) {
            this.getItems().add(value.name());
        }
        // if (I18nManager.currentLocale() == Locale.SIMPLIFIED_CHINESE) {
        //     this.getItems().add("HYPERLOGLOG(统计值)");
        //     this.getItems().add("GEO(地理坐标)");
        //     this.getItems().add("BITMAP(位图)");
        // } else if (I18nManager.currentLocale() == Locale.TRADITIONAL_CHINESE) {
        //     this.getItems().add("HYPERLOGLOG(統計值)");
        //     this.getItems().add("GEO(地理坐標)");
        //     this.getItems().add("BITMAP(位圖)");
        // } else {
            this.getItems().add("HYPERLOGLOG");
            this.getItems().add("GEO");
            this.getItems().add("BITMAP");
        // }
        return this.getItems();
    }
}
