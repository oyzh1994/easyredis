package cn.oyzh.easyredis.tabs.key.list;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisListRowAddController;
import cn.oyzh.easyredis.event.RedisListRowAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.row.RedisListRow;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabController;
import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis list键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisListKeyTab extends RedisKeyTab<RedisListKeyTreeItem> {

    public RedisListKeyTab(RedisListKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisListKeyTabContent.fxml";
    }

    @Override
    public RedisListKeyTabController controller() {
        return (RedisListKeyTabController) super.controller();
    }

    @Override
    public RedisListKey key() {
        return (RedisListKey) super.key();
    }

    /**
     * list键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/21
     */
    public static class RedisListKeyTabController extends RedisRowKeyTabController<RedisListKeyTreeItem, RedisListRow> {

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
         * 数据监听器
         */
        private final ChangeListener<String> dataListener = (t1, t2, newValue) -> {
            if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
                this.treeItem.clearData();
            } else {
                this.treeItem.data(newValue);
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
            }
        };

        @Override
        public boolean init(RedisListKeyTreeItem treeItem) {
            this.pageData = null;
            if (super.init(treeItem)) {
                // 格式监听
                this.format.selectedItemChanged(this.formatListener);
                this.treeItem.dataProperty().addListener((t1, t2, newValue) -> this.saveNodeData.setDisable(newValue == null));
                // 键数据处理
                this.nodeData.addTextChangeListener(this.dataListener);
                this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
                this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
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
            StageAdapter fxView = StageManager.parseStage(RedisListRowAddController.class, this.treeItem.window());
            fxView.setProp("treeItem", this.treeItem);
            fxView.display();
        }

        @Override
        protected void initRow(RedisListRow row) {
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
        protected void copyRow() {
            String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                    I18nHelper.member() + ": " + this.treeItem.currentRow().getValue();
            ClipboardUtil.setStringAndTip(builder);
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
}
