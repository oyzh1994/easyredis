package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.svg.glyph.ChangelogSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * @author oyzh
 * @since 2025/02/06
 */
public class RedisQueryTab extends DynamicTab {

    public RedisQueryTab(RedisClient client, RedisQuery query) {
        super();
        this.init(client, query);
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
        return "/tabs/query/redisQueryTab.fxml";
    }

    @Override
    protected RedisQueryTabController controller() {
        return (RedisQueryTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        if (this.controller().isUnsaved()) {
            return this.query().getName() + " *";
        }
        return this.query().getName();
    }

    public RedisQuery query() {
        return this.controller().getQuery();
    }

    public RedisConnect redisConnect() {
        return this.controller().redisConnect();
    }

    public void init(RedisClient client) {
        this.controller().init(client, null);
    }

    public void init(RedisClient client, RedisQuery query) {
        this.controller().init(client, query);
    }
}
