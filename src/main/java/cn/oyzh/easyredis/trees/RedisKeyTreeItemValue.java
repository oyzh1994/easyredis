package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * Redis 键树节点值
 *
 * @author oyzh
 * @since 2023/07/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisKeyTreeItemValue<T extends RedisKeyTreeItem<?>> extends RedisTreeItemValue {

    protected final T item;

    public RedisKeyTreeItemValue(T item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.key());
        this.flushText();
        this.flushType();
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            this.graphic(new SVGGlyph("/font/treeNode.svg", 12));
        }
    }

    @Override
    public void flushGraphicColor() {
        if (this.graphic() instanceof SVGGlyph glyph) {
            if (!this.item.dataUnsaved() && glyph.getColor() != Color.BLACK) {
                glyph.setColor(Color.BLACK);
            } else if (this.item.dataUnsaved() && glyph.getColor() != Color.ORANGERED) {
                glyph.setColor(Color.ORANGERED);
            }
        }
    }

    /**
     * 初始化类型数量组件
     */
    protected void flushType() {
        // 创建组件
        FXText text = new FXText("(" + this.item.type() + ")");
        text.setFill(Color.valueOf("#228B22"));
        this.addChild(text);
        HBox.setMargin(text, new Insets(0, 0, 0, 3));
    }
}
