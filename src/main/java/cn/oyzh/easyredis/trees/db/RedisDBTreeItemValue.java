package cn.oyzh.easyredis.trees.db;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis DB值
 *
 * @author oyzh
 * @since 2023/06/22
 */
//@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisDBTreeItemValue extends RedisTreeItemValue {

    /**
     * redis树db节点
     */
    private final RedisDBTreeItem item;

    public RedisDBTreeItemValue(RedisDBTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value());
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/database-2-line.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isKeyEmpty() && glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
        } else if (!this.item.isKeyEmpty() && glyph.getColor() != Color.DARKGREEN) {
            glyph.setColor(Color.DARKGREEN);
        }
    }

    /**
     * 刷新节点数量
     */
    public void flushNum() {
        try {
            Long totalNum = this.item.dbSize();
            // 寻找组件
            FXText text = (FXText) this.lookup("#num");
            if (totalNum == null) {
                this.removeChild(text);
            } else {
                if (text == null) {
                    text = new FXText();
                    this.addChild(text);
                    text.setId("num");
                    text.setFill(Color.valueOf("#228B22"));
                    HBox.setMargin(text, new Insets(0, 0, 0, 3));
                }
                text.setText("(" + totalNum + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 刷新键过滤模式
     */
    public void flushFilterPattern() {
        // 寻找组件
        FXText text = (FXText) this.lookup("#filterPattern");
        if (StrUtil.isNotBlank(this.item.getFilterPattern())) {
            if (text == null) {
                text = new FXText();
                text.setId("filterPattern");
                this.addChild(text);
                HBox.setMargin(text, new Insets(0, 0, 0, 3));
            }
            text.setText("[键过滤:" + this.item.getFilterPattern() + "]");
        } else {
            this.removeChild(text);
        }
    }
}
