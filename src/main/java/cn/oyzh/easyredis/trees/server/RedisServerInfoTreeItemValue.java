package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;


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
        return "服务信息";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/info-circle.svg", 10);
            this.graphic(glyph);
        }
    }
}
