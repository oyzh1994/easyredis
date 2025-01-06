package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisHashFieldAddController;
import cn.oyzh.easyredis.event.key.RedisHashFieldAddedEvent;
import cn.oyzh.easyredis.fx.svg.pane.ExpandListSVGPane;
import cn.oyzh.easyredis.redis.key.RedisHashValue;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.trees.key.RedisHashKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * hash键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisHashKeyController extends RedisRowKeyController<RedisHashKeyTreeItem, RedisHashValue.RedisHashRow> {

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
     * 数据撤销
     */
    @FXML
    private SVGGlyph fieldUndo;

    /**
     * 数据重做
     */
    @FXML
    private SVGGlyph fieldRedo;

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 字段名
     */
    @FXML
    private RichDataTextAreaPane hashField;

    /**
     * 数据组件
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    /**
     * 格式
     */
    @FXML
    private RichDataTypeComboBox format;

    /**
     * 字段格式
     */
    @FXML
    private RichDataTypeComboBox fieldFormat;

    /**
     * 字段操作
     */
    @FXML
    private FlexHBox fieldAction;

    /**
     * 展开列表面板
     */
    @FXML
    private ExpandListSVGPane expandPane;

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
     * 字段格式监听器
     */
    private final ChangeListener<RichDataType> fieldFormatListener = (t1, t2, t3) -> {
        if (this.fieldFormat.isStringFormat()) {
            this.hashField.showData(RichDataType.STRING);
            this.hashField.setEditable(true);
        } else if (this.fieldFormat.isJsonFormat()) {
            this.hashField.showData(RichDataType.JSON);
            this.hashField.setEditable(true);
        } else if (this.fieldFormat.isBinaryFormat()) {
            this.hashField.showData(RichDataType.BINARY);
            this.hashField.setEditable(false);
        } else if (this.fieldFormat.isHexFormat()) {
            this.hashField.showData(RichDataType.HEX);
            this.hashField.setEditable(false);
        } else if (this.fieldFormat.isRawFormat()) {
            this.hashField.showData(RichDataType.RAW);
            this.hashField.setEditable(true);
        }
    };

    /**
     * 忽略数据变化
     */
    private boolean ignoreDataChange = false;

    /**
     * redis数据监听器
     */
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreDataChange && !Objects.equals(this.treeItem.rawData(), newValue)) {
            this.saveNodeData.enable();
            if (this.treeItem.unsavedValue() == null) {
                this.treeItem.data(this.treeItem.currentRow());
            }
            if (this.treeItem.unsavedValue() != null) {
                this.treeItem.unsavedValue().setValue(newValue);
            }
        }
    };

    /**
     * 字段值监听器
     */
    private final ChangeListener<String> fieldValListener = (observable, oldValue, newValue) -> {
        RedisHashValue.RedisHashRow row = this.treeItem.rawValue();
        if (!this.ignoreDataChange && row != null && !Objects.equals(row.getField(), newValue)) {
            this.saveNodeData.enable();
            if (this.treeItem.unsavedValue() == null) {
                this.treeItem.data(this.treeItem.currentRow());
            }
            if (this.treeItem.unsavedValue() != null) {
                this.treeItem.unsavedValue().setField(newValue);
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

    @Override
    protected void initRow(RedisHashValue.RedisHashRow row) {
        super.initRow(row);
        if (row == null) {
            this.hashField.clear();
//            this.nodeData.clear();
//            this.nodeData.disable();
        } else {
            this.hashField.setText(row.getField());
            this.hashField.forgetHistory();
//            this.nodeData.enable();
        }
    }

    @Override
    protected List<RedisHashValue.RedisHashRow> getRows() {
        List<RedisHashValue.RedisHashRow> rows = this.treeItem.rows();
        String filterKW = this.filter.getText();
        if (StringUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StringUtil.containsIgnoreCase(r.getField(), filterKW) || StringUtil.containsIgnoreCase(String.valueOf(r.getValue()), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageAdapter fxView = StageManager.parseStage(RedisHashFieldAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
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

    @FXML
    @Override
    protected void copyRow() {
        if (this.treeItem.isSelectRow()) {
            String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                    I18nHelper.fieldName() + " : " + this.treeItem.currentRow().getField() + System.lineSeparator() +
                    I18nHelper.fieldValue() + " : " + this.treeItem.currentRow().getValue();
            ClipboardUtil.setStringAndTip(builder);
        }
    }

    @FXML
    @Override
    protected void saveKeyValue() {
        if (this.treeItem.checkRowExists()) {
            MessageBox.warn(I18nHelper.fieldAlreadyExists());
            return;
        }
        if (this.treeItem.isDataTooBig()) {
            MessageBox.warn(I18nHelper.dataTooLarge());
            return;
        }
        if (this.treeItem.isDataUnsaved()) {
            this.disableTab();
            TaskManager.start(() -> {
                try {
                    this.treeItem.saveKeyValue();
                    this.listTable.refresh();
                    this.saveNodeData.disable();
                    this.treeItem.flushMemoryUsage();
                } finally {
                    this.enableTab();
                }
            });
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

    /**
     * 数据撤销
     */
    @FXML
    private void fieldUndo() {
        this.hashField.undo();
        this.hashField.requestFocus();
    }

    /**
     * 数据重做
     */
    @FXML
    private void fieldRedo() {
        this.hashField.redo();
        this.hashField.requestFocus();
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteField() {
        this.hashField.paste();
        this.hashField.requestFocus();
    }

    /**
     * 清除数据
     */
    @FXML
    private void clearFiled() {
        this.hashField.clear();
        this.hashField.requestFocus();
    }

    @Override
    protected void firstShowData() {
        RedisHashValue.RedisHashRow row = this.treeItem.data();
        if (row == null) {
            return;
        }
        // 数据太大
        if (this.treeItem.isDataTooBig()) {
            // 状态处理
            this.nodeData.clear();
            this.nodeData.disable();
            this.ignoreDataChange = true;
            NodeGroupUtil.disable(this.getTab(), "dataToBig");
            MessageBox.warn(I18nHelper.dataTooLarge());
            return;
        }
        // 状态处理
        this.nodeData.enable();
        this.ignoreDataChange = false;
        NodeGroupUtil.enable(this.getTab(), "dataToBig");
        // 数据处理
        RichDataType dataType = this.nodeData.showDetectData(row.getValue());
        this.format.setValue(dataType);
        this.nodeData.forgetHistory();
        this.saveNodeData.disable();

        // 字段格式
        RichDataType fieldDataType = this.hashField.showDetectData(row.getField());
        this.fieldFormat.setValue(fieldDataType);
    }

    @Override
    protected void showData(RichDataType dataType) {
        RedisHashValue.RedisHashRow row = this.treeItem.data();
        if (row != null) {
            this.nodeData.showData(dataType, row.getValue());
        }
    }

    @FXML
    @Override
    protected void deleteRow() {
        if (this.treeItem.isSelectRow() && MessageBox.confirm(I18nHelper.deleteField() + "?")) {
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
        this.fieldFormat.selectedItemChanged(this.fieldFormatListener);
        // 值处理
        this.nodeData.addTextChangeListener(this.dataListener);
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        // 字段处理
        this.hashField.addTextChangeListener(this.fieldValListener);
        this.hashField.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldUndo.setDisable(!t1));
        this.hashField.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldRedo.setDisable(!t1));
        this.hashField.disableProperty().bind(this.nodeData.disabledProperty());
        this.fieldAction.disableProperty().bind(this.nodeData.disabledProperty());
    }

    /**
     * hash字段添加事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onHashFieldAdded(RedisHashFieldAddedEvent event) {
        if (this.treeItem == event.data()) {
            this.firstPage();
        }
    }

    @FXML
    private void expendList() {
        if (this.expandPane.isCollapse()) {
            NodeGroupUtil.disappear(this.getTab(), "hash_list");
            this.hashField.realHeight(150);
            this.nodeData.setFlexHeight("100% - 292");
            this.expandPane.expand();
        } else {
            NodeGroupUtil.display(this.getTab(), "hash_list");
            this.hashField.realHeight(100);
            this.nodeData.setFlexHeight("100% - 567");
            this.expandPane.collapse();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        this.dataAction.disableProperty().bind(this.nodeData.disableProperty());
    }
}
