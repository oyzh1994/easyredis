//package cn.oyzh.easyredis.trees.connect;
//
//import cn.oyzh.easyredis.domain.RedisConnect;
//import cn.oyzh.easyredis.event.RedisEventUtil;
//import cn.oyzh.fx.gui.tree.view.RichTreeItem;
//import cn.oyzh.fx.gui.tree.view.RichTreeView;
//import javafx.scene.control.TreeItem;
//
///**
// * @author oyzh
// * @since 2023/1/30
// */
//public class RedisDataTreeItem extends RichTreeItem<RedisDataTreeItemValue> {
//
//    public RedisDataTreeItem(RichTreeView treeView) {
//        super(treeView);
//        this.setValue(new RedisDataTreeItemValue());
//    }
//
//    @Override
//    public RedisDatabaseTreeItem parent() {
//        TreeItem<?> treeItem = super.getParent();
//        return (RedisDatabaseTreeItem) treeItem;
//    }
//
//    public RedisConnect redisConnect() {
//        return this.parent().redisConnect();
//    }
//
//    private void setOpening(boolean opening) {
//        super.bitValue().set(7, opening);
//    }
//
//    private boolean isOpening() {
//        return super.bitValue().get(7);
//    }
//
//    @Override
//    public void onPrimaryDoubleClick() {
//        if (!this.isOpening()) {
//            this.setOpening(true);
//            super.startWaiting(() -> {
//                try {
//                    RedisEventUtil.connectionOpened(this.parent());
//                } finally {
//                    this.setOpening(false);
//                }
//            });
//        }
//    }
//
//}
