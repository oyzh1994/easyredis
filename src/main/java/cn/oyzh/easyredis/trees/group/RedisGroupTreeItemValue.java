package cn.oyzh.easyredis.trees.group;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis Group键值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisGroupTreeItemValue extends RedisTreeItemValue {

    private final RedisGroupTreeItem item;

    public RedisGroupTreeItemValue(RedisGroupTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value().getName());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/group.svg", 10);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isChildEmpty()) {
            super.flushGraphicColor();
        } else {
            glyph.setColor(Color.DEEPSKYBLUE);
        }
    }
}
