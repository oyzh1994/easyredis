package cn.oyzh.easyredis.tabs.filter;

import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import javafx.scene.Cursor;

/**
 * redis过滤列表tab
 *
 * @author oyzh
 * @since 2023/11/27
 */
public class RedisFilterTab extends DynamicTab {

    public RedisFilterTab() {
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new FilterSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/filter/redisFilterTabContent.fxml";
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.filter.main");
    }
}
