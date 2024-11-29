package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.info.RedisClientItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class RedisClientInfoContent {

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
     * 编号
     */
    @FXML
    private FlexTableColumn<RedisClientItem, Integer> index;

    /**
     * 地址
     */
    @FXML
    private FlexTableColumn<RedisClientItem, String> addr;

    /**
     * 存活
     */
    @FXML
    private FlexTableColumn<RedisClientItem, String> age;

    /**
     * 空闲
     */
    @FXML
    private FlexTableColumn<RedisClientItem, String> idle;

    /**
     * 标记
     */
    @FXML
    private FlexTableColumn<RedisClientItem, String> flags;

    /**
     * 当前库
     */
    @FXML
    private FlexTableColumn<RedisClientItem, String> db;

    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.db.setCellValueFactory(new PropertyValueFactory<>("db"));
        this.age.setCellValueFactory(new PropertyValueFactory<>("age"));
        this.addr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        this.idle.setCellValueFactory(new PropertyValueFactory<>("idle"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.flags.setCellValueFactory(new PropertyValueFactory<>("flags"));
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
        list.lines().forEach(l->{
            RedisClientItem item = RedisClientItem.from(l);
            item.setIndex(index.getAndIncrement());
            items.add(item);
        });
        this.listTable.getItems().setAll(items);
    }
}
