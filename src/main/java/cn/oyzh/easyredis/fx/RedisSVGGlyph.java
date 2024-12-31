package cn.oyzh.easyredis.fx;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024-12-10
 */
public class RedisSVGGlyph extends SVGGlyph {

    public RedisSVGGlyph() {
        super("/font/redis.svg");
    }

    public RedisSVGGlyph(double size) {
        super("/font/redis.svg", size);
    }
}
