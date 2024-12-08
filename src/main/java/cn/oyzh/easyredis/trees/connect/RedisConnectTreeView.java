package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoAddController;
import cn.oyzh.easyredis.event.RedisAddConnectEvent;
import cn.oyzh.easyredis.event.RedisAddGroupEvent;
import cn.oyzh.easyredis.event.RedisInfoAddedEvent;
import cn.oyzh.easyredis.event.RedisInfoUpdatedEvent;
import cn.oyzh.easyredis.event.RedisKeyAddedEvent;
import cn.oyzh.easyredis.event.RedisKeyDeletedEvent;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

/**
 * redis树
 *
 * @author oyzh
 * @since 2023/1/29
 */
// @Accessors(chain = true, fluent = true)
public class RedisConnectTreeView extends RichTreeView implements FXEventListener {

    // /**
    //  * 搜索中标志位
    //  */
    // @Getter
    // private volatile boolean searching;

    // public RedisConnectTreeView() {
    //     this.dragContent = "redis_connect_tree_drag";
    //     this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
    //     // 初始化根节点
    //     super.setRoot(new RedisRootTreeItem(this));
    //     this.getRoot().expend();
    // }

    @Override
    protected void initTreeView() {
        this.dragContent = "redis_connect_tree_drag";
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.initTreeView();
    }

    @Override
    protected void initRoot() {
        super.setRoot(new RedisRootTreeItem(this));
        this.getRoot().expend();
        super.initRoot();
    }

    @Override
    protected void initEvenListener() {
        super.initEvenListener();
        // 暂停按键处理
        KeyListener.listenReleased(this, KeyCode.PAUSE, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisConnectTreeItem treeItem) {
                treeItem.closeConnect();
                // } else if (item instanceof RedisKeyTreeItem<?, ?> nodeTreeItem) {
                //     nodeTreeItem.connectTreeItem().closeConnect();
            }
        });
    }

    // @Override
    // public RedisConnectTreeItemFilter itemFilter() {
    //     // 初始化过滤器
    //     if (this.itemFilter == null) {
    //         RedisConnectTreeItemFilter filter = new RedisConnectTreeItemFilter();
    //         // filter.initFilters();
    //         this.itemFilter = filter;
    //     }
    //     return (RedisConnectTreeItemFilter) this.itemFilter;
    // }

    @Override
    public RedisRootTreeItem getRoot() {
        return (RedisRootTreeItem) super.getRoot();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (RedisConnectTreeItem treeItem : this.getRoot().getConnectedItems()) {
            ThreadUtil.startVirtual(() -> treeItem.closeConnect(false));
        }
    }

    /**
     * 键添加事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyAdded(RedisKeyAddedEvent event) {
        // if (event != null && event.data() != null) {
        //     event.data().onKeyAdded(event.key());
        // }
        for (RedisConnectTreeItem connectItem : this.getRoot().getConnectItems()) {
            if (connectItem.value() == event.data()) {
                RedisDatabaseTreeItem dbItem = connectItem.getDatabaseItem(event.dbIndex());
                if (dbItem != null) {
                    dbItem.onKeyAdded(event.key());
                }
                break;
            }
        }
    }

    /**
     * 键删除事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyDeleted(RedisKeyDeletedEvent event) {
        for (RedisConnectTreeItem connectItem : this.getRoot().getConnectItems()) {
            if (connectItem.value() == event.data()) {
                RedisDatabaseTreeItem dbItem = connectItem.getDatabaseItem(event.dbIndex());
                if (dbItem != null) {
                    dbItem.onKeyDeleted(event.key());
                }
                break;
            }
        }
    }
    //
    // /**
    //  * 键刷新事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void keyFlushed(RedisKeyFlushedEvent event) {
    //     if (event != null && event.data() != null) {
    //         event.data().reloadChild();
    //     }
    // }

    // /**
    //  * 键复制事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void keyCopied(RedisKeyCopiedEvent event) {
    //     int dbIndex = event.targetDB();
    //     TreeItem<?> treeItem = event.data();
    //     RedisDBTreeItem targetDBItem = null;
    //     if (treeItem instanceof RedisDBTreeItem dbItem) {
    //         dbItem.reloadChild();
    //         targetDBItem = dbItem.parent().getDatabaseItem(dbIndex);
    //     } else if (treeItem instanceof RedisKeyTreeItem<?, ?> keyTreeItem) {
    //         targetDBItem = keyTreeItem.connectTreeItem().getDatabaseItem(dbIndex);
    //     }
    //     if (targetDBItem != null) {
    //         targetDBItem.reloadChild();
    //     }
    // }

    // /**
    //  * 键移动事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onKeyMoved(RedisKeyMovedEvent event) {
    //     int dbIndex = event.targetDB();
    //     TreeItem<?> treeItem = event.data();
    //     RedisDBTreeItem targetDBItem = null;
    //     if (treeItem instanceof RedisDBTreeItem dbItem) {
    //         dbItem.reloadChild();
    //         targetDBItem = dbItem.parent().getDatabaseItem(dbIndex);
    //     } else if (treeItem instanceof RedisKeyTreeItem<?, ?> keyTreeItem) {
    //         keyTreeItem.remove();
    //         targetDBItem = keyTreeItem.connectTreeItem().getDatabaseItem(dbIndex);
    //     }
    //     if (targetDBItem != null) {
    //         targetDBItem.reloadChild();
    //     }
    // }

    // /**
    //  * 搜索开始事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void searchStart(RedisSearchStartEvent event) {
    //     this.searching = true;
    //     this.filter();
    // }
    //
    // /**
    //  * 搜索结束事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void searchFinish(RedisSearchFinishEvent event) {
    //     this.searching = false;
    //     this.filter();
    // }

    // /**
    //  * 树节点过滤
    //  */
    // @EventSubscribe
    // private void treeChildFilter(TreeChildFilterEvent event) {
    //     this.itemFilter().initFilters();
    //     this.filter();
    // }

    /**
     * 添加连接事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void addConnect(RedisAddConnectEvent event) {
        StageManager.showStage(RedisInfoAddController.class, this.window());
    }

    /**
     * 添加分组事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addGroup(RedisAddGroupEvent event) {
        this.getRoot().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoAdded(RedisInfoAddedEvent event) {
        this.getRoot().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoUpdated(RedisInfoUpdatedEvent event) {
        this.getRoot().infoUpdate(event.data());
    }
}
