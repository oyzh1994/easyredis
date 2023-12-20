package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis 键树节点值
 *
 * @author oyzh
 * @since 2023/07/7
 */
public abstract class RedisKeyTreeItemValue<T extends RedisKeyTreeItem<?, ?>> extends RedisTreeItemValue {

    @Accessors(chain = true, fluent = true)
    protected final T item;

    public RedisKeyTreeItemValue(T item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.key());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/key.svg", 10);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        if (this.graphic() instanceof SVGGlyph glyph) {
            if (!this.item.dataUnsaved()) {
                if (ThemeManager.isDarkMode()) {
                    glyph.setColor(Color.WHITE);
                } else {
                    glyph.setColor(Color.BLACK);
                }
            } else {
                glyph.setColor(Color.ORANGERED);
            }
        }
    }
}
