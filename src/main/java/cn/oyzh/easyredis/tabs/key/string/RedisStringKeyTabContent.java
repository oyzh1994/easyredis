package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.tabs.key.RedisKeyTabContent;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.util.Objects;

/**
 * string键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/31
 */
public class RedisStringKeyTabContent extends RedisKeyTabContent<RedisStringKeyTreeItem> {

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

    // /**
    //  * 数据大小
    //  */
    // @FXML
    // private FXLabel size;

    /**
     * 二进制数据
     */
    @FXML
    private FXLabel binary;

    /**
     * 数据保存按钮
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
     * redis数据监听器
     */
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (Objects.equals(newValue, this.treeItem.value().value())) {
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
    public boolean init(RedisStringKeyTreeItem treeItem) {
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
        // 数据处理
        // this.setRawData(this.treeItem.rawValue());
        this.firstShowData();
        // // 大小
        // Integer size = this.treeItem.size();
        // if (size == null) {
        //     this.size.setText(I18nHelper.size() + ": N/A");
        // } else {
        //     this.size.setText(I18nHelper.size() + ": " + size + " bytes");
        // }
        // 刷新二进制处理
        this.flushBinary();
        // 按钮状态处理
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
        // 如果是raw格式，则选择binary
        if (this.treeItem.isRawEncoding()) {
            this.format.selectBinary();
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
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        // 刷新数据
        try {
            this.treeItem.refreshNodeValue();
            // 数据变更
            this.initNode();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @FXML
    @Override
    protected void saveKeyData() {
        if (this.treeItem.dataUnsaved()) {
            TaskManager.start(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.flushBinary();
                }
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
        this.nodeData.showData(this.treeItem.rawValue());
        // 首次设置数据要清除历史
        this.nodeData.forgetHistory();
    }

    @Override
    protected void showData(RichDataType dataType) {
        this.nodeData.showData(dataType, this.treeItem.rawValue());
    }
}
