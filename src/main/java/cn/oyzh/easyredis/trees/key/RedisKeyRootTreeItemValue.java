package cn.oyzh.easyredis.trees.key;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2025-02-10
 */
public class RedisKeyRootTreeItemValue extends RichTreeItemValue {

    public RedisKeyRootTreeItemValue(RedisKeyRootTreeItem item) {
        super(item);
    }

    @Override
    protected RedisKeyRootTreeItem item() {
        return (RedisKeyRootTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic != null && this.graphic.isWaiting()) {
            this.graphic.enableTheme();
            return this.graphic;
        }
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/key.svg", 10);
        }
        this.graphic.disableTheme();
        return super.graphic();
    }

    @Override
    public String name() {
        return I18nHelper.keys();
    }

    @Override
    public String extra() {
        int size = this.item().keyChildrenSize();
        String extra= "(" + size + ")";
        String filterPattern = this.item().dbItem().getFilterPattern();
        if (StringUtil.isNotBlank(filterPattern)) {
            extra += "[" + I18nHelper.keyFilter() + ":" + filterPattern + "]";
        }
        return extra;
    }
}
