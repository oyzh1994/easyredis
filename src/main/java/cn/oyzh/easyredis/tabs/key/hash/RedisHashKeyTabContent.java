package cn.oyzh.easyredis.tabs.key.hash;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisHashFieldAddController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisHashFieldAddedMsg;
import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
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
 * hash键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisHashKeyTabContent extends RedisRowKeyTabContent<RedisHashKeyTreeItem, RedisHashRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 行号列
     */
    @FXML
    private TableColumn<RedisHashRow, Integer> index;

    /**
     * 字段列
     */
    @FXML
    private TableColumn<RedisHashRow, String> field;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisHashRow, String> value;

    /**
     * 字段名
     */
    @FXML
    private FlexTextArea hashField;

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

    /**
     * 字段值监听器
     */
    private final ChangeListener<String> fieldValListener = (observable, oldValue, newValue) -> {
        String value = this.hashField.getText();
        if (this.treeItem.currentRow() == null || Objects.equals(value, this.treeItem.currentRow().getField())) {
            this.treeItem.field(null);
        } else {
            this.treeItem.field(value);
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    @Override
    public boolean init(RedisHashKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            this.treeItem.dataProperty().addListener((_, _, newValue) -> this.saveNodeData.setDisable(newValue == null));
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
        this.field.setCellValueFactory(new PropertyValueFactory<>("field"));
        this.hashField.addTextChangeListener(this.fieldValListener);
    }

    @Override
    protected void initRow(RedisHashRow row) {
        super.initRow(row);
        if (row == null) {
            this.hashField.clear();
            this.hashField.disable();
        } else {
            this.hashField.setText(row.getField());
            this.hashField.enable();
        }
    }

    @Override
    protected List<RedisHashRow> getRows() {
        List<RedisHashRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getField(), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getValue()), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisHashFieldAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
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

    @FXML
    @Override
    protected void copyRow() {
        String builder = "键名称：" + this.treeItem.key() + System.lineSeparator() +
                "字段：" + this.treeItem.currentRow().getField() + System.lineSeparator() +
                "数据：" + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "行信息");
    }

    /**
     * hash字段添加事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_HASH_FIELD_ADDED, verbose = true, async = true)
    private void onHashFieldAdded(RedisHashFieldAddedMsg msg) {
        if (this.treeItem == msg.item()) {
            this.firstPage();
        }
    }

    @FXML
    @Override
    protected void saveNodeData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn("此字段已存在！");
        } else if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.saveNodeData.disable();
                }
            });
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
