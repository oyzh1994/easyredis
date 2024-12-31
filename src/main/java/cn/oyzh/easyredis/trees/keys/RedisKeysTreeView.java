package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.key.RedisKeyAddedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyCopiedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyDeletedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyFilteredEvent;
import cn.oyzh.easyredis.event.key.RedisKeyFlushedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyMovedEvent;
import cn.oyzh.easyredis.event.key.RedisKeysMovedEvent;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class RedisKeysTreeView extends RichTreeView implements FXEventListener {

    @Getter
    @Setter
    @Accessors(fluent = true, chain = false)
    private RedisDatabaseTreeItem dbItem;

    public int dbIndex() {
        return this.dbItem.dbIndex();
    }

    public RedisClient client() {
        return this.dbItem == null ? null : this.dbItem.client();
    }

    public RedisConnect redisConnect() {
        return this.dbItem.redisConnect();
    }

    @Override
    protected void initTreeView() {
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.initTreeView();
    }

    @Override
    protected void initRoot() {
        this.setRoot(new RedisRootKeyTreeItem(this));
        super.setShowRoot(false);
        super.initRoot();
    }

    @Override
    public RedisKeyTreeItemFilter itemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null && this.dbItem != null) {
            RedisKeyTreeItemFilter filter = new RedisKeyTreeItemFilter();
            filter.initFilters(this.client().iid());
            this.itemFilter = filter;
        }
        return (RedisKeyTreeItemFilter) this.itemFilter;
    }

    @Override
    public RedisRootKeyTreeItem getRoot() {
        return (RedisRootKeyTreeItem) super.getRoot();
    }

    /**
     * 键添加事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyAdded(RedisKeyAddedEvent event) {
        if (event.data() == this.redisConnect() && event.dbIndex() == this.dbIndex()) {
            this.getRoot().keyAdded(event.key());
        }
    }

    /**
     * 键删除事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyDeleted(RedisKeyDeletedEvent event) {
        if (event.data() == this.redisConnect() && event.dbIndex() == this.dbIndex()) {
            this.getRoot().keyDeleted(event.key());
        }
    }

    /**
     * 键刷新事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyFlushed(RedisKeyFlushedEvent event) {
        if (event.data() == this.dbItem) {
            this.loadItems();
        }
    }

    /**
     * 键过滤事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyFiltered(RedisKeyFilteredEvent event) {
        if (event.data() == this.dbItem) {
            this.loadItems();
        }
    }

    /**
     * 键复制事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyCopied(RedisKeyCopiedEvent event) {
        int dbIndex = event.targetDB();
        if (dbIndex == this.dbIndex()) {
            this.loadItems();
        }
    }

    /**
     * 键移动事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onKeyMoved(RedisKeyMovedEvent event) {
        // 检查连接
        if (event.redisConnect() != this.redisConnect()) {
            return;
        }
        // 目标库刷新节点
        if (event.targetDB() == this.dbIndex()) {
            this.loadItems();
        } else if (event.sourceDB() == this.dbIndex()) {// 来源库，移除此节点
            event.data().remove();
        }
    }

    /**
     * 多个键移动事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onKeysMoved(RedisKeysMovedEvent event) {
        // 检查连接
        if (event.redisConnect() != this.redisConnect()) {
            return;
        }
        // 来源库、目标库刷新节点
        if (event.targetDB() == this.dbIndex() || event.sourceDB() == this.dbIndex()) {
            this.loadItems();
        }
    }

    // /**
    //  * 树节点过滤
    //  */
    // @EventSubscribe
    // private void treeChildFilter(TreeChildFilterEvent event) {
    //     this.filter();
    // }

    /**
     * 加载节点
     */
    public void loadItems() {
        this.disable();
        try {
            this.getRoot().loadChild();
        } finally {
            this.enable();
        }
    }

    @Override
    public synchronized void sortAsc() {
        RichTreeItem<?> item = this.getSelectedItem();
        this.getRoot().sortAsc();
        if (item != null) {
            this.select(item);
        }
        this.refresh();
    }

    @Override
    public synchronized void sortDesc() {
        RichTreeItem<?> item = this.getSelectedItem();
        this.getRoot().sortDesc();
        if (item != null) {
            this.select(item);
        }
        this.refresh();
    }
}
