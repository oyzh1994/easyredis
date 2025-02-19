package cn.oyzh.easyredis.fx;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * redis连接选择框
 *
 * @author oyzh
 * @since 2023/07/20
 */
public class RedisConnectComboBox extends FXComboBox<RedisConnect> {

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
        this.getItems().setAll(RedisConnectStore.INSTANCE.load());
    }
}
