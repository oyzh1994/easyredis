package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.ServerSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;

/**
 * redis 服务信息值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisServerInfoTreeItemValue extends RichTreeItemValue {

    @Override
    public String name() {
        return I18nHelper.info();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new ServerSVGGlyph("10");
        }
        return super.graphic();
    }
}
