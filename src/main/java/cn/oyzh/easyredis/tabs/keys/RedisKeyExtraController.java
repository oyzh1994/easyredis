package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.controller.key.RedisKeyTTLController;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

/**
 * redis键信息组件
 *
 * @author oyzh
 * @since 2023/08/03
 */
public class RedisKeyExtraController {

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
     * redis键节点
     */
    private RedisKeyTreeItem<?> treeItem;

    /**
     * 初始化组件
     *
     * @param treeItem redis树键
     */
    public void init(RedisKeyTreeItem<?> treeItem) {
        this.treeItem = treeItem;
        this.flushTTL();
        this.loadTime.setText(I18nHelper.cost() + ":" + this.treeItem.loadTime() + "ms");
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
     * ttl设置
     */
    @FXML
    protected void ttlUpdate() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyTTLController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

}
