package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisSetMemberAddController;
import cn.oyzh.easyredis.event.RedisSetMemberAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.redis.key.RedisSetValue;
import cn.oyzh.easyredis.trees.keys.RedisSetKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis set键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTab extends RedisKeyTab<RedisSetKeyTreeItem> {

    public RedisSetKeyTab(RedisSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisSetKeyTab.fxml";
    }

    @Override
    public RedisSetKeyTabController controller() {
        return (RedisSetKeyTabController) super.controller();
    }

    /**
     * set键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/21
     */
    public static class RedisSetKeyTabController extends RedisRowKeyTabController<RedisSetKeyTreeItem, RedisSetValue.RedisSetRow> {

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
         * redis数据保存按钮
         */
        @FXML
        private SVGGlyph saveNodeData;

        /**
         * 格式
         */
        @FXML
        private RedisFormatComboBox format;

        /**
         * 数据组件
         */
        @FXML
        private RichDataTextAreaPane nodeData;

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

        @Override
        protected List<RedisSetValue.RedisSetRow> getRows() {
            List<RedisSetValue.RedisSetRow> rows = this.treeItem.rows();
            String filterKW = this.filter.getText();
            if (StringUtil.isNotEmpty(filterKW)) {
                rows = rows.parallelStream()
                        .filter(r -> StringUtil.containsIgnoreCase(r.getValue(), filterKW))
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
            StageAdapter fxView = StageManager.parseStage(RedisSetMemberAddController.class, this.treeItem.window());
            fxView.setProp("treeItem", this.treeItem);
            fxView.display();
        }

        @Override
        protected void initRow(RedisSetValue.RedisSetRow row) {
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
            if (this.treeItem.checkRowExists()) {
                MessageBox.warn(I18nHelper.dataAlreadyExists());
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

        @FXML
        @Override
        protected void copyRow() {
            if (this.treeItem.isSelectRow()) {
                String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                        I18nHelper.member() + " : " + this.treeItem.currentRow().getValue();
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
            RedisSetValue.RedisSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(row.getValue());
                this.nodeData.forgetHistory();
                this.saveNodeData.disable();
            }
        }

        @Override
        protected void showData(RichDataType dataType) {
            RedisSetValue.RedisSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(dataType, row.getValue());
            }
        }

        @FXML
        @Override
        protected void deleteRow() {
            if (this.treeItem.isSelectRow() && MessageBox.confirm(I18nHelper.deleteMessage() + "?")) {
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
         * set成员添加事件
         *
         * @param msg 消息
         */
        @EventSubscribe
        private void onSetMemberAdded(RedisSetMemberAddedEvent msg) {
            if (this.treeItem == msg.data()) {
                this.firstPage();
            }
        }
    }
}
