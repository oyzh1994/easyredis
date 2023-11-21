package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.tabs.key.KeyTabContent;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * string键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/31
 */
@Lazy
@Component
public class StringKeyTabContent extends KeyTabContent<RedisStringKeyTreeItem> {

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
            // 如果是raw格式，则选择binary
            if (this.treeItem.isRawEncoding()) {
                this.format.selectBinary();
            }
            this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
            return true;
        }
        return false;
    }

    @Override
    protected void initNode() {
        // 数据处理
        this.setRawData(this.treeItem.rawValue());
        // 大小
        Integer size = this.treeItem.size();
        if (size == null) {
            this.size.setText("大小: N/A");
        } else {
            this.size.setText("大小: " + size + " bytes");
        }
        // 刷新二进制处理
        this.flushBinary();
        // 按钮状态处理
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
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
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm("放弃未保存的数据？")) {
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

    @Override
    public void onNodeDataKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        // 保存键数据
        if (code == KeyCode.S && e.isControlDown()) {
            this.saveNodeData();
        }
    }

    @FXML
    @Override
    protected void saveNodeData() {
        if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.flushBinary();
                }
            });
        }
    }
}
