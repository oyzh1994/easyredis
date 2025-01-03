package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.key.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisZSetKeyTreeItem;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.util.NodeUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;

/**
 * redis键信息组件
 *
 * @author oyzh
 * @since 2023/08/03
 */
public class RedisKeyDataController extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    private FXTab root;

    /**
     * set键
     */
    @FXML
    private RedisSetKeyController setKeyController;

    /**
     * zset键
     */
    @FXML
    private RedisZSetKeyController zsetKeyController;

    /**
     * list键
     */
    @FXML
    private RedisListKeyController listKeyController;

    /**
     * hylog键
     */
    @FXML
    private RedisHylogKeyController hylogKeyController;

    /**
     * hash键
     */
    @FXML
    private RedisHashKeyController hashKeyController;

    /**
     * string键
     */
    @FXML
    private RedisStringKeyController stringKeyController;

    /**
     * stream键
     */
    @FXML
    private RedisStreamKeyController streamKeyController;

    /**
     * 坐标键
     */
    @FXML
    private RedisCoordinateKeyController coordinateKeyController;

    /**
     * 当前item
     */
    private RedisKeyTreeItem treeItem;

    /**
     * 执行初始化
     *
     * @param treeItem 节点
     */
    public void init(RedisKeyTreeItem treeItem) {
        this.treeItem = treeItem;
        // 隐藏旧内容
        NodeGroupUtil.disappear(this.root, "key-data");
        // 处理具体业务
        if (treeItem instanceof RedisStringKeyTreeItem item1) {
            if (item1.isHyLog()) {
                this.hylogKeyController.init(item1);
                Node node = this.root.getContent().lookup("#hylogKey");
                NodeUtil.display(node);
            } else {
                this.stringKeyController.init(item1);
                Node node = this.root.getContent().lookup("#stringKey");
                NodeUtil.display(node);
            }
        } else if (treeItem instanceof RedisZSetKeyTreeItem item1) {
            if (item1.isCoordinateView()) {
                this.coordinateKeyController.init(item1);
                Node node = this.root.getContent().lookup("#coordinateKey");
                NodeUtil.display(node);
            } else {
                this.zsetKeyController.init(item1);
                Node node = this.root.getContent().lookup("#zsetKey");
                NodeUtil.display(node);
            }
        } else if (treeItem instanceof RedisHashKeyTreeItem item1) {
            this.hashKeyController.init(item1);
            Node node = this.root.getContent().lookup("#hashKey");
            NodeUtil.display(node);
        } else if (treeItem instanceof RedisListKeyTreeItem item1) {
            this.listKeyController.init(item1);
            Node node = this.root.getContent().lookup("#listKey");
            NodeUtil.display(node);
        } else if (treeItem instanceof RedisSetKeyTreeItem item1) {
            this.setKeyController.init(item1);
            Node node = this.root.getContent().lookup("#setKey");
            NodeUtil.display(node);
        } else if (treeItem instanceof RedisStreamKeyTreeItem item1) {
            this.streamKeyController.init(item1);
            Node node = this.root.getContent().lookup("#streamKey");
            NodeUtil.display(node);
        }
        // 刷新tab
        this.flushTab();
        // 判断这个key是否到期
        if (treeItem.isExpire()) {
            BackgroundService.submitFXLater(() -> {
                String tips = I18nHelper.key() + " [" + treeItem.key() + "] " + I18nHelper.expired() + ", " + I18nHelper.delete() + "?";
                if (MessageBox.confirm(tips)) {
                    treeItem.deleteByExpired();
                    this.closeTab();
                }
            });
        }
    }

    /**
     * 获取键Controller
     * @return 键Controller
     */
    private RedisKeyController<?> getKeyController() {
        if (this.treeItem instanceof RedisStringKeyTreeItem item1) {
            if (item1.isHyLog()) {
                return this.stringKeyController;
            }
            return this.hylogKeyController;
        }
        if (this.treeItem instanceof RedisZSetKeyTreeItem item1) {
            if (item1.isCoordinateView()) {
                return this.zsetKeyController;
            }
            return this.coordinateKeyController;
        }
        if (this.treeItem instanceof RedisHashKeyTreeItem) {
            return this.hashKeyController;
        }
        if (this.treeItem instanceof RedisListKeyTreeItem) {
            return this.listKeyController;
        }
        if (this.treeItem instanceof RedisSetKeyTreeItem) {
            return this.setKeyController;
        }
        if (this.treeItem instanceof RedisStreamKeyTreeItem) {
            return this.streamKeyController;
        }
        return null;
    }

    /**
     * 重载键
     */
    public void reloadKey() {
        RedisKeyController<?> controller = this.getKeyController();
        if (controller != null) {
            controller.reloadKey();
        }
    }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        RedisKeyController<?> controller = this.getKeyController();
        if (controller != null) {
            controller.flushTTL();
        }
    }
}
