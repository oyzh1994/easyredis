package cn.oyzh.easyredis.trees;

import cn.hutool.extra.spring.SpringUtil;
import cn.oyzh.easyredis.event.RedisAddConnectEvent;
import cn.oyzh.easyredis.event.RedisAddGroupEvent;
import cn.oyzh.easyredis.event.msg.RedisInfoAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisInfoUpdatedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyCopiedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyDeletedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyFlushedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyMovedMsg;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.root.RedisRootTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.trees.RichTreeView;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * redis树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class RedisTreeView extends RichTreeView implements EventListener {

    /**
     * 搜索中标志位
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private volatile boolean searching;

    @Override
    public RedisTreeItemFilter itemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            RedisTreeItemFilter filter = SpringUtil.getBean(RedisTreeItemFilter.class);
            filter.initFilters();
            this.itemFilter = filter;
        }
        return (RedisTreeItemFilter) this.itemFilter;
    }

    public RedisTreeView() {
        EventListener.super.register();
        this.dragContent = "redis_tree_drag";
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RedisTreeCell());
        super.root(new RedisRootTreeItem(this));
        this.root().extend();
    }

    @Override
    public RedisRootTreeItem root() {
        return (RedisRootTreeItem) this.getRoot();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (RedisConnectTreeItem treeItem : this.root().getConnectedItems()) {
            ThreadUtil.startVirtual(() -> treeItem.closeConnect(false));
        }
    }

    @Override
    protected void initEvenListener() {
        super.initEvenListener();
        // 暂停按键处理
        KeyListener.listenReleased(this, KeyCode.PAUSE, t1 -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisConnectTreeItem treeItem) {
                treeItem.closeConnect();
            } else if (item instanceof RedisKeyTreeItem<?, ?> nodeTreeItem) {
                nodeTreeItem.connectTreeItem().closeConnect();
            }
        });
    }

    /**
     * 重新载入
     */
    public void reload() {
        TreeItem<?> item = this.getSelectedItem();
    }

    /**
     * 键添加事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_ADDED, verbose = true, async = true)
    @Subscribe
    private void onKeyAdded(RedisKeyAddedMsg msg) {
        if (msg != null && msg.data() != null) {
            msg.data().onKeyAdded(msg.key());
        }
    }

    /**
     * 键删除事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_DELETED, verbose = true, async = true)
    @Subscribe
    private void onKeyDeleted(RedisKeyDeletedMsg msg) {
        if (msg != null && msg.data() != null) {
            msg.data().onKeyDeleted(msg.key());
        }
    }

    /**
     * 键刷新事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_FLUSHED, verbose = true, async = true)
    @Subscribe
    private void onKeyFlushed(RedisKeyFlushedMsg msg) {
        if (msg != null && msg.data() != null) {
            msg.data().reloadChild();
        }
    }

    /**
     * 键复制事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_COPIED, verbose = true, async = true)
    @Subscribe
    private void onKeyCopied(RedisKeyCopiedMsg msg) {
        int dbIndex = msg.targetDB();
        TreeItem<?> treeItem = msg.data();
        RedisDBTreeItem targetDBItem = null;
        if (treeItem instanceof RedisDBTreeItem dbItem) {
            dbItem.reloadChild();
            targetDBItem = dbItem.parent().getDatabaseItem(dbIndex);
        } else if (treeItem instanceof RedisKeyTreeItem<?, ?> keyTreeItem) {
            targetDBItem = keyTreeItem.connectTreeItem().getDatabaseItem(dbIndex);
        }
        if (targetDBItem != null) {
            targetDBItem.reloadChild();
        }
    }

    /**
     * 键移动事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_MOVED, verbose = true, async = true)
    @Subscribe
    private void onKeyMoved(RedisKeyMovedMsg msg) {
        int dbIndex = msg.targetDB();
        TreeItem<?> treeItem = msg.data();
        RedisDBTreeItem targetDBItem = null;
        if (treeItem instanceof RedisDBTreeItem dbItem) {
            dbItem.reloadChild();
            targetDBItem = dbItem.parent().getDatabaseItem(dbIndex);
        } else if (treeItem instanceof RedisKeyTreeItem<?, ?> keyTreeItem) {
            keyTreeItem.remove();
            targetDBItem = keyTreeItem.connectTreeItem().getDatabaseItem(dbIndex);
        }
        if (targetDBItem != null) {
            targetDBItem.reloadChild();
        }
    }

    /**
     * 搜索开始事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_START, async = true, verbose = true)
    @Subscribe
    private void onSearchStart() {
        this.searching = true;
        this.filter();
    }

    /**
     * 搜索结束事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_FINISH, async = true, verbose = true)
    @Subscribe
    private void onSearchFinish() {
        this.searching = false;
        this.filter();
    }

    /**
     * 树节点过滤
     */
    // @EventReceiver(value = RedisEventTypes.TREE_CHILD_FILTER, async = true, verbose = true)
    @Subscribe
    private void onTreeChildFilter() {
        this.itemFilter().initFilters();
        this.filter();
    }

    // /**
    //  * 导入开始事件
    //  */
    // @EventReceiver(RedisEventTypes.REDIS_IMPORT_START)
    // private void onImportStart() {
    //     this.importing = true;
    //     StaticLog.info("REDIS_IMPORT_START.");
    // }
    //
    // /**
    //  * 导入结束事件
    //  */
    // @EventReceiver(value = RedisEventTypes.REDIS_IMPORT_FINISH, async = true, verbose = true)
    // private void onImportFinish(RedisConnectTreeItem connectTreeItem) {
    //     this.importing = false;
    //     for (RedisDBTreeItem child : connectTreeItem.getChildren()) {
    //         child.reloadChild();
    //     }
    //     StaticLog.info("REDIS_IMPORT_FINISH.");
    // }

    /**
     * 添加连接
     */
    @Subscribe
    private void addConnect(RedisAddConnectEvent event) {
        this.root().addConnect();
    }

    /**
     * 添加分组
     */
    @Subscribe
    private void addConnect(RedisAddGroupEvent event) {
        this.root().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @Subscribe
    public void infoAdded(RedisInfoAddedMsg event) {
        this.root().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @Subscribe
    public void infoUpdate(RedisInfoUpdatedMsg event) {
        this.root().infoUpdate(event.data());
    }

}
