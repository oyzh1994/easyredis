package cn.oyzh.easyredis.fx;


import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * redis 键类型下拉框
 *
 * @author oyzh
 * @since 2023/8/11
 */
public class RedisKeyTypeComboBox extends FlexComboBox<String> {

    {
        for (RedisKeyType value : RedisKeyType.values()) {
            this.getItems().add(value.name() + "(" + value.desc() + ")");
        }
        this.getItems().add("GEO(地理坐标)");
        this.getItems().add("BITMAP(位图)");
    }

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
}
