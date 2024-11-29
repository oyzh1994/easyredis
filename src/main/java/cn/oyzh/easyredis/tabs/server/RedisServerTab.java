package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.svg.ServerSVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * redis服务信息tab
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class RedisServerTab extends DynamicTab {

    @Override
    public RedisServerTabContent controller() {
        return (RedisServerTabContent) super.controller();
    }

    @Override
    protected String url() {
        return  "/tabs/server/redisServerTabContent.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new ServerSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(RedisClient client) {
        try {
            // 设置文本
            this.setText(I18nHelper.serverInfo() + "-" + client.infoName());
            // 刷新图标
            this.flushGraphic();
            // 初始化
            this.controller().init(client);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭刷新任务
     */
    public void closeRefreshTask() {
        this.controller().closeRefreshTask();
    }

    /**
     * redis信息
     *
     * @return redis信息
     */
    public RedisConnect info() {
        return this.controller().client().redisInfo();
    }

    /**
     * redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.controller().client();
    }

    @Override
    protected void onTabClosed(Event event) {
        super.onTabClosed(event);
        this.closeRefreshTask();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        super.onTabCloseRequest(event);
        this.closeRefreshTask();
    }
}
