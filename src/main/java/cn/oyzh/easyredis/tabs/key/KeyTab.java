package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.tabs.key.hash.HashKeyTab;
import cn.oyzh.easyredis.tabs.key.hylog.HyLogKeyTab;
import cn.oyzh.easyredis.tabs.key.list.ListKeyTab;
import cn.oyzh.easyredis.tabs.key.set.SetKeyTab;
import cn.oyzh.easyredis.tabs.key.string.StringKeyTab;
import cn.oyzh.easyredis.tabs.key.zset.ZSetKeyTab;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.hylog.RedisHyperLogLogKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
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
public abstract class KeyTab<T extends RedisKeyTreeItem<?>> extends DynamicTab {

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
    public KeyTab(@NonNull T treeItem) {
        this.setClosable(true);
        this.treeItem = treeItem;
        // 加载内容
        this.loadContent();
        // 初始化
        if (!this.controller().init(treeItem)) {
            this.disable();
        }
        // 刷新图标
        this.flushGraphic();
        this.setOnCloseRequest(event -> {
            // 取消当前键的选中
            if (this.treeItem.treeView().getSelectedItem() == this.treeItem) {
                this.treeItem.treeView().select(this.treeItem.root());
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
    public KeyTabContent<T> controller() {
        return (KeyTabContent<T>) super.controller();
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

    public static <T extends RedisKeyTreeItem<?>> KeyTab<T> ofItem(T item) {
        KeyTab<T> tab = null;
        if (item instanceof RedisStringKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new StringKeyTab(treeItem);
        } else if (item instanceof RedisListKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new ListKeyTab(treeItem);
        } else if (item instanceof RedisSetKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new SetKeyTab(treeItem);
        } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
            // if (treeItem.isGEOView()) {
            //     tab = (RedisKeyTab<T>) new RedisGEOKeyTab(treeItem);
            // } else {
                tab = (KeyTab<T>) new ZSetKeyTab(treeItem);
            // }
        } else if (item instanceof RedisHashKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new HashKeyTab(treeItem);
        } else if (item instanceof RedisHyperLogLogKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new HyLogKeyTab(treeItem);
        } else if (item instanceof RedisStringKeyTreeItem treeItem) {
            tab = (KeyTab<T>) new StringKeyTab(treeItem);
        }
        return tab;
    }
}
