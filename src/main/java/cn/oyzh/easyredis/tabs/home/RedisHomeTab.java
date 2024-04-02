package cn.oyzh.easyredis.tabs.home;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * redis主页tab
 *
 * @author oyzh
 * @since 2023/6/24
 */
public class RedisHomeTab extends DynamicTab {

    public RedisHomeTab(){
        super();
        super.flush();
    }

    @Override
    protected String url() {
        return "/tabs/home/redisHomeTabContent.fxml";
    }

    @Override
    public void flushTitle() {
        super.title("主页");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/home.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }
}
