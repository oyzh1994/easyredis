package cn.oyzh.easyredis.tabs;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.event.RedisClientClosedEvent;
import cn.oyzh.easyredis.event.RedisFilterMainEvent;
import cn.oyzh.easyredis.event.RedisKeyRenamedEvent;
import cn.oyzh.easyredis.event.RedisKeyTTLUpdatedEvent;
import cn.oyzh.easyredis.event.RedisPubsubOpenEvent;
import cn.oyzh.easyredis.event.RedisServerMonitorEvent;
import cn.oyzh.easyredis.event.RedisTerminalCloseEvent;
import cn.oyzh.easyredis.event.RedisTerminalOpenEvent;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.tabs.filter.RedisFilterTab;
import cn.oyzh.easyredis.tabs.home.RedisHomeTab;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.pubsub.RedisPubsubTab;
import cn.oyzh.easyredis.tabs.server.RedisServerTab;
import cn.oyzh.easyredis.tabs.terminal.RedisTerminalTab;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.util.FXUtil;
import com.google.common.eventbus.Subscribe;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

/**
 * redis切换面板
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class RedisTabPane extends DynamicTabPane implements EventListener {

    @Override
    protected void initTabPane() {
        super.initTabPane();
        this.initHomeTab();
        // 监听tab
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    TaskManager.startDelay("redis:homeTab:flush", this::flushHomeTab, 100);
                    if (c.wasAdded()) {
                        TaskManager.startDelay("redis:nodeTab:flush", this::flushNodeTab, 100);
                    }
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

    /**
     * 刷新节点标签
     */
    private void flushNodeTab() {
        // 获取设置
        RedisSetting setting = RedisSettingStore.SETTING;
        // 判断是否需要处理tab限制
        if (setting.isTabUnLimit()) {
            return;
        }
        // 获取全部节点tab
        List<RedisKeyTab<?>> tabs = this.getKeyTabs();
        // 数据不满足限制要求，则直接忽略
        if (tabs.size() <= setting.getTabLimit()) {
            return;
        }
        // tab处理函数
        Consumer<List<RedisKeyTab<?>>> func = tabList -> {
            // 数据满足限制要求才处理
            if (tabList.size() > setting.getTabLimit()) {
                // 进行排序
                tabList.sort((o1, o2) -> Comparator.comparingLong((ToLongFunction<RedisKeyTab<?>>) RedisKeyTab::getOpenedTime).compare(o2, o1));
                // 跳过指定数量
                List<RedisKeyTab<?>> list = tabList.stream().skip(setting.getTabLimit()).toList();
                // 移除tab
                if (!list.isEmpty()) {
                    FXUtil.runLater(() -> this.getTabs().removeAll(list));
                }
            }
        };
        // 限制全部连接
        if (setting.isAllTabLimitStrategy()) {
            func.accept(tabs);
        } else if (setting.isSingleTabLimitStrategy()) {// 限制单个连接
            // 分组处理
            Map<RedisClient, List<RedisKeyTab<?>>> map = new HashMap<>();
            // 按分组添加到map
            for (RedisKeyTab<?> tab : tabs) {
                List<RedisKeyTab<?>> list = map.computeIfAbsent(tab.client(), k -> new ArrayList<>());
                list.add(tab);
            }
            // 处理值
            for (List<RedisKeyTab<?>> tabList : map.values()) {
                func.accept(tabList);
            }
        }
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
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_OPEN_TERMINAL, async = true, verbose = true, fxThread = true)
    @Subscribe
    private void openTerminal(RedisTerminalOpenEvent msg) {
        this.initTerminalTab(msg.data());
    }

    /**
     * 终端关闭事件
     *
     * @param event 事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_CLOSE_TERMINAL, async = true, verbose = true, fxThread = true)
    @Subscribe
    private void closeTerminal(RedisTerminalCloseEvent event) {
        try {
            // 寻找节点
            RedisTerminalTab terminalTab = this.getTerminalTab(event.data());
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
     * @param event 事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_OPEN_PUBSUB, verbose = true, async = true, fxThread = true)
    @Subscribe
    public void initPubsubTab(RedisPubsubOpenEvent event) {
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
     * 初始化服务信息tab
     *
     * @param event 事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_SERVER_INFO, verbose = true, async = true, fxThread = true)
    @Subscribe
    public void initServerTab(RedisServerMonitorEvent event) {
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
     * @param item 树节点
     * @return 键tab
     */
    public RedisKeyTab<?> getKeyTab(TreeItem<?> item) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisKeyTab<?> nodeTab && nodeTab.treeItem() == item) {
                return nodeTab;
            }
        }
        return null;
    }

    /**
     * 获取键tab列表
     *
     * @return 键tab列表
     */
    public List<RedisKeyTab<?>> getKeyTabs() {
        List<RedisKeyTab<?>> list = new ArrayList<>();
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RedisKeyTab<?> nodeTab) {
                list.add(nodeTab);
            }
        }
        return list;
    }

    /**
     * 初始化键tab
     *
     * @param item redis树节点
     */
    public void initKeyTab(RedisKeyTreeItem<?, ?> item) {
        if (item != null) {
            RedisKeyTab keyTab = this.getKeyTab(item);
            if (keyTab == null) {
                keyTab = RedisKeyTab.ofItem(item);
                super.addTab(keyTab);
            }
            // 选中节点
            this.select(keyTab);
            // 初始化节点
            keyTab.init(item);
        }
    }

    /**
     * 键更名事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_RENAMED, verbose = true, async = true, fxThread = true)
    @Subscribe
    private void onKeyRenamed(RedisKeyRenamedEvent msg) {
        RedisKeyTab<?> tab = this.getKeyTab(msg.data());
        if (tab != null && tab.treeItem() == msg.data()) {
            tab.flushGraphic();
            tab.flushTitle();
        }
    }

    /**
     * ttl更新事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_KEY_TTL_UPDATED, verbose = true, async = true, fxThread = true)
    @Subscribe
    private void onTTLUpdated(RedisKeyTTLUpdatedEvent msg) {
        RedisKeyTab<?> tab = this.getKeyTab(msg.data());
        if (tab != null) {
            tab.flushTTL();
        }
    }

    /**
     * redis客户端关闭事件
     *
     * @param event 事件
     */
    // @EventReceiver(value = RedisEventTypes.REDIS_CLINE_CLOSED, verbose = true, async = true)
    @Subscribe
    private void onClientClosed(RedisClientClosedEvent event) {
        RedisClient client= event.data();
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

    /**
     * 获取过滤tab
     *
     * @return 过滤tab
     */
    public RedisFilterTab getFilterTab() {
        return super.getTab(RedisFilterTab.class);
    }

    /**
     * 初始化过滤tab
     */
    @Subscribe
    public void initFilterTab(RedisFilterMainEvent msg) {
        RedisFilterTab tab = this.getFilterTab();
        if (tab == null) {
            tab = new RedisFilterTab();
            super.addTab(tab);
        }
        this.select(tab);
    }
}
