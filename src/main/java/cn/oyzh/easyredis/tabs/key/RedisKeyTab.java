package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.tabs.key.hash.RedisHashKeyTab;
import cn.oyzh.easyredis.tabs.key.list.RedisListKeyTab;
import cn.oyzh.easyredis.tabs.key.set.RedisSetKeyTab;
import cn.oyzh.easyredis.tabs.key.stream.RedisStreamKeyTab;
import cn.oyzh.easyredis.tabs.key.string.RedisStringKeyTab;
import cn.oyzh.easyredis.tabs.key.zset.RedisZSetKeyTab;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * redis 键tab
 *
 * @author oyzh
 * @since 2023/06/21
 */
public abstract class RedisKeyTab<T extends RedisKeyTreeItem<?, ?>> extends DynamicTab {

    /**
     * 标签打开时间
     */
    @Getter
    private final long openedTime = System.currentTimeMillis();

    /**
     * redis树节点
     */
    @Getter
    @Accessors(fluent = true)
    protected T treeItem;

    /**
     * 执行初始化
     *
     * @param treeItem redis树节点
     */
    public void init(@NonNull T treeItem) {
        if (treeItem != this.treeItem) {
            this.treeItem = treeItem;
            // 刷新
            this.flush();
            // 初始化
            if (!this.controller().init(treeItem)) {
                this.disable();
            }
            // 判断这个key是否到期
            if (treeItem.isExpire()) {
                BackgroundService.submitFXLater(() -> {
                    if (MessageBox.confirm("键[" + treeItem.key() + "]已过期，是否删除？")) {
                        treeItem.delete();
                    }
                });
            }
        }
    }

    @Override
    public void flushTitle() {
        // 设置文本
        this.setText(this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "-" + this.treeItem.key());
        // 设置提示文本
        this.setTipText(this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "-" + this.treeItem.key());
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/key.svg", 12);
            glyph.setCursor(Cursor.DEFAULT);
            this.setGraphic(glyph);
        }
    }

    @Override
    public RedisKeyTabContent<T> controller() {
        return (RedisKeyTabContent<T>) super.controller();
    }

//    /**
//     * 获取键数据组件
//     *
//     * @return 键数据组件
//     */
//    public FlexTextArea getNodeDataNode() {
//        return this.controller().getNodeDataNode();
//    }

    /**
     * 重新载入
     */
    public void reload() {
        this.controller().reloadNode();
    }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        this.controller().flushTTL();
    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.treeItem.client();
    }

    /**
     * 获取redis键
     *
     * @return redis键
     */
    public RedisKey key() {
        return this.treeItem.value();
    }

    public static <T extends RedisKeyTreeItem<?, ?>> RedisKeyTab<T> ofItem(T item) {
        RedisKeyTab<T> tab = null;
        if (item instanceof RedisStringKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisStringKeyTab();
        } else if (item instanceof RedisListKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisListKeyTab();
        } else if (item instanceof RedisSetKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisSetKeyTab();
        } else if (item instanceof RedisZSetKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisZSetKeyTab();
        } else if (item instanceof RedisHashKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisHashKeyTab();
        } else if (item instanceof RedisStreamKeyTreeItem) {
            tab = (RedisKeyTab<T>) new RedisStreamKeyTab();
        }
        return tab;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = super.getMenuItems();
        MenuItem closeConnectTab = FXMenuItem.newItem("关闭当前连接", "关闭当前连接标签页", this::closeConnectTab);
        MenuItem closeOtherConnectTab = FXMenuItem.newItem("关闭其他连接", "关闭其他连接标签页", this::closeOtherConnectTab);
        items.add(4, closeConnectTab);
        items.add(5, closeOtherConnectTab);
        return items;
    }

    /**
     * 关闭当前连接tab
     */
    private void closeConnectTab() {
        FXUtil.runLater(() -> {
            List<Tab> list = new ArrayList<>();
            for (Tab tab : this.tabs()) {
                if (tab instanceof RedisKeyTab<?> keyType && keyType.client() == this.treeItem.client()) {
                    list.add(tab);
                }
            }
            this.tabs().removeAll(list);
        });
    }

    /**
     * 关闭其他连接tab
     */
    private void closeOtherConnectTab() {
        FXUtil.runLater(() -> {
            List<Tab> list = new ArrayList<>();
            for (Tab tab : this.tabs()) {
                if (tab instanceof RedisKeyTab<?> keyType && keyType.client() == this.treeItem.client()) {
                    list.add(tab);
                }
            }
            this.tabs().removeAll(list);
        });
    }
}
