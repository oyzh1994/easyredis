package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * Redis DB树键值
 *
 * @author oyzh
 * @since 2023/07/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisKeyTreeItemValue extends RedisTreeItemValue {

    private final RedisKey node;

    public RedisKeyTreeItemValue(@NonNull RedisKey node) {
        this.node = node;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(node.key());
        this.flushText();
        this.flushType();
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            this.graphic(new SVGGlyph("/font/treeNode.svg", 12));
        }
    }

    /**
     * 初始化类型数量组件
     */
    protected void flushType() {
        // 创建组件
        FXText text = new FXText("(" + this.node.type() + ")");
        text.setFill(Color.valueOf("#228B22"));
        this.addChild(text);
        HBox.setMargin(text, new Insets(0, 0, 0, 3));
    }
}
