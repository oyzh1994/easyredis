package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetMemberAddController;
import cn.oyzh.easyredis.event.RedisZSetMemberAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisZSetValue;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
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

    // @Override
    // public RedisZSetKey key() {
    //     return (RedisZSetKey) super.key();
    // }

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

        // /**
        //  * 编号列
        //  */
        // @FXML
        // private TableColumn<RedisZSetRow, Integer> index;
        //
        // /**
        //  * 分数列
        //  */
        // @FXML
        // private TableColumn<RedisZSetRow, Double> score;
        //
        // /**
        //  * 值列
        //  */
        // @FXML
        // private FlexTableColumn<RedisZSetRow, String> value;

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
            if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
                this.treeItem.data(null);
            } else {
                this.treeItem.data(newValue);
            }
            this.saveNodeData.setDisable(!this.treeItem.isDataUnsaved());
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

        /**
         * 分数监听器
         */
        private final ChangeListener<String> scoreValListener = (observable, oldValue, newValue) -> {
            Number scoreVal = this.scoreVal.getValue();
            if (this.treeItem.currentRow() == null || Objects.equals(scoreVal.doubleValue(), this.treeItem.currentRow().getScore())) {
                this.treeItem.score(null);
            } else {
                this.treeItem.score(scoreVal.doubleValue());
            }
            this.saveNodeData.setDisable(!this.treeItem.isDataUnsaved());
        };

        @Override
        public boolean init(RedisZSetKeyTreeItem treeItem) {
            if (super.init(treeItem)) {
                this.pageData = null;
                // 格式监听
                this.format.selectedItemChanged(this.formatListener);
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
            // 显示切换按钮
            this.reverseView.setVisible(this.isSupportGEO());
            // // 绑定属性
            // this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
            // this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
            // this.score.setCellValueFactory(new PropertyValueFactory<>("score"));
            // this.value.setText(I18nHelper.member());
            this.scoreVal.addTextChangeListener(this.scoreValListener);
        }

        @Override
        protected List<RedisZSetValue.RedisZSetRow> getRows() {
            List<RedisZSetValue.RedisZSetRow> rows = this.treeItem.nodeValue();
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
                this.scoreVal.disable();
            } else {
                this.scoreVal.setValue(row.getScore());
                this.scoreVal.enable();
                this.nodeData.enable();
                this.saveNodeData.disable();
                this.treeItem.clearData();
            }
        }

        @FXML
        @Override
        protected void saveKeyData() {
            if (this.treeItem.checkRowExists()) {
                MessageBox.warn(I18nHelper.dataAlreadyExists());
                return;
            }
            if (this.treeItem.isDataUnsaved()) {
                TaskManager.start(() -> {
                    if (this.treeItem.saveKeyValue()) {
                        this.saveNodeData.disable();
                    }
                });
            }
        }

        @FXML
        @Override
        protected void copyRow() {
            StringBuilder builder = new StringBuilder();
            builder.append(I18nHelper.keyName()).append(": ").append(this.treeItem.key()).append(System.lineSeparator());
            builder.append(I18nHelper.member()).append(": ").append(this.treeItem.currentRow().getValue()).append(System.lineSeparator())
                    .append(I18nHelper.score()).append(": ").append(this.treeItem.currentRow().getScore());
            ClipboardUtil.setStringAndTip(builder.toString());
        }

        /**
         * 是否支持地理坐标
         *
         * @return 结果
         */
        private boolean isSupportGEO() {
            return this.treeItem.isSupportGEO();
        }

        @FXML
        private void reverseView() {
            this.treeItem.reverseView();
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

        @Override
        public void initialize(URL location, ResourceBundle resourceBundle) {
            super.initialize(location, resourceBundle);
            this.reverseView.managedBindVisible();
        }
    }
}
