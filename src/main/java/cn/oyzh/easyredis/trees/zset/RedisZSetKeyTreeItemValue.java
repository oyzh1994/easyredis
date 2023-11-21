package cn.oyzh.easyredis.trees.zset;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * Redis zset树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
@Slf4j
public class RedisZSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTreeItemValue(RedisZSetKeyTreeItem item) {
        super(item);
    }

    @Override
    public void flushGraphicColor() {
        if (this.graphic() instanceof SVGGlyph glyph) {
            if (!this.item.isChanged() && glyph.getColor() != Color.BLACK) {
                glyph.setColor(Color.BLACK);
            } else if (this.item.isChanged() && glyph.getColor() != Color.ORANGERED) {
                glyph.setColor(Color.ORANGERED);
            }
        }
    }
}
