package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.util.Objects;

/**
 * redis string键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    public RedisStringKeyTab(RedisStringKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisStringKeyTab.fxml";
    }

    @Override
    public RedisStringKeyTabController controller() {
        return (RedisStringKeyTabController) super.controller();
    }

    /**
     * string键tab内容组件
     *
     * @author oyzh
     * @since 2023/06/31
     */
    public static class RedisStringKeyTabController extends RedisKeyTabController<RedisStringKeyTreeItem> {

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
         * 二进制数据
         */
        @FXML
        private FXText binary;

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
         * redis数据监听器
         */
        private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
            if (!Objects.equals(this.treeItem.rawData(), newValue)) {
                this.saveNodeData.enable();
                this.treeItem.data(newValue);
            }
        };

        @Override
        protected void initKey() {
            // 数据处理
            this.firstShowData();
            // 刷新二进制处理
            this.flushBinary();
            // 按钮状态处理
            this.saveNodeData.setDisable(!this.treeItem.isDataUnsaved());
            // 如果是raw格式，则选择binary
            if (this.treeItem.isRawEncoding()) {
                this.format.selectBinary();
            } else {// 自动匹配
                RichDataType dataType = this.nodeData.showDetectData(this.treeItem.data());
                this.format.selectObj(dataType);
            }
        }

        /**
         * 刷新二进制处理
         */
        private void flushBinary() {
            // 如果是raw格式，则选择binary
            if (this.treeItem.isRawEncoding()) {
                this.binary.display();
            } else {
                this.binary.disappear();
            }
        }

        /**
         * 重载数据
         */
        @FXML
        private void reloadData() {
            // 放弃保存
            if (this.treeItem.isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
                return;
            }
            try {
                // 刷新数据
                this.treeItem.refreshKeyValue();
                // 初始化数据
                this.initKey();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }

        @FXML
        @Override
        protected void saveKeyValue() {
            if (this.treeItem.isDataUnsaved()) {
                TaskManager.start(() -> {
                    this.treeItem.saveKeyValue();
                    this.flushBinary();
                    // 保存监听
                    this.saveNodeData.disable();
                });
            }
        }

        /**
         * 数据撤销
         */
        @FXML
        protected void dataUndo() {
            this.nodeData.undo();
            this.nodeData.requestFocus();
        }

        /**
         * 数据重做
         */
        @FXML
        protected void dataRedo() {
            this.nodeData.redo();
            this.nodeData.requestFocus();
        }

        /**
         * 粘贴数据
         */
        @FXML
        protected void pasteData() {
            this.nodeData.paste();
            this.nodeData.requestFocus();
        }

        /**
         * 清除数据
         */
        @FXML
        protected void clearData() {
            this.nodeData.clear();
            this.nodeData.requestFocus();
        }

        @Override
        protected void firstShowData() {
            this.nodeData.showData(this.treeItem.data());
            this.nodeData.forgetHistory();
        }

        @Override
        protected void showData(RichDataType dataType) {
            this.nodeData.showData(dataType, this.treeItem.data());
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
    }
}
