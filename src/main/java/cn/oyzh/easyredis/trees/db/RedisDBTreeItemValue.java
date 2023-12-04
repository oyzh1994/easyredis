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
     * 子节点总数量
     */
    private Long totalNum;

    /**
     * 子节点显示数量
     */
    private Integer showNum;

    /**
     * 键过滤模式
     */
    private String keyFilterPattern;

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
        if (this.item.isChildEmpty() && glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
        } else if (!this.item.isChildEmpty() && glyph.getColor() != Color.DARKGREEN) {
            glyph.setColor(Color.DARKGREEN);
        }
    }

    /**
     * 设置节点数量
     *
     * @param totalNum     子节点总数量
     * @param showNum 子节点显示数量
     */
    public void num(Long totalNum, Integer showNum) {
        if (totalNum != null) {
            this.totalNum = totalNum;
        }
        this.showNum = showNum;
        // 寻找组件
        FXText text = (FXText) this.lookup("#num");
        if (text == null) {
            text = new FXText();
            text.setId("num");
            text.setFill(Color.valueOf("#228B22"));
            this.addChild(text);
            HBox.setMargin(text, new Insets(0, 0, 0, 3));
        }
        if (this.showNum == null || this.showNum.longValue() == this.totalNum) {
            text.setText("(" + this.totalNum + ")");
        } else {
            text.setText("(" + this.showNum + "/" + this.totalNum + ")");
        }
    }

    /**
     * 设置键过滤模式
     *
     * @param keyFilterPattern 键过滤模式
     */
    public void keyFilterPattern(String keyFilterPattern) {
        this.keyFilterPattern = keyFilterPattern;
        this.flushKeyFilter();
    }

    /**
     * 初始化键过滤组件
     */
    public void flushKeyFilter() {
        // 寻找组件
        FXText text = (FXText) this.lookup("#filter");
        if (StrUtil.isNotBlank(this.keyFilterPattern)) {
            if (text == null) {
                text = new FXText();
                text.setId("filter");
                this.addChild(text);
                HBox.setMargin(text, new Insets(0, 0, 0, 3));
            }
            text.setText("[过滤:" + this.keyFilterPattern + "]");
        } else if (text != null) {
            this.removeChild(text);
        }
    }
}
