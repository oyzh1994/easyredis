package cn.oyzh.easyredis.tabs;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisTerminalCloseMsg;
import cn.oyzh.easyredis.event.msg.RedisTerminalOpenMsg;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.tabs.home.RedisHomeTab;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.key.hash.RedisHashKeyTabContent;
import cn.oyzh.easyredis.tabs.key.list.RedisListKeyTabContent;
import cn.oyzh.easyredis.tabs.key.set.RedisSetKeyTabContent;
import cn.oyzh.easyredis.tabs.key.stream.RedisStreamKeyTabContent;
import cn.oyzh.easyredis.tabs.key.zset.RedisZSetKeyTabContent;
import cn.oyzh.easyredis.tabs.pubsub.RedisPubsubTab;
import cn.oyzh.easyredis.tabs.server.RedisServerTab;
import cn.oyzh.easyredis.tabs.terminal.RedisTerminalTab;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.collections.ListChangeListener;
import javafx.scene.CacheHint;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;

/**
 * redis切换面板
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class RedisTabPane extends DynamicTabPane {

    {
        this.setCache(true);
        this.setCacheHint(CacheHint.QUALITY);
        this.initHomeTab();
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            TaskManager.startDelayTask("redis:tab:init", () -> {
                if (this.tabsEmpty()) {
                    this.initHomeTab();
                } else if (this.tabsSize() > 1) {
                    this.closeHomeTab();
                }
            }, 100);
        });
    }

    /**
     * 初始化终端tab
     *
     * @param info redis信息
     */
    public void initTerminalTab(RedisInfo info) {
        RedisTerminalTab terminalTab = this.getTerminalTab(info);
        if (terminalTab == null) {
            terminalTab = new RedisTerminalTab();
            terminalTab.init(info);
            super.addTab(terminalTab);
        } else {
            terminalTab.flushGraphic();
        }
        if (!terminalTab.isSelected()) {
            this.select(terminalTab);
        }
    }

    /**
     * 终端打开事件
     *
     * @param event 事件
     */
    @EventReceiver(value = RedisEventTypes.REDIS_OPEN_TERMINAL, async = true, verbose = true, fxThread = true)
    private void openTerminal(Event<RedisTerminalOpenMsg> event) {
        this.initTerminalTab(event.data().info());
    }

    /**
     * 终端关闭事件
     *
     * @param event 事件
     */
    @EventReceiver(value = RedisEventTypes.REDIS_CLOSE_TERMINAL, async = true, verbose = true, fxThread = true)
    private void closeTerminal(Event<RedisTerminalCloseMsg> event) {
        try {
            // 寻找节点
            RedisTerminalTab terminalTab = this.getTerminalTab(event.data().info());
            // 移除节点
            if (terminalTab != null) {
                terminalTab.closeTab();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取终端tab
     *
     * @param info redis信息
     * @return 终端tab
     */
    private RedisTerminalTab getTerminalTab(RedisInfo info) {
        if (info != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisTerminalTab cmdTab && cmdTab.info() == info) {
                    return cmdTab;
                }
            }
        }
        return null;
    }

    /**
     * 初始化发布及订阅tab
     *
     * @param item 发布及订阅节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_OPEN_PUBSUB, verbose = true, async = true, fxThread = true)
    public void initPubsubTab(RedisPubsubItem item) {
        RedisPubsubTab tab = this.getPubsubTab(item);
        if (tab == null) {
            tab = new RedisPubsubTab();
            tab.init(item);
            super.addTab(tab);
        } else {
            tab.flushGraphic();
        }
        if (!tab.isSelected()) {
            this.select(tab);
        }
    }

    /**
     * 获取发布及订阅tab
     *
     * @param item 发布及订阅节点
     * @return 发布及订阅tab
     */
    private RedisPubsubTab getPubsubTab(RedisPubsubItem item) {
        if (item != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisPubsubTab cmdTab && cmdTab.item() == item) {
                    return cmdTab;
                }
            }
        }
        return null;
    }

    /**
     * 初始化服务信息tab
     *
     * @param client redis客户端
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SERVER_INFO, verbose = true, async = true, fxThread = true)
    public void initServerTab(RedisClient client) {
        RedisServerTab serverTab = this.getServerTab(client);
        if (serverTab == null) {
            serverTab = new RedisServerTab();
            serverTab.init(client);
            super.addTab(serverTab);
        } else {
            serverTab.flushGraphic();
        }
        if (!serverTab.isSelected()) {
            this.select(serverTab);
        }
    }

    /**
     * 获取服务信息tab
     *
     * @param client redis客户端
     * @return 服务信息tab
     */
    private RedisServerTab getServerTab(RedisClient client) {
        if (client != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisServerTab serverTab && serverTab.info() == client.redisInfo()) {
                    return serverTab;
                }
            }
        }
        return null;
    }

    /**
     * 获取主页tab
     *
     * @return 主页tab
     */
    public RedisHomeTab getHomeTab() {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisHomeTab homeTab) {
                return homeTab;
            }
        }
        return null;
    }

    /**
     * 初始化主页tab
     */
    public void initHomeTab() {
        if (this.getHomeTab() == null) {
            super.addTab(new RedisHomeTab());
        }
    }

    /**
     * 关闭主页tab
     */
    public void closeHomeTab() {
        RedisHomeTab homeTab = this.getHomeTab();
        if (homeTab != null) {
            super.removeTab(homeTab);
        }
    }

    /**
     * 获取键tab
     *
     * @return 键tab
     */
    public RedisKeyTab<?> getKeyTab() {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisKeyTab<?> nodeTab) {
                return nodeTab;
            }
        }
        return null;
    }

    /**
     * 初始化键tab
     *
     * @param item redis树节点
     */
    public void initKeyTab(RedisKeyTreeItem<?> item) {
        if (item != null) {
            RedisKeyTab<?> nodeTab = this.getKeyTab();
            if (nodeTab != null && nodeTab.treeItem() != item) {
                nodeTab.closeTab();
                nodeTab = null;
            }
            if (nodeTab == null) {
                nodeTab = RedisKeyTab.ofItem(item);
                super.addTab(nodeTab);
            } else {
                nodeTab.flushGraphic();
            }
            if (!nodeTab.isSelected()) {
                this.select(nodeTab);
            }
            // 延迟判断下，这个key是否到期
            ExecutorUtil.start(() -> {
                if (item.isExpire()) {
                    FXUtil.runLater(() -> {
                        if (MessageBox.confirm("键[" + item.key() + "]已过期，是否删除？")) {
                            item.delete();
                        }
                    });
                }
            }, 1);
        }
    }

    // public void flushGraphic() {
    // }

    /**
     * 键更名事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_CHANGE_ZSET_SHOW_TYPE, verbose = true, async = true)
    private void changeZETShowType(RedisZSetKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            tab.closeTab();
            this.initKeyTab(treeItem);
        }
    }

    /**
     * list行添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_LIST_ROW_ADDED, verbose = true, async = true)
    private void onListRowAdded(RedisListKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisListKeyTabContent controller = (RedisListKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * set成员添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SET_MEMBER_ADDED, verbose = true, async = true)
    private void onSetMemberAdded(RedisSetKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisSetKeyTabContent controller = (RedisSetKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * zset成员添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_ZSET_MEMBER_ADDED, verbose = true, async = true)
    private void onZSetMemberAdded(RedisZSetKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisZSetKeyTabContent controller = (RedisZSetKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * geo坐标添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_GEO_COORDINATE_ADDED, verbose = true, async = true)
    private void onGEOCoordinateAdded(RedisZSetKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisZSetKeyTabContent controller = (RedisZSetKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * stream消息添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_STREAM_MESSAGE_ADDED, verbose = true, async = true)
    private void onStreamMessageAdded(RedisStreamKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisStreamKeyTabContent controller = (RedisStreamKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * hash字段添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_HASH_FIELD_ADDED, verbose = true, async = true)
    private void onHashFieldAdded(RedisHashKeyTreeItem treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            RedisHashKeyTabContent controller = (RedisHashKeyTabContent) tab.controller();
            treeItem.refreshNodeValue();
            controller.firstPage();
        }
    }

    /**
     * hylog元素添加事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_HYPER_LOG_LOG_ELEMENT_ADDED, verbose = true, async = true, fxThread = true)
    private void onHyperLogLogElementAdded(RedisKeyTreeItem<?> treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            tab.reload();
        }
    }

    /**
     * 键更名事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_RENAMED, verbose = true, async = true, fxThread = true)
    private void onKeyRenamed(RedisKeyTreeItem<?> treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            tab.flushGraphic();
        }
    }

    /**
     * ttl更新事件
     *
     * @param treeItem redis树节点
     */
    @EventReceiver(value = RedisEventTypes.REDIS_TTL_UPDATED, verbose = true, async = true, fxThread = true)
    private void onTTLUpdated(RedisKeyTreeItem<?> treeItem) {
        RedisKeyTab<?> tab = this.getKeyTab();
        if (tab != null && tab.treeItem() == treeItem) {
            tab.flushTTL();
        }
    }

    /**
     * redis客户端关闭事件
     *
     * @param client redis客户端
     */
    @EventReceiver(value = RedisEventTypes.REDIS_CLINE_CLOSED, verbose = true, async = true)
    private void onClientClosed(RedisClient client) {
        List<Tab> closeTabs = new ArrayList<>();
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisServerTab serverTab && serverTab.client() == client) {
                serverTab.closeRefreshTask();
                closeTabs.add(tab);
            } else if (tab instanceof RedisPubsubTab pubsubTab && pubsubTab.client() == client) {
                pubsubTab.unsubscribe();
                closeTabs.add(tab);
            } else if (tab instanceof RedisKeyTab<?> keyTab && keyTab.client() == client) {
                closeTabs.add(tab);
            }
        }
        if (!closeTabs.isEmpty()) {
            FXUtil.runLater(() -> this.getTabs().removeAll(closeTabs));
        }
    }
}
