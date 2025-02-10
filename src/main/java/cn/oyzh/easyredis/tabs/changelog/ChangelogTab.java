package cn.oyzh.easyredis.tabs.changelog;

import cn.oyzh.fx.gui.svg.glyph.ChangelogSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import javafx.scene.Cursor;

/**
 * 更新日志表tab
 *
 * @author oyzh
 * @since 2024/05/08
 */
public class ChangelogTab extends DynamicTab {

    public ChangelogTab() {
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        ChangelogSVGGlyph glyph = (ChangelogSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new ChangelogSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/changelog/changelogTab.fxml";
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.changelog");
    }

}
