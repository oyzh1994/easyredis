package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.InfoSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;


/**
 * redis 服务信息值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisServerInfoTreeItemValue extends RedisTreeItemValue {

    public RedisServerInfoTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return I18nResourceBundle.i18nString("base.serverInfo");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new InfoSVGGlyph("11");
            this.graphic(glyph);
        }
    }
}
