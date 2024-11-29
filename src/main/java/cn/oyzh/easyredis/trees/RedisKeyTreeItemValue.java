package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * Redis 键树节点值
 *
 * @author oyzh
 * @since 2023/07/7
 */
public abstract class RedisKeyTreeItemValue<T extends RedisKeyTreeItem<?, ?>> extends RedisTreeItemValue {

    // @Accessors(chain = true, fluent = true)
    // protected final T item;

    public RedisKeyTreeItemValue(T item) {
        super(item);
        //     this.flushGraphic();
        //     this.flushGraphicColor();
        //     this.name(item.key());
    }

    @Override
    protected RedisKeyTreeItem<?, ?> item() {
        return (RedisKeyTreeItem<?, ?>) super.item();
    }

    @Override
    public String name() {
        return this.item().key();
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
