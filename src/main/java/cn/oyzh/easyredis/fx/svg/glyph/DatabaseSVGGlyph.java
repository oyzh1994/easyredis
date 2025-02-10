package cn.oyzh.easyredis.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-01-22
 */
public class DatabaseSVGGlyph extends SVGGlyph {

    public DatabaseSVGGlyph() {
        super("/font/database-2-line.svg");
    }

    public DatabaseSVGGlyph(String size) {
        this();
        super.setSizeStr(size);
    }
}
