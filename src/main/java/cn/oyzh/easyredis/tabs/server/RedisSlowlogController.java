package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.info.RedisSlowlogItem;
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
     * id
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> id;

    /**
     * 命令
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> command;

    /**
     * 发生时间
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> timeStamp;

    /**
     * 客户端慢查
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> clientName;

    /**
     * 客户端地址
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> clientHost;

    /**
     * 耗时
     */
    @FXML
    private FlexTableColumn<RedisSlowlogItem, String> executionTime;

    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.command.setCellValueFactory(new PropertyValueFactory<>("command"));
        this.timeStamp.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        this.clientHost.setCellValueFactory(new PropertyValueFactory<>("clientHost"));
        this.clientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        this.executionTime.setCellValueFactory(new PropertyValueFactory<>("executionTime"));
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
        this.listTable.getItems().setAll(items);
    }
}
