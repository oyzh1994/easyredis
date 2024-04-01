package cn.oyzh.easyredis.tabs.key.set;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisSetMemberAddController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisSetMemberAddedMsg;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * set键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisSetKeyTabContent extends RedisRowKeyTabContent<RedisSetKeyTreeItem, RedisSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisSetRow, Integer> index;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisSetRow, String> value;

    /**
     * redis数据监听器
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
    };

    @Override
    public boolean init(RedisSetKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
            return true;
        }
        return false;
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 绑定属性
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @Override
    protected List<RedisSetRow> getRows() {
        List<RedisSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    /**
     * 添加行
     */
    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisSetMemberAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @FXML
    @Override
    protected void saveNodeData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn("此成员已存在！");
        } else if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(this.treeItem::saveNodeValue);
        }
    }

    @FXML
    @Override
    protected void copyRow() {
        String builder = "键名称：" + this.treeItem.key() + System.lineSeparator() +
                "成员：" + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "成员信息");
    }

    /**
     * set成员添加事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_SET_MEMBER_ADDED, verbose = true, async = true)
    @Subscribe
    private void onSetMemberAdded(RedisSetMemberAddedMsg msg) {
        if (this.treeItem == msg.data()) {
            this.firstPage();
        }
    }

    @Override
    public void onTabInit() {
        EventUtil.register(this);
    }

    @Override
    public void onTabClose(Event event) {
        EventUtil.unregister(this);
    }
}
