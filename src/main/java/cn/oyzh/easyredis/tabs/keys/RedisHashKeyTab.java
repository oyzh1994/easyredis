package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisHashFieldAddController;
import cn.oyzh.easyredis.event.RedisHashFieldAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisHashValue;
import cn.oyzh.easyredis.trees.keys.RedisHashKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.util.List;
import java.util.stream.Collectors;

/**
 * redis hash键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisHashKeyTab extends RedisKeyTab<RedisHashKeyTreeItem> {

    public RedisHashKeyTab(RedisHashKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisHashKeyTab.fxml";
    }

    @Override
    public RedisHashKeyTabController controller() {
        return (RedisHashKeyTabController) super.controller();
    }

    /**
     * hash键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/21
     */
    public static class RedisHashKeyTabController extends RedisRowKeyTabController<RedisHashKeyTreeItem, RedisHashValue.RedisHashRow> {

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
        private RedisFormatComboBox format;

        /**
         * 字段操作
         */
        @FXML
        private FlexHBox fieldAction;

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
                this.nodeData.setEditable(true);
            }
        };

        /**
         * redis数据监听器
         */
        private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
            if (this.treeItem.unsavedValue() == null) {
                this.treeItem.data(this.treeItem.currentRow());
            }
            if (this.treeItem.unsavedValue() != null) {
                this.treeItem.data().setValue(newValue);
            }
            this.saveNodeData.enable();
        };

        /**
         * 字段值监听器
         */
        private final ChangeListener<String> fieldValListener = (observable, oldValue, newValue) -> {
            if (this.treeItem.unsavedValue() == null) {
                this.treeItem.data(this.treeItem.currentRow());
            }
            if (this.treeItem.unsavedValue() != null) {
                this.treeItem.data().setField(newValue);
            }
            this.saveNodeData.enable();
        };

        @Override
        public boolean init(RedisHashKeyTreeItem treeItem) {
            this.pageData = null;
            if (super.init(treeItem)) {
                // 格式监听
                this.format.selectedItemChanged(this.formatListener);
                // 值处理
                this.nodeData.addTextChangeListener(this.dataListener);
                this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
                this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
                // 字段处理
                this.hashField.addTextChangeListener(this.fieldValListener);
                this.hashField.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldUndo.setDisable(!t1));
                this.hashField.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.fieldRedo.setDisable(!t1));
                return true;
            }
            return false;
        }

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
                this.nodeData.clear();
                this.nodeData.disable();
            } else {
                this.hashField.setText(row.getField());
                this.hashField.forgetHistory();
                this.nodeData.enable();
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
            if (this.treeItem.isDataUnsaved()) {
                TaskManager.start(() -> {
                    this.treeItem.saveKeyValue();
                    this.listTable.refresh();
                    this.saveNodeData.disable();
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
            if (row != null) {
                this.nodeData.showData(row.getValue());
                this.nodeData.forgetHistory();
                this.saveNodeData.disable();
            }
        }

        @Override
        protected void showData(RichDataType dataType) {
            RedisHashValue.RedisHashRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(dataType, row.getValue());
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
            this.hashField.disableProperty().bind(this.nodeData.disabledProperty());
            this.hashField.editableProperty().bind(this.nodeData.editableProperty());
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
    }
}
