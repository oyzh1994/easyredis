package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis Group键值
 *
 * @author oyzh
 * @since 2023/11/21
 */
@Accessors(chain = true, fluent = true)
public class RedisGroupTreeItemValue extends RedisTreeItemValue {

    // private final RedisGroupTreeItem item;

    public RedisGroupTreeItemValue(RedisGroupTreeItem item) {
        super(item);
        // this.flushGraphic();
        // this.flushGraphicColor();
        // this.name(item.value().getName());
    }

    @Override
    protected RedisGroupTreeItem item() {
        return (RedisGroupTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new GroupSVGGlyph("10");
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (this.item.isChildEmpty()) {
            return super.graphicColor();
        }
        return Color.DEEPSKYBLUE;
    }
}
