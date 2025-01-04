//package cn.oyzh.easyredis.tabs.keys;
//
//import cn.oyzh.easyredis.domain.RedisConnect;
//import cn.oyzh.easyredis.redis.RedisClient;
//import cn.oyzh.easyredis.redis.key.RedisKey;
//import cn.oyzh.easyredis.trees.keys.RedisHashKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisSetKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisStreamKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
//import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
//import cn.oyzh.fx.gui.tabs.DynamicTab;
//import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
//import cn.oyzh.fx.plus.information.MessageBox;
//import cn.oyzh.fx.plus.thread.BackgroundService;
//import cn.oyzh.i18n.I18nHelper;
//import javafx.scene.Cursor;
//import lombok.Getter;
//import lombok.experimental.Accessors;
//
///**
// * redis 键tab
// *
// * @author oyzh
// * @since 2023/06/21
// */
//public class RedisKeyDataTab<T extends RedisKeyTreeItem> extends DynamicTab {
//
//    /**
//     * redis树节点
//     */
//    @Getter
//    @Accessors(fluent = true)
//    protected T treeItem;
//
//    public void init(T treeItem) {
//        // 刷新
//        this.flush();
//        // 初始化
//        this.controller().init(treeItem);
//        // 判断这个key是否到期
//        if (treeItem.isExpire()) {
//            BackgroundService.submitFXLater(() -> {
//                String tips = I18nHelper.key() + " [" + treeItem.key() + "] " + I18nHelper.expired() + ", " + I18nHelper.delete() + "?";
//                if (MessageBox.confirm(tips)) {
//                    treeItem.deleteByExpired();
//                    this.closeTab();
//                }
//            });
//        }
//    }
//
//    @Override
//    protected String getTabTitle() {
//        return I18nHelper.data();
//    }
//
//    @Override
//    public void flushGraphic() {
//        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
//        if (glyph == null) {
//            glyph = new SVGGlyph("/font/key.svg", 12);
//            glyph.setCursor(Cursor.DEFAULT);
//            this.setGraphic(glyph);
//        }
//    }
//
//    @Override
//    public RedisKeyDataController controller() {
//        return (RedisKeyDataController) super.controller();
//    }
//
//    /**
//     * 重新载入
//     */
//    public void reload() {
//        this.controller().reloadKey();
//    }
//
//    /**
//     * 刷新ttl
//     */
//    public void flushTTL() {
//        this.controller().flushTTL();
//    }
//
//    /**
//     * 获取redis客户端
//     *
//     * @return redis客户端
//     */
//    public RedisClient client() {
//        return this.treeItem.client();
//    }
//
//    /**
//     * 获取redis客户端
//     *
//     * @return redis客户端
//     */
//    public RedisConnect redisConnect() {
//        return this.client().redisConnect();
//    }
//
//    /**
//     * 获取redis键
//     *
//     * @return redis键
//     */
//    public RedisKey key() {
//        return this.treeItem.value();
//    }
//}
