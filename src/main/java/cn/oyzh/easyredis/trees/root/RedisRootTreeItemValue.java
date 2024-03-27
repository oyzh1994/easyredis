package cn.oyzh.easyredis.trees.root;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;


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
        return "Redis连接列表";
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/redis.svg", 10);
            this.graphic(glyph);
        }
    }
}
