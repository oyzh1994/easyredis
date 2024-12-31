package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.dto.RedisSlowlogItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import redis.clients.jedis.resps.Slowlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * redis慢查日志tab内容组件
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class RedisSlowlogController {

    /**
     * redis客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisClient client;

    /**
     * 表格组件
     */
    @FXML
    private FlexTableView<RedisSlowlogItem> listTable;

    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.initSlowlog();
    }

    /**
     * 刷新
     */
    @FXML
    private void refresh() {
        this.initSlowlog();
    }

    /**
     * 初始化慢查日志
     */
    private void initSlowlog() {
        List<Slowlog> list = this.client.slowlogGet(1024);
        List<RedisSlowlogItem> items = new ArrayList<>();
        for (Slowlog slowlog : list) {
            RedisSlowlogItem item = RedisSlowlogItem.from(slowlog);
            items.add(item);
        }
        Collections.reverse(items);
        this.listTable.setItem(items);
    }
}
