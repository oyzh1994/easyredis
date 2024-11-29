package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.InfoSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;


/**
 * redis 服务信息值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisServerInfoTreeItemValue extends RedisTreeItemValue {

    public RedisServerInfoTreeItemValue() {
        // this.flushGraphic();
        // this.flushText();
    }

    @Override
    public String name() {
        return I18nHelper.serverInfo();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new InfoSVGGlyph("11");
        }
        return super.graphic();
    }
}
