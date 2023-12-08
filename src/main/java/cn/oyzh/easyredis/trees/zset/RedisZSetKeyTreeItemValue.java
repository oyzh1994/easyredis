package cn.oyzh.easyredis.trees.zset;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * Redis zset树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisZSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTreeItemValue(RedisZSetKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((observableValue, aDouble, t1) -> this.flushGraphicColor());
        item.scoreProperty().addListener((observableValue, aDouble, t1) -> this.flushGraphicColor());
        item.latitudeProperty().addListener((observableValue, aDouble, t1) -> this.flushGraphicColor());
        item.longitudeProperty().addListener((observableValue, aDouble, t1) -> this.flushGraphicColor());
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
}
