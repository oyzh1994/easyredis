package cn.oyzh.easyredis.tabs.changelog;

import cn.oyzh.fx.plus.controls.svg.ChangelogSVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
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
        return "/tabs/changelog/changelogContent.fxml";
    }
}
