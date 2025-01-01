package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisListRowAddController;
import cn.oyzh.easyredis.event.key.RedisListRowAddedEvent;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.redis.key.RedisListValue;
import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * list键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisListKeyTabController extends RedisRowKeyTabController<RedisListKeyTreeItem, RedisListValue.RedisListRow> {

    /**
     * 数据撤销
     */
    @FXML
    private SVGGlyph dataUndo;

    /**
     * 数据重做
     */
    @FXML
    private SVGGlyph dataRedo;

    /**
     * 数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 格式
     */
    @FXML
    private RichDataTypeComboBox format;

    /**
     * 数据组件
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    /**
     * 格式监听器
     */
    private final ChangeListener<RichDataType> formatListener = (t1, t2, t3) -> {
        if (this.format.isStringFormat()) {
            this.showData(RichDataType.STRING);
            this.nodeData.setEditable(true);
        } else if (this.format.isJsonFormat()) {
            this.showData(RichDataType.JSON);
            this.nodeData.setEditable(true);
        } else if (this.format.isBinaryFormat()) {
            this.showData(RichDataType.BINARY);
            this.nodeData.setEditable(false);
        } else if (this.format.isHexFormat()) {
            this.showData(RichDataType.HEX);
            this.nodeData.setEditable(false);
        } else if (this.format.isRawFormat()) {
            this.showData(RichDataType.RAW);
            this.nodeData.setEditable(true);
        }
    };

    /**
     * 数据监听器
     */
    private final ChangeListener<String> dataListener = (t1, t2, newValue) -> {
        if (!Objects.equals(this.treeItem.rawData(), newValue)) {
            this.saveNodeData.enable();
            if (this.treeItem.unsavedValue() == null) {
                this.treeItem.data(this.treeItem.currentRow());
            }
            if (this.treeItem.unsavedValue() != null) {
                this.treeItem.unsavedValue().setValue(newValue);
            }
        }
    };

    @Override
    protected void initKey() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
    }

    /**
     * 刷新行
     */
    @FXML
    private void reloadRow() {
        // 放弃保存
        if (this.treeItem.isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
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
    protected List<RedisListValue.RedisListRow> getRows() {
        List<RedisListValue.RedisListRow> rows = this.treeItem.rows();
        String filterKW = this.filter.getText();
        if (StringUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StringUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageAdapter fxView = StageManager.parseStage(RedisListRowAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected void initRow(RedisListValue.RedisListRow row) {
        super.initRow(row);
        if (row == null) {
            this.nodeData.clear();
            this.nodeData.disable();
        } else {
            this.nodeData.enable();
        }
    }

    @FXML
    @Override
    protected void saveKeyValue() {
        if (this.treeItem.isDataUnsaved()) {
            TaskManager.start(() -> {
                this.treeItem.saveKeyValue();
                this.listTable.refresh();
                this.saveNodeData.disable();
            });
        }
    }

    @FXML
    @Override
    protected void copyRow() {
        if (this.treeItem.isSelectRow()) {
            String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                    I18nHelper.element() + " : " + this.treeItem.currentRow().getValue();
            ClipboardUtil.setStringAndTip(builder);
        }
    }

    /**
     * 数据撤销
     */
    @FXML
    private void dataUndo() {
        this.nodeData.undo();
        this.nodeData.requestFocus();
    }

    /**
     * 数据重做
     */
    @FXML
    private void dataRedo() {
        this.nodeData.redo();
        this.nodeData.requestFocus();
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.nodeData.paste();
        this.nodeData.requestFocus();
    }

    /**
     * 清除数据
     */
    @FXML
    private void clearData() {
        this.nodeData.clear();
        this.nodeData.requestFocus();
    }

    @Override
    protected void firstShowData() {
        RedisListValue.RedisListRow row = this.treeItem.data();
        if (row != null) {
            RichDataType dataType = this.nodeData.showDetectData(row.getValue());
            this.format.setValue(dataType);
            this.nodeData.forgetHistory();
            this.saveNodeData.disable();
        }
    }

    @Override
    protected void showData(RichDataType dataType) {
        RedisListValue.RedisListRow row = this.treeItem.data();
        if (row != null) {
            this.nodeData.showData(dataType, row.getValue());
        }
    }

    @FXML
    @Override
    protected void deleteRow() {
        if (this.treeItem.isSelectRow() && MessageBox.confirm(I18nHelper.deleteElement() + "?")) {
            RedisKeyRow row = this.treeItem.currentRow();
            if (this.treeItem.deleteRow()) {
                // 移除
                if (this.listTable.getItemSize() > 1) {
                    this.listTable.removeItem(row);
                } else {// 刷新
                    this.firstPage();
                }
            }
        }
    }

    @Override
    protected void clearRow() {
        this.nodeData.clear();
        this.nodeData.disable();
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 格式监听
        this.format.selectedItemChanged(this.formatListener);
        // 键数据处理
        this.nodeData.addTextChangeListener(this.dataListener);
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
    }

    /**
     * list行添加事件
     *
     * @param msg 消息
     */
    @EventSubscribe
    private void onListRowAdded(RedisListRowAddedEvent msg) {
        if (this.treeItem == msg.data()) {
            this.firstPage();
        }
    }
}
