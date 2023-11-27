package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.tabs.key.hash.RedisHashKeyTab;
import cn.oyzh.easyredis.tabs.key.hylog.RedisHyLogKeyTab;
import cn.oyzh.easyredis.tabs.key.list.RedisListKeyTab;
import cn.oyzh.easyredis.tabs.key.set.RedisSetKeyTab;
import cn.oyzh.easyredis.tabs.key.stream.RedisStreamKeyTab;
import cn.oyzh.easyredis.tabs.key.string.RedisStringKeyTab;
import cn.oyzh.easyredis.tabs.key.zset.RedisZSetKeyTab;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.hylog.RedisHyLogKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * redis 键tab
 *
 * @author oyzh
 * @since 2023/06/21
 */
public abstract class RedisKeyTab<T extends RedisKeyTreeItem<?, ?>> extends DynamicTab {

    /**
     * redis树节点
     */
    @Getter
    @Accessors(fluent = true)
    protected final T treeItem;

    /**
     * 执行初始化
     *
     * @param treeItem redis树键
     */
    public RedisKeyTab(@NonNull T treeItem) {
        this.setClosable(true);
        this.treeItem = treeItem;
        // 初始化
        if (!this.controller().init(treeItem)) {
            this.disable();
        }
        // 刷新图标
        this.flushGraphic();
        this.setOnCloseRequest(event -> {
            // 取消当前键的选中
            if (this.treeItem.getTreeView().getSelectedItem() == this.treeItem) {
                this.treeItem.getTreeView().select(this.treeItem.connectTreeItem());
            }
        });
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/treeNode.svg", 12);
        }
        if (graphic.getCursor() != Cursor.DEFAULT) {
            graphic.setCursor(Cursor.DEFAULT);
        }
        // 设置文本
        this.setText("（" + this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "）" + this.treeItem.key());
        // 设置提示文本
        this.setTipText("（" + this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "）" + this.treeItem.key());
    }

    @Override
    public RedisKeyTabContent<T> controller() {
        return (RedisKeyTabContent<T>) super.controller();
    }

    /**
     * 获取键数据组件
     *
     * @return 键数据组件
     */
    public FlexTextArea getNodeDataNode() {
        return this.controller().getNodeDataNode();
    }

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
        if (item instanceof RedisStringKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisStringKeyTab(treeItem);
        } else if (item instanceof RedisListKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisListKeyTab(treeItem);
        } else if (item instanceof RedisSetKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisSetKeyTab(treeItem);
        } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisZSetKeyTab(treeItem);
        } else if (item instanceof RedisHashKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisHashKeyTab(treeItem);
        } else if (item instanceof RedisHyLogKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisHyLogKeyTab(treeItem);
        } else if (item instanceof RedisStreamKeyTreeItem treeItem) {
            tab = (RedisKeyTab<T>) new RedisStreamKeyTab(treeItem);
        }
        return tab;
    }
}
