package cn.oyzh.easyredis.fx.svg.pane;

import cn.oyzh.easyredis.fx.svg.glyph.CollapseListSVGGlyph;
import cn.oyzh.easyredis.fx.svg.glyph.ExpandListSVGGlyph;
import cn.oyzh.fx.gui.svg.glyph.CollectSVGGlyph;
import cn.oyzh.fx.gui.svg.glyph.UnCollectSVGGlyph;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.mouse.MouseAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2025-01-04
 */
public class ExpandListSVGPane extends FXHBox implements MouseAdapter {

    @Getter
    private String size;

    public void setSize(String size) {
        this.size = size;
        SVGGlyph glyph = (SVGGlyph) this.getChild(0);
        if (glyph != null) {
            glyph.setSizeStr(size);
        }
    }

    public ExpandListSVGPane() {
        this.collapse();
    }

    public void expand() {
        this.setChild(new ExpandListSVGGlyph(this.size));
    }

    public void collapse() {
        this.setChild(new CollapseListSVGGlyph(this.size));
    }

    public boolean isCollapse() {
        SVGGlyph svgGlyph = (SVGGlyph) this.getChildren().getFirst();
        return svgGlyph.getUrl().contains("arrow-up-double-line.svg");
    }

    public void setCollapse(boolean collapse) {
        if (collapse) {
            this.collapse();
        } else {
            this.expand();
        }
    }
}
