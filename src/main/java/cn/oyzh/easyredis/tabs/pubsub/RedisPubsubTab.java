package cn.oyzh.easyredis.tabs.pubsub;

import cn.oyzh.easyredis.dto.RedisPubsubItem;
import cn.oyzh.easyredis.fx.svg.glyph.SubscribeSVGGlyph;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.tabs.RichTab;
import javafx.event.Event;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis发布订阅tab
 *
 * @author oyzh
 * @since 2023/08/02
 */
public class RedisPubsubTab extends RichTab {

    {
        this.setClosable(true);
        // this.setOnCloseRequest(event -> this.unsubscribe());
        this.loadContent();
    }

    /**
     * redis发布及订阅节点
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private RedisPubsubItem item;

    @Override
    public RedisPubsubTabController controller() {
        return (RedisPubsubTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/pubsub/redisPubsubTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SubscribeSVGGlyph graphic = (SubscribeSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SubscribeSVGGlyph(13);
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param item redis发布及订阅节点
     */
    public void init(RedisPubsubItem item) {
        try {
            this.item = item;
            // 设置文本
            this.setText(item.getClient().connectName() + "-" + item.getChannel());
            // 刷新图标
            this.flushGraphic();
            // 初始化
            this.controller().init(item);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 取消订阅
     */
    public void unsubscribe() {
        this.controller().unsubscribe();
    }

    @Override
    protected void onTabClosed(Event event) {
        super.onTabClosed(event);
        this.unsubscribe();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        super.onTabCloseRequest(event);
        this.unsubscribe();
    }

    public RedisClient client() {
        return this.item.getClient();
    }

}
