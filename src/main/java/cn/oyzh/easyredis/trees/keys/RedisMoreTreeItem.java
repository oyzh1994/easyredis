package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.fx.gui.svg.glyph.MoreSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisMoreTreeItem extends RichTreeItem<RedisMoreTreeItem.RedisMoreTreeItemValue> {

    public RedisMoreTreeItem(RedisKeysTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(false);
        this.setValue(new RedisMoreTreeItemValue());
    }

    @Override
    public RedisRootKeyTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisRootKeyTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoading()) {
            RedisRootKeyTreeItem treeItem = this.parent();
            if (treeItem != null) {
                treeItem.loadChild();
            }
        }
    }

    /**
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class RedisMoreTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new MoreSVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.loadMore();
        }
    }
}
