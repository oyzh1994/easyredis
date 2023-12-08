package cn.oyzh.easyredis.trees;

import cn.hutool.extra.spring.SpringUtil;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisKeyAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyDeletedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyFlushedMsg;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.root.RedisRootTreeItem;
import cn.oyzh.easyredis.trees.server.RedisServerInfoTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.trees.RichTreeView;
import cn.oyzh.fx.plus.util.MouseUtil;
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
//@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisTreeView extends RichTreeView {

    // /**
    //  * 排序方式
    //  */
    // @Getter
    // @Setter
    // private Boolean sortOrder;

    // /**
    //  * 键过滤器
    //  */
    // @Getter
    // @Setter
    // private RedisTreeItemFilter itemFilter;

    // /**
    //  * 导入中标志位
    //  */
    // private volatile boolean importing;

    /**
     * 搜索中标志位
     */
    @Getter
    private volatile boolean searching;

    // /**
    //  * 子节点变化处理
    //  */
    // @Setter
    // @Getter
    // private Runnable childChanged;

    // /**
    //  * 图标变化处理
    //  */
    // @Setter
    // @Getter
    // private Consumer<TreeItem<?>> graphicChanged;

    // /**
    //  * 连接关闭处理
    //  */
    // @Setter
    // @Getter
    // private Consumer<RedisConnectTreeItem> connectClosed;
    //
    // /**
    //  * 连接完成处理
    //  */
    // @Setter
    // @Getter
    // private Consumer<RedisConnectTreeItem> connectConnected;

    // /**
    //  * 配置储存对象
    //  */
    // private final RedisSetting setting = RedisSettingStore.SETTING;

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

    // /**
    //  * 触发子节点变化事件
    //  */
    // public void fireChildChanged() {
    //     if (this.childChanged != null) {
    //         try {
    //             this.childChanged.run();
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }

    // /**
    //  * 触发图标变化事件
    //  */
    // public void fireGraphicChanged(@NonNull TreeItem<?> item) {
    //     if (this.graphicChanged != null) {
    //         try {
    //             this.graphicChanged.accept(item);
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }

    // /**
    //  * 触发连接关闭事件
    //  */
    // public void fireConnectClosed(@NonNull RedisConnectTreeItem item) {
    //     if (this.connectClosed != null) {
    //         try {
    //             this.connectClosed.accept(item);
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }
    //
    // /**
    //  * 触发连接完成事件
    //  */
    // public void fireConnectConnected(@NonNull RedisConnectTreeItem item) {
    //     if (this.connectConnected != null) {
    //         try {
    //             this.connectConnected.accept(item);
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }

    public RedisTreeView() {
        this.dragContent = "redis_tree_drag";
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RedisTreeCell());
        // 初始化事件处理
        this.initEventHandler();
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
            ThreadUtil.startVirtual(treeItem::disConnect);
        }
    }

    // /**
    //  * 获取窗口
    //  *
    //  * @return 窗口
    //  */
    // public Window window() {
    //     return this.getScene().getWindow();
    // }

    // @Override
    // public void selectAndScroll(TreeItem<?> item) {
    //     if (item != null) {
    //         super.selectAndScroll(item);
    //     } else {
    //         this.clearSelection();
    //     }
    // }

    /**
     * 初始化事件处理器
     */
    protected void initEventHandler() {
        // 主鼠标按钮点击事件
        super.setOnMousePrimaryClicked(e -> {
            TreeItem<?> item = this.getSelectedItem();
            if (MouseUtil.isSingleClick(e)) {
                this.clearContextMenu();
                if (item instanceof RedisServerInfoTreeItem serverInfoTreeItem) {
                    serverInfoTreeItem.showServerInfo();
                }
            } else {
                if (item instanceof RedisConnectTreeItem treeItem) {
                    treeItem.connect();
                }
            }
        });
        // 右键菜单事件
        this.setOnContextMenuRequested(e -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisTreeItem treeItem) {
                this.showContextMenu(treeItem.getMenuItems(), e.getScreenX() - 10, e.getScreenY() - 10);
            } else {
                this.clearContextMenu();
            }
        });
        // f2按键处理
        KeyListener.listenReleased(this, KeyCode.F2, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisTreeItem treeItem) {
                treeItem.rename();
            }
        });
        // 删除按键处理
        KeyListener.listenReleased(this, KeyCode.DELETE, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisTreeItem<?> treeItem) {
                treeItem.delete();
            }
        });
        // 暂停按键处理
        KeyListener.listenReleased(this, KeyCode.PAUSE, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof RedisConnectTreeItem treeItem) {
                treeItem.disConnect();
            } else if (item instanceof RedisKeyTreeItem<?, ?> nodeTreeItem) {
                nodeTreeItem.connectTreeItem().disConnect();
            }
        });
    }

    // /**
    //  * 对键排序
    //  *
    //  * @param sortOrder 排序方式
    //  */
    // public void sortItem(Boolean sortOrder) {
    //     this.sortOrder = sortOrder;
    //     if (sortOrder != null) {
    //         // 获取选中键
    //         TreeItem<?> item = this.getSelectedItem();
    //         // 执行排序
    //         if (item instanceof RedisTreeItem treeItem) {
    //             treeItem.sort(sortOrder);
    //         }
    //         // 重新选中此键
    //         this.select(item);
    //     }
    // }

    // /**
    //  * 过滤键
    //  */
    // public void filterItem() {
    //     // 获取选中键
    //     TreeItem<?> item = this.getSelectedItem();
    //     // 清除选中键
    //     this.clearSelection();
    //     // 执行过滤
    //     this.root().filter(this.itemFilter);
    //     // 选中并滚动键
    //     this.selectAndScroll(item);
    // }

    /**
     * 重新载入
     */
    public void reload() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof RedisKeyTreeItem treeItem) {
        }
    }

    /**
     * 键添加事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_ADDED, verbose = true, async = true)
    private void onKeyAdded(RedisKeyAddedMsg msg) {
        if (msg != null && msg.item() != null) {
            msg.item().reloadChild();
        }
    }

    /**
     * 键删除事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_DELETED, verbose = true, async = true)
    private void onKeyDeleted(RedisKeyDeletedMsg msg) {
        if (msg != null && msg.item() != null) {
            msg.item().reloadChild();
        }
    }

    /**
     * 键刷新事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_FLUSHED, verbose = true, async = true)
    private void onKeyFlushed(RedisKeyFlushedMsg msg) {
        if (msg != null && msg.item() != null) {
            msg.item().reloadChild();
        }
    }

    /**
     * 键复制事件
     *
     * @param treeItem key树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_COPY, verbose = true, async = true)
    private void onKeyCopied(TreeItem<?> treeItem) {
        int dbIndex = this.getProp("targetDB");
        RedisConnectTreeItem connectTreeItem = null;
        if (treeItem instanceof RedisDBTreeItem dbTeeItem) {
            connectTreeItem = dbTeeItem.parent();
        } else if (treeItem instanceof RedisKeyTreeItem<?, ?> keyTreeItem) {
            connectTreeItem = keyTreeItem.connectTreeItem();
        }
        if (connectTreeItem != null) {
            RedisDBTreeItem dbTreeItem = connectTreeItem.getDatabaseItem(dbIndex);
            if (dbTreeItem != null) {
                dbTreeItem.reloadChild();
            }
        }
    }

    /**
     * 键移动事件
     *
     * @param treeItem key树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_MOVED, verbose = true, async = true)
    private void onKeyMoved(RedisKeyTreeItem<?, ?> treeItem) {
        int dbIndex = this.getProp("targetDB");
        RedisConnectTreeItem connectTreeItem = treeItem.connectTreeItem();
        treeItem.remove();
        RedisDBTreeItem dbTreeItem = connectTreeItem.getDatabaseItem(dbIndex);
        if (dbTreeItem != null) {
            dbTreeItem.reloadChild();
        }
    }

    /**
     * 搜索开始事件
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_START, async = true, verbose = true)
    private void onSearchStart() {
        this.searching = true;
        this.filter();
    }

    /**
     * 搜索结束事件
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_FINISH, async = true, verbose = true)
    private void onSearchFinish() {
        this.searching = false;
        this.filter();
    }

    /**
     * 树节点过滤
     */
    @EventReceiver(value = RedisEventTypes.TREE_CHILD_FILTER, async = true, verbose = true)
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
}
