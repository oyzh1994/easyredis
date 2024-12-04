package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
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
public class RedisQueryTreeItem extends RichTreeItem<RedisQueryTreeItem.RedisQueryTreeItemValue> {

    public RedisQueryTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisQueryTreeItemValue());
    }

    @Override
    public RedisDatabaseTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisDatabaseTreeItem) parent;
    }

    public RedisConnect redisConnect(){
        return this.parent().info();
    }

    @Override
    public void onPrimaryDoubleClick() {
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class RedisQueryTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new QuerySVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.query();
        }
    }
}
