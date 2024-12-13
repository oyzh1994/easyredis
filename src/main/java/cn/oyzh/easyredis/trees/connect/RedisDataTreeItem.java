package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisDataTreeItem extends RichTreeItem<RedisDataTreeItem.RedisDataTreeItemValue> {

    public RedisDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisDataTreeItemValue());
    }

    @Override
    public RedisDatabaseTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (RedisDatabaseTreeItem) treeItem;
    }

    public RedisConnect redisConnect() {
        return this.parent().info();
    }

    private void setOpening(boolean opening) {
        super.getBitValue().set(7, opening);
    }

    private boolean isOpening() {
        return super.getBitValue().get(7);
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isOpening()) {
            this.setOpening(true);
            super.startWaiting(() -> {
                try {
                    RedisEventUtil.connectionOpened(this.parent());
                } finally {
                    this.setOpening(false);
                }
            });
        }
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class RedisDataTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/file-text.svg", 10);
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.data();
        }
    }
}
