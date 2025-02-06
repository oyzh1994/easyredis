package cn.oyzh.easyredis.tabs;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.dto.RedisPubsubItem;
import cn.oyzh.easyredis.event.connect.RedisConnectOpenedEvent;
import cn.oyzh.easyredis.event.connection.RedisConnectionClosedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyTTLUpdatedEvent;
import cn.oyzh.easyredis.event.key.RedisPubsubOpenEvent;
import cn.oyzh.easyredis.event.connection.RedisServerEvent;
import cn.oyzh.easyredis.event.query.RedisAddQueryEvent;
import cn.oyzh.easyredis.event.query.RedisOpenQueryEvent;
import cn.oyzh.easyredis.event.query.RedisQueryDeletedEvent;
import cn.oyzh.easyredis.event.query.RedisQueryRenamedEvent;
import cn.oyzh.easyredis.event.terminal.RedisTerminalCloseEvent;
import cn.oyzh.easyredis.event.terminal.RedisTerminalOpenEvent;
import cn.oyzh.easyredis.event.key.RedisZSetReverseViewEvent;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.tabs.changelog.ChangelogTab;
import cn.oyzh.easyredis.tabs.home.RedisHomeTab;
import cn.oyzh.easyredis.tabs.key.RedisKeysTab;
import cn.oyzh.easyredis.tabs.pubsub.RedisPubsubTab;
import cn.oyzh.easyredis.tabs.query.RedisQueryTab;
import cn.oyzh.easyredis.tabs.server.RedisServerTab;
import cn.oyzh.easyredis.tabs.terminal.RedisTerminalTab;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * redis切换面板
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class RedisTabPane extends DynamicTabPane implements FXEventListener {

    @Override
    public void onNodeInitialize() {
        if (!FXEventListener.super.isNodeInitialize()) {
            FXEventListener.super.onNodeInitialize();
            // 刷新触发事件
            KeyListener.listenReleased(this, KeyCode.F5, keyEvent -> this.reload());
        }
    }

    @Override
    public void onNodeDestroy() {
        FXEventListener.super.onNodeDestroy();
        KeyListener.unListenReleased(this, KeyCode.F5);
    }

    @Override
    protected void initTabPane() {
        super.initTabPane();
        this.initHomeTab();
        // 监听tab
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    TaskManager.startDelay("redis:homeTab:flush", this::flushHomeTab, 100);
                }
            }
        });
    }

    /**
     * 刷新主页标签
     */
    private void flushHomeTab() {
        if (this.tabsEmpty()) {
            this.initHomeTab();
        } else if (this.tabsSize() > 1) {
            this.closeHomeTab();
        }
    }

