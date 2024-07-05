package cn.oyzh.easyredis.tabs.key.hash;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisHashFieldAddController;
import cn.oyzh.easyredis.event.RedisHashFieldAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.FlexFlowPane;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.rich.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.data.RichDataType;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private RedisFormatComboBox format;

    /**
     * 字段操作
     */
    @FXML
    private FlexFlowPane fieldAction;

    /**
     * redis数据监听器
     */
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

    /**
     * 格式监听器
     */
    private final ChangeListener<String> formatListener = (t1, t2, t3) -> {
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
        }
    };

    @Override
    public boolean init(RedisHashKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            // 格式监听
            this.format.selectedItemChanged(this.formatListener);
            this.treeItem.dataProperty().addListener((t1, t2, newValue) -> this.saveNodeData.setDisable(newValue == null));
            // 键数据处理
            this.nodeData.addTextChangeListener(this.dataListener);
            this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
            this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
            // 键字段
            this.hashField.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldUndo.setDisable(!t1));
            this.hashField.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldRedo.setDisable(!t1));
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
            this.nodeData.clear();
            this.nodeData.disable();
            this.fieldAction.disable();
        } else {
            this.hashField.setText(row.getField());
            this.hashField.forgetHistory();
            this.hashField.enable();
            this.nodeData.enable();
            this.fieldAction.enable();
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
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
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
        String builder = I18nHelper.keyName()+": " + this.treeItem.key() + System.lineSeparator() +
                I18nHelper.fieldName()+ ": " + this.treeItem.currentRow().getField() + System.lineSeparator() +
                I18nHelper.fieldValue()+  ": " + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "行信息");
    }

    /**
     * hash字段添加事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onHashFieldAdded(RedisHashFieldAddedEvent event) {
        if (this.treeItem == event.data()) {
            this.firstPage();
        }
    }

    @FXML
    @Override
    protected void saveKeyData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn(I18nHelper.fieldAlreadyExists());
            return;
        }
        if (this.treeItem.dataUnsaved()) {
            TaskManager.start(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.saveNodeData.disable();
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
    private void fleldUndo() {
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
        this.nodeData.showData(this.treeItem.rawValue());
        // 首次设置数据要清除历史
        this.nodeData.forgetHistory();
    }

    @Override
    protected void showData(RichDataType dataType) {
        this.nodeData.showData(dataType, this.treeItem.rawValue());
    }

    @Override
    protected void clearRaw() {
        this.nodeData.clear();
        this.nodeData.disable();
    }
}
