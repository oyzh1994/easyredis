package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.gui.treeView.RichTreeView;
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

    @Override
    public void onPrimaryDoubleClick() {
        super.startWaiting(() -> RedisEventUtil.connectionOpened(this.parent()));
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
