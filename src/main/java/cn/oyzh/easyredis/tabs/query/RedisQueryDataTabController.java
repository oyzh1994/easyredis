package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.easyredis.query.RedisQueryParam;
import cn.oyzh.easyredis.query.RedisQueryResult;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.property.KeyValueProperty;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryDataTabController extends DynamicTabController {

    @FXML
    private FlexTableView<KeyValueProperty<Integer, Object>> dataTable;

    public void init(Collection<?> list) {
        List<KeyValueProperty<Integer, Object>> data = new ArrayList<>();
        int index = 1;
        for (Object o : list) {
            if (o instanceof byte[] bytes) {
                data.add(KeyValueProperty.of(index++, SafeEncoder.encode(bytes)));
            } else {
                data.add(KeyValueProperty.of(index++, o.toString()));
            }
        }
        this.dataTable.setItem(data);
    }
}