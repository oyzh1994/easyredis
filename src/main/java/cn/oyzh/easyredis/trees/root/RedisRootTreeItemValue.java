package cn.oyzh.easyredis.trees.root;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;


/**
 * redis 根节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisRootTreeItemValue extends RedisTreeItemValue {

    public RedisRootTreeItemValue() {
        // this.flushGraphic();
        // this.flushText();
    }

    @Override
    public String name() {
        return I18nHelper.redis();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/redis.svg", 10);
        }
        return super.graphic();
    }
}
