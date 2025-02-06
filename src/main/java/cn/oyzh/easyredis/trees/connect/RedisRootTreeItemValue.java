package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.fx.svg.glyph.RedisSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;

/**
 * redis树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisRootTreeItemValue extends RichTreeItemValue {

    @Override
    public String name() {
        return I18nHelper.redis();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new RedisSVGGlyph(12);
        }
        return super.graphic();
    }
}
