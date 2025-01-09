package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.dto.RedisClientItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * redis客户端信息tab内容组件
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class RedisClientInfoController extends SubTabController {

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
    private FlexTableView<RedisClientItem> listTable;


    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.initClientList();
    }

    /**
     * 刷新
     */
    @FXML
    private void refresh() {
        this.initClientList();
    }

    /**
     * 初始化客户端信息
     */
    private void initClientList() {
        String list = this.client.clientList();
        AtomicInteger index = new AtomicInteger(1);
        List<RedisClientItem> items = new ArrayList<>();
        list.lines().forEach(l -> {
            RedisClientItem item = RedisClientItem.from(l);
            item.setIndex(index.getAndIncrement());
            items.add(item);
        });
        this.listTable.getItems().setAll(items);
    }
}
