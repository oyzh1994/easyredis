package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * redis订阅发布tab内容组件
 *
 * @author oyzh
 * @since 2023/08/02
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
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
     * 编号
     */
    @FXML
    private FlexTableColumn<RedisPubsubItem, String> index;

    /**
     * 通道
     */
    @FXML
    private FlexTableColumn<RedisPubsubItem, String> channel;

    /**
     * 操作
     */
    @FXML
    private FlexTableColumn<RedisPubsubItem, String> action;

    /**
     * 执行初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.channel.setCellValueFactory(new PropertyValueFactory<>("channel"));
        // 操作栏初始化
        this.action.setCellFactory((cell) -> new FXTableCell<>() {
            private HBox hBox;

            @Override
            public Node initGraphic() {
                if (this.hBox == null) {
                    Button subscribe = new Button("控制台订阅");
                    subscribe.setCursor(Cursor.HAND);
                    subscribe.setOnAction((event) -> subscribe(this.getTableItem()));
                    this.hBox = new HBox(subscribe);
                    this.hBox.setSpacing(5);
                }
                return hBox;
            }
        });
        this.initPubsub();
    }

    /**
     * 执行订阅
     *
     * @param pubsubItem 订阅发布键
     */
    private void subscribe(RedisPubsubItem pubsubItem) {
        pubsubItem.setClient(this.client);
        EventUtil.fire(RedisEventTypes.REDIS_OPEN_PUBSUB, pubsubItem);
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
