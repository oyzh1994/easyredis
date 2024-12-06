package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

/**
 * redis键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public abstract class RedisKeyTabController<T extends RedisKeyTreeItem> extends DynamicTabController {

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
     * 树节点
     */
    protected T treeItem;

    // /**
    //  * ttl组件
    //  */
    // @FXML
    // protected FXLabel ttl;
    //
    // /**
    //  * 加载耗时
    //  */
    // @FXML
    // protected FXLabel loadTime;

    // /**
    //  * 键信息
    //  */
    // @FXML
    // private RedisKeyInfoTab.RedisKeyInfoController keyInfoController;

    /**
     * 键扩展信息
     */
    @FXML
    private RedisKeyExtraController keyExtraController;

    /**
     * 初始化
     *
     * @param treeItem 树键
     */
    public boolean init(T treeItem) {
        this.treeItem = treeItem;

        // // ttl处理
        // this.flushTTL();

        // 处理额外信息
        this.keyExtraController.init(treeItem);

        // 键已过期
        if (this.treeItem.isExpire()) {
            return false;
        }

        // 收藏处理
        this.collect.setVisible(!this.treeItem.isCollect());
        this.unCollect.setVisible(this.treeItem.isCollect());

        // // 初始化键信息
        // this.keyInfoController.init(treeItem);

        // 初始化节点
        this.initNode();

        // // 加载耗时处理
        // FXUtil.runWait(() -> this.loadTime.setText(I18nHelper.cost() + ":" + this.treeItem.loadTime() + "ms"));
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
        if (KeyboardUtil.isCtrlS(e)) {
            this.saveKeyData();
            e.consume();
        }
    }

    /**
     * 保存键数据
     */
    @FXML
    protected void saveKeyData() {
        if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(this.treeItem::saveKeyValue);
        }
    }

    /**
     * 重新加载键
     */
    public void reloadNode() {

    }

    // /**
    //  * ttl设置
    //  */
    // @FXML
    // protected void ttlUpdate() {
    //     StageAdapter fxView = StageManager.parseStage(RedisKeyTTLController.class, this.treeItem.window());
    //     fxView.setProp("treeItem", this.treeItem);
    //     fxView.display();
    // }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        // if (this.ttl.getCursor() != Cursor.HAND) {
        //     this.ttl.setCursor(Cursor.HAND);
        // }
        // this.ttl.setText("TTL: " + this.treeItem.ttl());
        this.keyExtraController.flushTTL();
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        super.onTabClose(tab, event);
        // // 取消当前键的选中
        // if (this.treeItem.getTreeView().getSelectedItem() == this.treeItem) {
        //     this.treeItem.getTreeView().select(this.treeItem.connectTreeItem());
        // }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.collect.managedBindVisible();
        this.unCollect.managedBindVisible();
    }
    //
    // @Override
    // public void initialize(URL location, ResourceBundle resourceBundle) {
    //     super.initialize(location, resourceBundle);
    //     this.collect.managedBindVisible();
    //     this.unCollect.managedBindVisible();
    // }

    /**
     * 首次显示数据
     */
    protected abstract void firstShowData( ) ;

    /**
     * 显示数据
     *
     * @param dataType 数据类型
     */
    protected abstract void showData(RichDataType dataType) ;


}
