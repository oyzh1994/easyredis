package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.property.KeyValueProperty;
import javafx.fxml.FXML;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author oyzh
 * @since 2025/02/07
 */
public class RedisQueryDataTabController extends DynamicTabController {

    @FXML
    private FlexTableView<KeyValueProperty<Integer, Object>> dataTable;

    public void init(Collection<?> list) {
        List<KeyValueProperty<Integer, Object>> data = new ArrayList<>();
        int index = 1;
        for (Object o : list) {
            this.parseObject(o, index++, data);
        }
        this.dataTable.setItem(data);
    }

    public void init(Object o) {
        List<KeyValueProperty<Integer, Object>> data = new ArrayList<>();
        this.parseObject(o, 1, data);
        this.dataTable.setItem(data);
    }

    private void parseObject(Object o, int index, List<KeyValueProperty<Integer, Object>> data) {
        switch (o) {
            case byte[] bytes -> data.add(KeyValueProperty.of(index, SafeEncoder.encode(bytes)));
            case Collection<?> _ -> data.add(KeyValueProperty.of(index, SafeEncoder.encodeObject(o)));
            case KeyValue<?, ?> _ -> data.add(KeyValueProperty.of(index, SafeEncoder.encodeObject(o)));
            case null -> data.add(KeyValueProperty.of(index, ""));
            default -> data.add(KeyValueProperty.of(index, o.toString()));
        }
    }
}