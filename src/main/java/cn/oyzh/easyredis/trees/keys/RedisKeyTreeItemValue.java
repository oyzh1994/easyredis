package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * Redis 键树节点值
 *
 * @author oyzh
 * @since 2023/07/7
 */
public class RedisKeyTreeItemValue extends RichTreeItemValue {

    public RedisKeyTreeItemValue(RedisKeyTreeItem item) {
        super(item);
    }

    @Override
    protected RedisKeyTreeItem item() {
        return (RedisKeyTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().key();
    }

    @Override
    public String extra() {
        return " (" + this.item().type().name() + ")";
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/key.svg", 10);
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (this.item().dataUnsaved()) {
            return Color.ORANGERED;
        }
        return super.graphicColor();
    }
}
