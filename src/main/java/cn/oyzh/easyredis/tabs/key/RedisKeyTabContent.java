package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.controller.key.RedisKeyTTLController;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.rich.data.RichDataPane;
import cn.oyzh.fx.rich.data.RichDataType;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

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
    protected RichDataPane nodeData;
    // protected RedisDataTextArea nodeData;

    /**
     * 键信息
     */
    @FXML
    private RedisKeyInfoContent keyInfoController;

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

        // 收藏处理
        this.collect.setVisible(!this.treeItem.isCollect());
        this.unCollect.setVisible(this.treeItem.isCollect());

        // 初始化键信息
        this.keyInfoController.init(treeItem);

        // 格式监听
        if (this.format != null) {
            this.format.selectedItemChanged(this.formatListener);
        }

        // 初始化节点
        this.initNode();

        // 加载耗时处理
        FXUtil.runWait(() -> this.loadTime.setText(I18nHelper.cost() + ":" + this.treeItem.loadTime() + "ms"));
        return true;
    }

    /**
     * 初始化键
     */
    protected void initNode() {

    }

    /**
     * 复制键信息
     */
    @FXML
    protected void copyKeyInfo() {
        String builder = I18nHelper.database() + ": " + this.treeItem.dbIndex() + System.lineSeparator() +
                I18nHelper.keyType() + ": " + this.treeItem.value().type() + System.lineSeparator() +
                I18nHelper.keyName() + ": " + this.treeItem.key();
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
    protected void renameKey() {
        this.treeItem.rename();
    }

    /**
     * 删除键
     */
    @FXML
    protected void deleteNode() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.treeItem.key() + "]", I18nHelper.areYouSure())) {
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
        this.treeItem.unCollect();
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
            this.saveKeyData();
        }
    }

    /**
     * 保存键数据
     */
    @FXML
    protected void saveKeyData() {
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
     * 首次显示数据
     */
    protected void firstShowData() {
        this.showData();
        // 首次设置数据要清除历史
        this.nodeData.forgetHistory();
    }

    /**
     * 显示数据
     */
    protected void showData() {
        this.nodeData.showData(this.treeItem.rawValue());
    }

    /**
     * 显示数据
     *
     * @param dataType 数据类型
     */
    protected void showData(RichDataType dataType) {
        this.nodeData.showData(dataType, this.treeItem.rawValue());
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

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        this.collect.managedBindVisible();
        this.unCollect.managedBindVisible();
    }
}
