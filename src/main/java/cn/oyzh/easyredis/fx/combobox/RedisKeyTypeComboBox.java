package cn.oyzh.easyredis.fx.combobox;


import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2023/8/11
 */
public class RedisKeyTypeComboBox extends FlexComboBox<String> {

    {
        this.getItems().add("STRING(字符串)");
        this.getItems().add("LIST(列表)");
        this.getItems().add("SET(集合)");
        this.getItems().add("ZSET(有序集合)");
        this.getItems().add("HASH(哈希)");
        this.getItems().add("HYPERLOGLOG(统计日志)");
        this.getItems().add("GEO(地理坐标)");
        this.getItems().add("STREAM(消息队列)");
        this.getItems().add("BITMAP(位图)");
    }
}
