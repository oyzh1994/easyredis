package cn.oyzh.easyredis.tabs.key.stream;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisStreamMessageAddController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisStreamMessageAddedMsg;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.fx.plus.controls.textfield.ReadOnlyTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import com.google.common.eventbus.Subscribe;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * stream键tab内容组件
 *
 * @author oyzh
 * @since 2023/07/07
 */
public class RedisStreamKeyTabContent extends RedisRowKeyTabContent<RedisStreamKeyTreeItem, RedisStreamRow> {

    /**
     * 消息id
     */
    @FXML
    private ReadOnlyTextField streamID;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisStreamRow, Integer> index;

    /**
     * 分数列
     */
    @FXML
    private TableColumn<RedisStreamRow, String> id;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisStreamRow, String> value;


    @Override
    public boolean init(RedisStreamKeyTreeItem treeItem) {
        this.pageData = null;
        return super.init(treeItem);
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 绑定属性
        this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @Override
    protected List<RedisStreamRow> getRows() {
        List<RedisStreamRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
                            StrUtil.containsIgnoreCase(String.valueOf(r.getId()), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisStreamMessageAddController.class);
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected void initRow(RedisStreamRow row) {
       super.initRow(row);
        if (row == null) {
            this.streamID.clear();
            this.streamID.disable();
        } else {
            this.streamID.setText(row.getId());
            this.streamID.enable();
        }
    }

    @FXML
    @Override
    protected void copyRow() {
        String builder = "键名称：" + this.treeItem.key() + System.lineSeparator() +
                "消息ID：" + this.treeItem.currentRow().getId() + System.lineSeparator() +
                "消息内容：" + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "消息");
    }

    /**
     * stream消息添加事件
     *
     * @param msg 消息
     */
    @Subscribe
    private void onStreamMessageAdded(RedisStreamMessageAddedMsg msg) {
        if (this.treeItem == msg.data()) {
            this.firstPage();
        }
    }
}
