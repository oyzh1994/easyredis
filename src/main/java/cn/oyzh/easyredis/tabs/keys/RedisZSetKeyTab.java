package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetMemberAddController;
import cn.oyzh.easyredis.event.RedisZSetMemberAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.redis.key.RedisZSetValue;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
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
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisZSetKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTab(RedisZSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisZSetKeyTab.fxml";
    }

    @Override
    public RedisZSetKeyTabController controller() {
        return (RedisZSetKeyTabController) super.controller();
    }

    /**
     * zset键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/30
     */
    public static class RedisZSetKeyTabController extends RedisRowKeyTabController<RedisZSetKeyTreeItem, RedisZSetValue.RedisZSetRow> {

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
         * 反转视图
         */
        @FXML
        private SVGGlyph reverseView;

        /**
         * 分数值
         */
        @FXML
        private DecimalTextField scoreVal;

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
         * 数据监听器
         */
        private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
            if (!Objects.equals(this.treeItem.rawData(), newValue)) {
                this.saveNodeData.enable();
                if (this.treeItem.unsavedValue() == null) {
                    this.treeItem.data(this.treeItem.currentRow());
                }
                this.treeItem.unsavedValue().setValue(newValue);
            }
        };

        /**
         * 分数监听器
         */
        private final ChangeListener<String> scoreValListener = (observable, oldValue, newValue) -> {
            Number value = this.scoreVal.getValue();
            RedisZSetValue.RedisZSetRow row = this.treeItem.rawValue();
            if (!Objects.equals(row.getLatitude(), value.doubleValue())) {
                this.saveNodeData.enable();
                if (this.treeItem.unsavedValue() == null) {
                    this.treeItem.data(this.treeItem.currentRow());
                }
                this.treeItem.unsavedValue().setScore(value.doubleValue());
            }
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
                this.nodeData.setEditable(true);
            }
        };

        @Override
        protected void initKey() {
            // 初始化表单
            this.initTable();
            // 显示首页
            this.firstPage();
            // 显示切换按钮
            this.reverseView.setVisible(this.isSupportCoordinate());
        }

        @Override
        protected List<RedisZSetValue.RedisZSetRow> getRows() {
            List<RedisZSetValue.RedisZSetRow> rows = this.treeItem.rows();
            String filterKW = this.filter.getText();
            if (StringUtil.isNotEmpty(filterKW)) {
                rows = rows.parallelStream()
                        .filter(r -> StringUtil.containsIgnoreCase(r.getValue(), filterKW) || StringUtil.containsIgnoreCase(String.valueOf(r.getScore()), filterKW))
                        .collect(Collectors.toList());
            }
            return rows;
        }

        @FXML
        @Override
        protected void addRow() {
            StageAdapter fxView = StageManager.parseStage(RedisZSetMemberAddController.class);
            fxView.setProp("treeItem", this.treeItem);
            fxView.display();
        }

        @Override
        protected void initRow(RedisZSetValue.RedisZSetRow row) {
            super.initRow(row);
            if (row == null) {
                this.nodeData.clear();
                this.nodeData.disable();
                this.scoreVal.clear();
            } else {
                this.scoreVal.setValue(row.getScore());
                this.nodeData.enable();
                this.treeItem.clearData();
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
                    // 保存监听
                    this.saveNodeData.disable();
                });
            }
        }

        @FXML
        @Override
        protected void copyRow() {
            if (this.treeItem.isSelectRow()) {
                String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                        I18nHelper.member() + " : " + this.treeItem.currentRow().getValue() + System.lineSeparator() +
                        I18nHelper.score() + " : " + this.treeItem.currentRow().getScore();
                ClipboardUtil.setStringAndTip(builder);
            }
        }

        /**
         * 是否支持地理坐标
         *
         * @return 结果
         */
        private boolean isSupportCoordinate() {
            return this.treeItem.isSupportCoordinate();
        }

        /**
         * 反转视图
         */
        @FXML
        private void reverseView() {
            this.treeItem.reverseView();
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
            RedisZSetValue.RedisZSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(row.getValue());
                this.nodeData.forgetHistory();
                // 保存监听
                this.saveNodeData.disable();
            }
        }

        @Override
        protected void showData(RichDataType dataType) {
            RedisZSetValue.RedisZSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(dataType, row.getValue());
            }
        }

        @FXML
        @Override
        protected void deleteRow() {
            if (this.treeItem.isSelectRow() && MessageBox.confirm(I18nHelper.deleteMember() + "?")) {
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
            // 切换视图
            this.reverseView.managedBindVisible();
            // 分数处理
            this.scoreVal.addTextChangeListener(this.scoreValListener);
            this.scoreVal.disableProperty().bind(this.nodeData.disabledProperty());
            this.scoreVal.editableProperty().bind(this.nodeData.editableProperty());
            // 键数据处理
            this.nodeData.addTextChangeListener(this.dataListener);
            this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
            this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        }

        /**
         * zset成员添加事件
         *
         * @param msg 消息
         */
        @EventSubscribe
        private void onZSetMemberAdded(RedisZSetMemberAddedEvent msg) {
            if (this.treeItem == msg.data()) {
                this.firstPage();
            }
        }
    }
}
