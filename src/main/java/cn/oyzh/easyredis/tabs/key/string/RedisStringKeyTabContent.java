package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.controller.row.RedisHyLogElementsAddController;
import cn.oyzh.easyredis.event.RedisHyLogElementsAddedEvent;
import cn.oyzh.easyredis.tabs.key.RedisKeyTabContent;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;

/**
 * string键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/31
 */
public class RedisStringKeyTabContent extends RedisKeyTabContent<RedisStringKeyTreeItem> {

    /**
     * 数据大小
     */
    @FXML
    private FXLabel size;

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
     * redis数据监听器
     */
    @Getter(AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (Objects.equals(newValue, this.treeItem.value().value())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
    };

    @Override
    public boolean init(RedisStringKeyTreeItem treeItem) {
        if (super.init(treeItem)) {
            this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
            // 键数据处理
            this.nodeData.addTextChangeListener(this.getDataListener());
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
        // 大小
        Integer size = this.treeItem.size();
        if (size == null) {
            this.size.setText(I18nHelper.size() + ": N/A");
        } else {
            this.size.setText(I18nHelper.size() + ": " + size + " bytes");
        }
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
}