//    /**
//     * 初始化终端tab
//     *
//     * @param redisConnect redis信息
//     */
//    public void initTerminalTab(RedisConnect redisConnect, Integer dbIndex) {
//        RedisTerminalTab terminalTab = this.getTerminalTab(redisConnect, dbIndex);
//        if (terminalTab == null) {
//            terminalTab = new RedisTerminalTab(redisConnect,dbIndex);
//            terminalTab.init(redisConnect, dbIndex);
//            super.addTab(terminalTab);
//        } else {
//            terminalTab.flushGraphic();
//        }
//        if (!terminalTab.isSelected()) {
//            this.select(terminalTab);
//        }
//    }

    /**
     * 终端打开事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void terminalOpen(RedisTerminalOpenEvent event) {
        RedisTerminalTab terminalTab = this.getTerminalTab(event.data(), event.dbIndex());
        if (terminalTab == null) {
            terminalTab = new RedisTerminalTab(event.data(), event.dbIndex());
            super.addTab(terminalTab);
        } else {
            terminalTab.flushGraphic();
        }
        if (!terminalTab.isSelected()) {
            this.select(terminalTab);
        }
    }

    /**
     * 终端关闭事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void terminalClose(RedisTerminalCloseEvent event) {
        try {
            // 寻找节点
            RedisTerminalTab terminalTab = this.getTerminalTab(event.data(), event.dbIndex());
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
     * @param client  redis客户端
     * @param dbIndex db索引
     * @return 终端tab
     */
    private RedisTerminalTab getTerminalTab(RedisClient client, Integer dbIndex) {
        if (client != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisTerminalTab tab1 && tab1.client() == client && Objects.equals(tab1.dbIndex(), dbIndex)) {
                    return tab1;
                }
            }
        }
        return null;
    }

    /**
     * 获取终端tab
     *
     * @param redisConnect redis信息
     * @return 终端tab
     */
    private RedisTerminalTab getTerminalTab(RedisConnect redisConnect, Integer dbIndex) {
        if (redisConnect != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisTerminalTab tab1 && tab1.redisConnect() == redisConnect
                        && Objects.equals(tab1.dbIndex(), dbIndex)) {
                    return tab1;
                }
            }
        }
        return null;
    }

    /**
     * 初始化发布及订阅tab
     *
     * @param event 事件
     */
    @EventSubscribe
    public void pubsubOpen(RedisPubsubOpenEvent event) {
        RedisPubsubTab tab = this.getPubsubTab(event.data());
        if (tab == null) {
            tab = new RedisPubsubTab();
            tab.init(event.data());
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
     * 服务信息
     *
     * @param event 事件
     */
    @EventSubscribe
    public void server(RedisServerEvent event) {
        RedisServerTab serverTab = this.getServerTab(event.data());
        if (serverTab == null) {
            serverTab = new RedisServerTab();
            serverTab.init(event.data());
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
                if (tab instanceof RedisServerTab serverTab && serverTab.redisConnect() == client.redisConnect()) {
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

    private RedisKeysTab getKeysTab(RedisDatabaseTreeItem treeItem) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisKeysTab tab1 && tab1.treeItem() == treeItem) {
                return tab1;
            }
        }
        return null;
    }

    private RedisKeysTab getKeysTab(RedisConnect connect, int dbIndex) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisKeysTab tab1 && tab1.redisConnect() == connect && tab1.dbIndex() == dbIndex) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 打开连接事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void connectOpened(RedisConnectOpenedEvent event) {
        if (event != null && event.data() != null) {
            RedisKeysTab connectTab = this.getKeysTab(event.data());
            if (connectTab == null) {
                connectTab = new RedisKeysTab(event.data());
                super.addTab(connectTab);
            }
            this.select(connectTab);
        }
    }

    /**
     * zset反转视图事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void onRedisZSetReverseView(RedisZSetReverseViewEvent event) {
        RedisKeysTab keysTab = this.getKeysTab(event.dbItem());
        if (keysTab != null) {
            keysTab.flushData();
            this.select(keysTab);
        }
    }

    /**
     * ttl更新事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void ttlUpdated(RedisKeyTTLUpdatedEvent event) {
        RedisKeysTab tab = this.getKeysTab(event.data(), event.dbIndex());
        if (tab != null) {
            tab.flushTTL();
        }
    }

    /**
     * redis客户端关闭事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void connectionClosed(RedisConnectionClosedEvent event) {
        RedisClient client = event.data();
        List<Tab> tabs = new ArrayList<>(this.getTabs());
        for (Tab tab : tabs) {
            if (tab instanceof RedisServerTab tab1 && tab1.redisConnect() == event.redisConnect()) {
                tab1.closeTab();
            } else if (tab instanceof RedisPubsubTab tab1 && tab1.client() == client) {
                tab1.closeTab();
            } else if (tab instanceof RedisKeysTab tab1 && tab1.redisConnect() == event.redisConnect()) {
                tab1.closeTab();
            } else if (tab instanceof RedisTerminalTab tab1 && tab1.redisConnect() == event.redisConnect()) {
                tab1.closeTab();
            }
        }
    }

    /**
     * 更新日志事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void changelog(ChangelogEvent event) {
        ChangelogTab tab = this.getTab(ChangelogTab.class);
        if (tab == null) {
            tab = new ChangelogTab();
            super.addTab(tab);
        }
        if (!tab.isSelected()) {
            this.select(tab);
        }
    }

    /**
     * 获取查询tab
     *
     * @param query 查询
     * @return 查询tab
     */
    private RedisQueryTab getQueryTab(RedisQuery query) {
        if (query != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof RedisQueryTab queryTab && queryTab.query() == query) {
                    return queryTab;
                }
            }
        }
        return null;
    }

    /**
     * 添加查询
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addQuery(RedisAddQueryEvent event) {
        RedisQueryTab queryTab = new RedisQueryTab(event.data(), null);
        super.addTab(queryTab);
        this.select(queryTab);
    }

    /**
     * 打开查询
     *
     * @param event 事件
     */
    @EventSubscribe
    public void openQuery(RedisOpenQueryEvent event) {
        RedisQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab == null) {
            queryTab = new RedisQueryTab(event.getClient(), event.data());
            super.addTab(queryTab);
        }
        if (!queryTab.isSelected()) {
            this.select(queryTab);
        }
    }

    /**
     * 查询更名
     *
     * @param event 事件
     */
    @EventSubscribe
    public void queryRenamed(RedisQueryRenamedEvent event) {
        RedisQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab != null) {
            queryTab.flushTitle();
        }
    }

    /**
     * 查询删除
     *
     * @param event 事件
     */
    @EventSubscribe
    public void queryDeleted(RedisQueryDeletedEvent event) {
        RedisQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab != null) {
            queryTab.closeTab();
        }
    }
}
