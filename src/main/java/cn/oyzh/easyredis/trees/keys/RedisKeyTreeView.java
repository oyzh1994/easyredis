package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.event.RedisKeyAddedEvent;
import cn.oyzh.easyredis.event.RedisKeyCopiedEvent;
import cn.oyzh.easyredis.event.RedisKeyDeletedEvent;
import cn.oyzh.easyredis.event.RedisKeyFlushedEvent;
import cn.oyzh.easyredis.event.RedisKeyMovedEvent;
import cn.oyzh.easyredis.event.TreeChildFilterEvent;
import cn.oyzh.easyredis.trees.RedisTreeCell;
import cn.oyzh.easyredis.trees.RedisTreeItemFilter;
import cn.oyzh.event.EventListener;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import lombok.experimental.Accessors;

/**
 * redis树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class RedisKeyTreeView extends RichTreeView implements EventListener {

    public RedisKeyTreeView() {
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RedisTreeCell());
        super.setShowRoot(false);
    }

    @Override
    public RedisTreeItemFilter itemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            RedisTreeItemFilter filter = new RedisTreeItemFilter();
            filter.initFilters();
            this.itemFilter = filter;
        }
        return (RedisTreeItemFilter) this.itemFilter;
    }

    @Override
    public RedisKeyTreeItem getRoot() {
        return (RedisKeyTreeItem) super.getRoot();
    }

    /**
     * 键添加事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyAdded(RedisKeyAddedEvent event) {
        if (event != null && event.data() != null) {
            event.data().onKeyAdded(event.key());
        }
    }

    /**
     * 键删除事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyDeleted(RedisKeyDeletedEvent event) {
        if (event != null && event.data() != null) {
            event.data().onKeyDeleted(event.key());
        }
    }

    /**
     * 键刷新事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyFlushed(RedisKeyFlushedEvent event) {
        if (event != null && event.data() != null) {
            event.data().reloadChild();
        }
    }

    /**
     * 键复制事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void keyCopied(RedisKeyCopiedEvent event) {
    }

    /**
     * 键移动事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onKeyMoved(RedisKeyMovedEvent event) {
    }

    /**
     * 树节点过滤
     */
    @EventSubscribe
    private void treeChildFilter(TreeChildFilterEvent event) {
        this.itemFilter().initFilters();
        this.filter();
    }

}
