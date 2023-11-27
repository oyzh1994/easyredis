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
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/group.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isChildEmpty() && glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
        } else if (!this.item.isChildEmpty() && glyph.getColor() != Color.DEEPSKYBLUE) {
            glyph.setColor(Color.DARKBLUE);
        }
    }
}
