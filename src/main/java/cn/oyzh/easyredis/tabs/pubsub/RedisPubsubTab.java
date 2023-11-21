package cn.oyzh.easyredis.tabs.pubsub;

import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis发布订阅tab
 *
 * @author oyzh
 * @since 2023/08/02
 */
public class RedisPubsubTab extends DynamicTab {

    {
        this.setClosable(true);
        this.setOnCloseRequest(event -> this.unsubscribe());
        this.loadContent();
    }

    /**
     * redis发布及订阅节点
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private RedisPubsubItem item;

    /**
     * 内容controller
     */
    private RedisPubsubTabContentController contentController;

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/pubsub/redisPubsubTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/subscribe.svg", "13");
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
            this.setText(item.getClient().infoName() + "(" + item.getChannel() + ")");
            // 刷新图标
            this.flushGraphic();
            // 初始化
            this.contentController.init(item);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 取消订阅
     */
    public void unsubscribe() {
        this.contentController.unsubscribe();
    }

    public RedisClient client() {
        return this.item.getClient();
    }
}
