package cn.oyzh.easyredis.trees.root;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;


/**
 * redis 根节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisRootTreeItemValue extends RedisTreeItemValue {

    public RedisRootTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return I18nResourceBundle.i18nString("base.redis");
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/redis.svg", 10);
            this.graphic(glyph);
        }
    }
}
