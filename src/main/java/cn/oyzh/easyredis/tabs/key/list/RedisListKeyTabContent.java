package cn.oyzh.easyredis.tabs.key.list;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisListRowAddController;
import cn.oyzh.easyredis.event.RedisListRowAddedEvent;
import cn.oyzh.easyredis.redis.row.RedisListRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * list键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisListKeyTabContent extends RedisRowKeyTabContent<RedisListKeyTreeItem, RedisListRow> {

    /**
     * 数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisListRow, Integer> index;

    /**
     * 行值列
     */
    @FXML
    private TableColumn<RedisListRow, String> value;

    /**
     * 数据监听器
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (t1, t2, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
    };

    @Override
    public boolean init(RedisListKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            this.treeItem.dataProperty().addListener((t1, t2, newValue) -> this.saveNodeData.setDisable(newValue == null));
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

    /**
     * 刷新行
     */
    @FXML
    private void reloadRow() {
        // 放弃保存
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm("放弃未保存的数据？")) {
            return;
        }
        try {
            // 刷新数据
            if (this.treeItem.reloadRow()) {
                this.initRow(this.treeItem.currentRow());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected List<RedisListRow> getRows() {
        List<RedisListRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisListRowAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @FXML
    @Override
    protected void copyRow() {
        String builder = "键名称：" + this.treeItem.key() + System.lineSeparator() + "成员：" + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "行信息");
    }

    /**
     * list行添加事件
     *
     * @param msg 消息
     */
    @Subscribe
    private void onListRowAdded(RedisListRowAddedEvent msg) {
        if (this.treeItem == msg.data()) {
            this.firstPage();
        }
    }
}
