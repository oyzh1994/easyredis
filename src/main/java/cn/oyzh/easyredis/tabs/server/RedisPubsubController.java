package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.dto.RedisPubsubItem;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * redis订阅发布tab内容组件
 *
 * @author oyzh
 * @since 2023/08/02
 */
public class RedisPubsubController {

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
    private FlexTableView<RedisPubsubItem> listTable;

    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.initPubsub();
    }

    /**
     * 执行订阅
     */
    @FXML
    private void subscribe() {
        RedisPubsubItem pubsubItem = this.listTable.getSelectedItem();
        if (pubsubItem != null) {
            pubsubItem.setClient(this.client);
            RedisEventUtil.pubsubOpen(pubsubItem);
        }
    }

    /**
     * 刷新
     */
    @FXML
    private void refresh() {
        this.initPubsub();
    }

    /**
     * 初始化订阅发布
     */
    private void initPubsub() {
        List<String> list = this.client.pubsubChannels("*");
        List<RedisPubsubItem> items = new ArrayList<>();
        int index = 1;
        for (String l : list) {
            RedisPubsubItem item = new RedisPubsubItem();
            item.setIndex(index++);
            item.setChannel(l);
            items.add(item);
        }
        this.listTable.getItems().setAll(items);
    }
}
