package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tree.view.FXTreeItem;
import cn.oyzh.i18n.I18nHelper;

/**
 * redis树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class RedisQueryTreeItemValue extends RichTreeItemValue {

    public RedisQueryTreeItemValue(RedisQueryTreeItem item) {
        super(item);
    }

    @Override
    protected RedisQueryTreeItem item() {
        return (RedisQueryTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new QuerySVGGlyph("10");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }
}
