package cn.oyzh.easyredis.fx;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * redis连接选择框
 *
 * @author oyzh
 * @since 2023/07/20
 */
public class RedisConnectComboBox extends FlexComboBox<RedisConnect> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(RedisConnect o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        this.getItems().setAll(RedisInfoStore.INSTANCE.load());
    }
}
