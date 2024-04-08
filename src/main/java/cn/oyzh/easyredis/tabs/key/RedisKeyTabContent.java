package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.controller.key.RedisKeyTTLController;
import cn.oyzh.easyredis.fx.RedisDataTextArea;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * redis键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class RedisKeyTabContent<T extends RedisKeyTreeItem<?, ?>> extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    protected FlexVBox root;

    /**
     * 收藏
     */
    @FXML
    protected SVGGlyph collect;

    /**
     * 取消收藏
     */
    @FXML
    protected SVGGlyph unCollect;

    /**
     * 数据撤销
     */
    @FXML
    protected SVGGlyph dataUndo;

    /**
     * 数据重做
     */
    @FXML
    protected SVGGlyph dataRedo;

    /**
     * 清除数据
     */
    @FXML
    protected SVGGlyph clearData;

    /**
     * 粘贴数据
     */
    @FXML
    protected SVGGlyph pasteData;

    /**
     * 格式
     */
    @FXML
    protected RedisFormatComboBox format;

    /**
     * 树节点
     */
    protected T treeItem;

    /**
     * ttl组件
     */
    @FXML
    protected FXLabel ttl;

    /**
     * 加载耗时
     */
    @FXML
    protected FXLabel loadTime;

    /**
     * 数据组件
     */
    @FXML
    protected RedisDataTextArea nodeData;

    /**
     * 键信息
     */
    @FXML
    private RedisKeyInfoContent keyInfoController;

    /**
     * 获取数据监听器
     *
     * @return 数据监听器
     */
    protected ChangeListener<String> getDataListener() {
        return null;
    }

    /**
     * 初始化
     *
     * @param treeItem 树键
     */
    public boolean init(T treeItem) {
        this.treeItem = treeItem;

        // ttl处理
        this.flushTTL();

        // 键已过期
        if (this.treeItem.isExpire()) {
            return false;
        }

        // 键数据处理
        if (this.nodeData.isEditable()) {
            this.nodeData.addTextChangeListener(this.getDataListener());
            this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
            this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        }

        // 收藏处理
        this.collect.managedBindVisible();
        this.unCollect.managedBindVisible();
        this.collect.setVisible(!this.treeItem.isCollect());
        this.unCollect.setVisible(this.treeItem.isCollect());

        // 初始化键信息
        this.keyInfoController.init(treeItem);

        // 格式变化
        this.format.selectedItemChanged((t3, t2, t1) -> this.onFormatChange(this.format));

        // 初始化节点
        this.initNode();

        // 加载耗时处理
        FXUtil.runWait(() -> this.loadTime.setText("耗时:" + this.treeItem.loadTime() + "ms"));
        return true;
    }

    /**
     * 初始化键
     */
    protected void initNode() {

    }

    /**
     * 获取键数据组件
     *
     * @return 键数据组件
     */
    public FlexTextArea getNodeDataNode() {
        return this.nodeData;
    }

    /**
     * 复制键信息
     */
    @FXML
    protected void copyKeyInfo() {
        String builder = "数据库：" + this.treeItem.dbIndex() + System.lineSeparator() +
                "键类型：" + this.treeItem.value().type() + System.lineSeparator() +
                "键名称：" + this.treeItem.key();
        ClipboardUtil.setStringAndTip(builder, "键信息");
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

    /**
     * 重命名键
     */
    @FXML
    protected void renameNode() {
        this.treeItem.rename();
    }

    /**
     * 删除键
     */
    @FXML
    protected void deleteNode() {
        if (MessageBox.confirm("删除" + this.treeItem.key(), "确定删除此键？")) {
            this.treeItem.delete();
        }
    }

    /**
     * 收藏
     */
    @FXML
    protected void collect() {
        this.treeItem.collect();
        this.collect.disappear();
        this.unCollect.display();
    }

    /**
     * 取消收藏
     */
    @FXML
    protected void unCollect() {
        this.treeItem.unCollect(true);
        this.collect.display();
        this.unCollect.disappear();
    }

    /**
     * 数据组件键盘按下事件
     *
     * @param e 事件
     */
    @FXML
    protected void onNodeDataKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        // 保存键数据
        if (code == KeyCode.S && e.isControlDown()) {
            this.saveNodeData();
        }
    }

    /**
     * 保存键数据
     */
    @FXML
    protected void saveNodeData() {
        if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(this.treeItem::saveNodeValue);
        }
    }

    /**
     * 重新加载键
     */
    public void reloadNode() {

    }

    /**
     * ttl设置
     */
    @FXML
    protected void ttlUpdate() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyTTLController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        if (this.ttl.getCursor() != Cursor.HAND) {
            this.ttl.setCursor(Cursor.HAND);
        }
        this.ttl.setText("TTL: " + this.treeItem.ttl());
    }

    /**
     * 格式变化事件
     *
     * @param comboBox 格式选择组件
     */
    protected void onFormatChange(RedisFormatComboBox comboBox) {
        if (comboBox.isRawFormat()) {
            this.showData((byte) 0);
            this.nodeData.setEditable(true);
        } else if (comboBox.isJsonFormat()) {
            this.showData((byte) 1);
            this.nodeData.setEditable(true);
        } else if (comboBox.isBinaryFormat()) {
            this.showData((byte) 2);
            this.nodeData.setEditable(false);
        } else if (comboBox.isHexFormat()) {
            this.showData((byte) 3);
            this.nodeData.setEditable(false);
        } else if (comboBox.isStringFormat()) {
            this.showData((byte) 4);
            this.nodeData.setEditable(false);
        }
    }

    /**
     * 显示数据
     *
     * @param showType 类型
     */
    protected void showData(byte showType) {
        this.nodeData.disable();
        this.nodeData.clear();
        this.nodeData.setPromptText("数据加载中...");
        ExecutorUtil.start(() -> FXUtil.runLater(() -> {
            try {
                this.nodeData.setShowType(showType);
                this.nodeData.showData();
                this.treeItem.clearData();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                this.nodeData.setPromptText("");
                this.nodeData.enable();
            }
        }), 50);
    }

    /**
     * 设置原始数据
     *
     * @param rawData 原始数据
     */
    protected void setRawData(Object rawData) {
        if (rawData == null) {
            return;
        }
        this.nodeData.disable();
        this.nodeData.setPromptText("数据加载中...");
        ExecutorUtil.start(() -> FXUtil.runLater(() -> {
            try {
                this.nodeData.setRawData(rawData);
                this.treeItem.clearData();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                this.nodeData.setPromptText("");
                this.nodeData.enable();
            }
        }), 50);
    }

    /**
     * 清除原始数据
     */
    protected void clearRawData() {
        this.nodeData.clear();
        this.nodeData.disable();
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        super.onTabClose(tab, event);
        // 取消当前键的选中
        if (this.treeItem.getTreeView().getSelectedItem() == this.treeItem) {
            this.treeItem.getTreeView().select(this.treeItem.connectTreeItem());
        }
    }
}
