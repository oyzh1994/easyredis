//package cn.oyzh.easyredis.trees.connect;
//
//import cn.oyzh.easyredis.domain.RedisConnect;
//import cn.oyzh.easyredis.dto.RedisScript;
//import cn.oyzh.easyredis.redis.RedisClient;
//import cn.oyzh.fx.gui.menu.MenuItemHelper;
//import cn.oyzh.fx.gui.tree.view.RichTreeItem;
//import cn.oyzh.fx.gui.tree.view.RichTreeView;
//import cn.oyzh.fx.plus.information.MessageBox;
//import cn.oyzh.fx.plus.menu.FXMenuItem;
//import cn.oyzh.i18n.I18nHelper;
//import javafx.scene.control.MenuItem;
//import javafx.scene.control.TreeItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author oyzh
// * @since 2025/01/20
// */
//public class RedisScriptTreeItem extends RichTreeItem<RedisScriptTreeItemValue> {
//
//    private final RedisScript value;
//
//    public RedisScript value() {
//        return value;
//    }
//
//    public RedisScriptTreeItem(RedisScript script, RichTreeView treeView) {
//        super(treeView);
//        this.value = script;
//        this.setValue(new RedisScriptTreeItemValue(this));
//    }
//
//    @Override
//    public RedisQueriesTreeItem parent() {
//        TreeItem<?> parent = this.getParent();
//        return (RedisQueriesTreeItem) parent;
//    }
//
//    public RedisClient client() {
//        return this.parent().client();
//    }
//
//    public RedisConnect redisConnect() {
//        return this.parent().redisConnect();
//    }
//
//    @Override
//    public List<MenuItem> getMenuItems() {
//        List<MenuItem> items = new ArrayList<>(4);
//        FXMenuItem openQuery = MenuItemHelper.openQuery("12", this::loadChild);
//        FXMenuItem renameQuery = MenuItemHelper.renameQuery("12", this::rename);
//        FXMenuItem deleteQuery = MenuItemHelper.deleteQuery("12", this::delete);
//        items.add(openQuery);
//        items.add(renameQuery);
//        items.add(deleteQuery);
//        return items;
//    }
//
//    @Override
//    public void delete() {
//        if (MessageBox.confirm(I18nHelper.deleteQuery() + "[" + this.value.getSha1() + "]?")) {
//            if (this.client().deleteScript(this.value.getSha1())) {
//                super.remove();
//            } else {
//                MessageBox.warn(I18nHelper.operationFail());
//            }
//        }
//    }
//
//    @Override
//    public void loadChild() {
////        RedisEventUtil.openQuery(this.client(), this.value);
//    }
//
//    @Override
//    public void onPrimaryDoubleClick() {
//        this.loadChild();
//    }
//}
