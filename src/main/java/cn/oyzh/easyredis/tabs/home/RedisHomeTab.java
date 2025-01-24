package cn.oyzh.easyredis.tabs.home;

import cn.oyzh.common.dto.Project;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.svg.glyph.HomeSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis主页tab
 *
 * @author oyzh
 * @since 2023/6/24
 */
public class RedisHomeTab extends DynamicTab {

    public RedisHomeTab() {
        super();
        super.flush();
    }

    @Override
    protected String url() {
        return "/tabs/home/redisHomeTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new HomeSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.home");
    }

}
