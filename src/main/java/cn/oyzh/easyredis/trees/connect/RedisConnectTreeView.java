package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyredis.event.connect.RedisConnectAddedEvent;
import cn.oyzh.easyredis.event.connect.RedisConnectImportedEvent;
import cn.oyzh.easyredis.event.connect.RedisConnectUpdatedEvent;
import cn.oyzh.easyredis.event.group.RedisAddGroupEvent;
import cn.oyzh.easyredis.event.key.RedisKeyAddedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyDeletedEvent;
import cn.oyzh.easyredis.event.query.RedisQueryAddedEvent;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.util.List;

/**
 * redis树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class RedisConnectTreeView extends RichTreeView implements FXEventListener {

    @Override
    protected void initTreeView() {
        this.dragContent = "redis_connect_tree_drag";
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.initTreeView();
    }

    @Override
    protected void initRoot() {
        super.setRoot(new RedisRootTreeItem(this));
        this.root().expend();
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
            }
        });
    }

    @Override
    public RedisRootTreeItem root() {
        return (RedisRootTreeItem) super.root();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (RedisConnectTreeItem treeItem : this.root().getConnectedItems()) {
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
        for (RedisConnectTreeItem connectItem : this.root().getConnectItems()) {
            if (connectItem.value() == event.data()) {
                RedisDatabaseTreeItem dbItem = connectItem.getDatabaseItem(event.getDbIndex());
                if (dbItem != null) {
                    dbItem.onKeyAdded();
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
        for (RedisConnectTreeItem connectItem : this.root().getConnectItems()) {
            if (connectItem.value() == event.data()) {
                RedisDatabaseTreeItem dbItem = connectItem.getDatabaseItem(event.getDbIndex());
                if (dbItem != null) {
                    dbItem.onKeyDeleted();
                }
                break;
            }
        }
    }

//    /**
//     * 添加连接事件
//     *
//     * @param event 事件
//     */
//    @EventSubscribe
//    private void addConnect(RedisAddConnectEvent event) {
//        StageManager.showStage(RedisAddConnectController.class, this.window());
//    }

    /**
     * 添加分组事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addGroup(RedisAddGroupEvent event) {
        this.root().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void connectAdded(RedisConnectAddedEvent event) {
        this.root().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void connectUpdate(RedisConnectUpdatedEvent event) {
        this.root().connectUpdate(event.data());
    }

    /**
     * 查询已添加事件
     */
    @EventSubscribe
    private void queryAdded(RedisQueryAddedEvent event) {
        this.root().queryAdded(event.data());
    }

    /**
     * 连接已导入事件
     */
    @EventSubscribe
    private void connectImported(RedisConnectImportedEvent event) {
        this.root().reloadChild();
    }

    @Override
    public RedisConnectTreeItemFilter getItemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            this.itemFilter = new RedisConnectTreeItemFilter();
        }
        return (RedisConnectTreeItemFilter) this.itemFilter;
    }

    @Override
    public void setHighlightText(String highlightText) {
        super.setHighlightText(highlightText);
        this.getItemFilter().setKw(highlightText);
    }

    public List<RedisGroupTreeItem> getGroupItems() {
        return this.root().getGroupItems();
    }
}
