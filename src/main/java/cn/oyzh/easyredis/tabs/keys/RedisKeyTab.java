package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.trees.keys.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * redis 键tab
 *
 * @author oyzh
 * @since 2023/06/21
 */
public abstract class RedisKeyTab<T extends RedisKeyTreeItem> extends DynamicTab {

    // /**
    //  * 标签打开时间
    //  */
    // @Getter
    // private final long openedTime = System.currentTimeMillis();

    /**
     * redis树节点
     */
    @Getter
    @Accessors(fluent = true)
    protected T treeItem;

    public RedisKeyTab(T treeItem){
        super();
        // 刷新
        this.flush();
        // 初始化
        if (!this.controller().init(treeItem)) {
            this.disable();
        }
        // 判断这个key是否到期
        if (treeItem.isExpire()) {
            BackgroundService.submitFXLater(() -> {
                String tips = I18nHelper.key() + " [" + treeItem.key() + "] " + I18nHelper.expired() + ", " + I18nHelper.delete() + "?";
                if (MessageBox.confirm(tips)) {
                    treeItem.deleteByExpired();
                    this.closeTab();
                }
            });
        }
    }

    // /**
    //  * 执行初始化
    //  *
    //  * @param treeItem redis树节点
    //  */
    // public void init(@NonNull T treeItem) {
    //     if (treeItem != this.treeItem) {
    //         this.treeItem = treeItem;
    //         // 刷新
    //         this.flush();
    //         // 初始化
    //         if (!this.controller().init(treeItem)) {
    //             this.disable();
    //         }
    //         // 判断这个key是否到期
    //         if (treeItem.isExpire()) {
    //             BackgroundService.submitFXLater(() -> {
    //                 String tips = I18nHelper.key() + " [" + treeItem.key() + "] " + I18nHelper.expired() + ", " + I18nHelper.delete() + "?";
    //                 if (MessageBox.confirm(tips)) {
    //                     treeItem.deleteByExpired();
    //                     this.closeTab();
    //                 }
    //             });
    //         }
    //     }
    // }

    // @Override
    // public void flushTitle() {
    //     // 设置文本
    //     this.setText(this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "-" + this.treeItem.key());
    //     // 设置提示文本
    //     this.setTipText(this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "-" + this.treeItem.key());
    // }

    @Override
    protected String getTabTitle() {
        return I18nHelper.data();
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
    public RedisKeyTabController<T> controller() {
        return (RedisKeyTabController<T>) super.controller();
    }

    /**
     * 重新载入
     */
    public void reload() {
        this.controller().reloadKey();
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
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisConnect redisConnect() {
        return this.client().redisInfo();
    }

    /**
     * 获取redis键
     *
     * @return redis键
     */
    public RedisKey key() {
        return this.treeItem.value();
    }

    public static  RedisKeyTab<?> ofItem(RedisKeyTreeItem item) {
        RedisKeyTab<?> tab = null;
        if (item instanceof RedisStringKeyTreeItem stringKeyTreeItem) {
            if (stringKeyTreeItem.isHyLog()) {
                tab = new RedisHyLogKeyTab(stringKeyTreeItem);
            } else {
                tab = new RedisStringKeyTab(stringKeyTreeItem);
            }
        } else if (item instanceof RedisListKeyTreeItem listKeyTreeItem) {
            tab = new RedisListKeyTab(listKeyTreeItem);
        } else if (item instanceof RedisSetKeyTreeItem setKeyTreeItem) {
            tab = new RedisSetKeyTab(setKeyTreeItem);
        } else if (item instanceof RedisZSetKeyTreeItem zSetKeyTreeItem) {
            if (zSetKeyTreeItem.isCoordinateView()) {
                tab = new RedisCoordinateKeyTab(zSetKeyTreeItem);
            } else {
                tab = new RedisZSetKeyTab(zSetKeyTreeItem);
            }
        } else if (item instanceof RedisHashKeyTreeItem hashKeyTreeItem) {
            tab = new RedisHashKeyTab(hashKeyTreeItem);
        } else if (item instanceof RedisStreamKeyTreeItem streamKeyTreeItem) {
            tab = new RedisStreamKeyTab(streamKeyTreeItem);
        }
        return tab;
    }
}
