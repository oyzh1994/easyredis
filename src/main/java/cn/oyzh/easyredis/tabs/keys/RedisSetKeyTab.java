package cn.oyzh.easyredis.tabs.keys;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyredis.controller.row.RedisSetMemberAddController;
import cn.oyzh.easyredis.event.RedisSetMemberAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
        return  "/tabs/keys/redisSetKeyTab.fxml";
    }

    @Override
    public RedisSetKeyTabController controller() {
        return (RedisSetKeyTabController) super.controller();
    }

    @Override
    public RedisSetKey key() {
        return (RedisSetKey) super.key();
    }

    /**
     * set键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/21
     */
    public static class RedisSetKeyTabController extends RedisRowKeyTabController<RedisSetKeyTreeItem, RedisSetRow> {

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

        // /**
        //  * 编号列
        //  */
        // @FXML
        // private TableColumn<RedisSetRow, Integer> index;
        //
        // /**
        //  * 值列
        //  */
        // @FXML
        // private TableColumn<RedisSetRow, String> value;

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
        public boolean init(RedisSetKeyTreeItem treeItem) {
            this.pageData = null;
            if (super.init(treeItem)) {
                // 格式监听
                this.format.selectedItemChanged(this.formatListener);
                this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
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
            // // 绑定属性
            // this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
            // this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
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
            StageAdapter fxView = StageManager.parseStage(RedisSetMemberAddController.class, this.treeItem.window());
            fxView.setProp("treeItem", this.treeItem);
            fxView.display();
        }

        @Override
        protected void initRow(RedisSetRow row) {
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
        protected void saveKeyData() {
            if (this.treeItem.checkExists()) {
                MessageBox.warn(I18nHelper.dataAlreadyExists());
                return;
            }
            if (this.treeItem.dataUnsaved()) {
                TaskManager.start(() -> this.treeItem.saveNodeValue());
            }
        }

        @FXML
        @Override
        protected void copyRow() {
            String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                    I18nHelper.member() + ": " + this.treeItem.currentRow().getValue();
            ClipboardUtil.setStringAndTip(builder, "成员信息");
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
