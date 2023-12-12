package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.controller.row.RedisHyLogElementsAddController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisHyLogElementsAddedMsg;
import cn.oyzh.easyredis.tabs.key.RedisKeyTabContent;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
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
     * 统计值
     */
    @FXML
    private FXLabel count;

    /**
     * 添加统计元素
     */
    @FXML
    private SVGGlyph addRow;

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
            this.addRow.managedBindVisible();
            // hyLog格式
            if (this.treeItem.isHyLog()) {
                this.nodeData.setEditable(false);
            } else {
                this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
                this.nodeData.setEditable(true);
            }
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
        // hyLog格式
        if (this.treeItem.isHyLog()) {
            this.initHyLogNode();
        } else {
            this.initStringNode();
        }
    }

    /**
     * 初始化字符串节点
     */
    private void initStringNode() {
        // 按钮状态处理
        this.count.disappear();
        this.addRow.disappear();
        this.nodeData.enable();
        this.dataUndo.display();
        this.dataRedo.display();
        this.pasteData.display();
        this.clearData.display();
        this.saveNodeData.display();
        // 按钮状态处理
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    }

    /**
     * 初始化统计值节点
     */
    private void initHyLogNode() {
        // 统计值
        this.count.setText("统计值: " + this.treeItem.count());
        // 按钮状态处理
        this.count.display();
        this.addRow.display();
        this.nodeData.disable();
        this.dataUndo.disappear();
        this.dataRedo.disappear();
        this.pasteData.disappear();
        this.clearData.disappear();
        this.saveNodeData.disappear();
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

    /**
     * 添加统计值
     */
    @FXML
    private void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisHyLogElementsAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    /**
     * hyLog元素添加事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_HYLOG_ELEMENT_ADDED, verbose = true, async = true, fxThread = true)
    private void onHyLogElementAdded(RedisHyLogElementsAddedMsg msg) {
        if (this.treeItem == msg.item()) {
            // 刷新数据
            this.treeItem.flushCount();
            this.initNode();
        }
    }

    @Override
    public void onTabInit() {
        EventUtil.register(this);
    }

    @Override
    public void onTabClose(Event event) {
        EventUtil.unregister(this);
    }
}
