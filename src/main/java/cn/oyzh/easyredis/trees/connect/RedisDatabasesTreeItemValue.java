package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.fx.svg.glyph.DatabaseSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import lombok.experimental.Accessors;

/**
 * Redis DB值
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Accessors(chain = true, fluent = true)
public class RedisDatabasesTreeItemValue extends RichTreeItemValue {

    public RedisDatabasesTreeItemValue(RedisDatabasesTreeItem item) {
        super(item);
    }

    @Override
    protected RedisDatabasesTreeItem item() {
        return (RedisDatabasesTreeItem) super.item();
    }

    @Override
    public String name() {
        return I18nHelper.database();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new DatabaseSVGGlyph("10");
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public String extra() {
        try {
            int databases = this.item().databases();
            return "(" + databases + ")";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.extra();
    }
}
