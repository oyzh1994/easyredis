package cn.oyzh.easyredis.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-01-22
 */
public class KeySVGGlyph extends SVGGlyph {

    public KeySVGGlyph() {
        super("/font/key.svg");
    }

    public KeySVGGlyph(String size) {
        this();
        super.setSizeStr(size);
    }
}
