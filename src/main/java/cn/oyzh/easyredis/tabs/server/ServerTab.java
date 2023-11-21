package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * redis服务信息tab
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class ServerTab extends DynamicTab {

    {
        this.setClosable(true);
        this.setOnCloseRequest(event -> this.closeRefreshTask());
        this.loadContent();
    }

    /**
     * 内容controller
     */
    private ServerTabContent contentController;

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/server/redisServerTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/server.svg", "13");
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
            this.setText("服务信息(" + client.infoName() + ")");
            // 刷新图标
            this.flushGraphic();
            // 初始化
            this.contentController.init(client);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭刷新任务
     */
    public void closeRefreshTask() {
        this.contentController.closeRefreshTask();
    }

    /**
     * redis信息
     *
     * @return redis信息
     */
    public RedisInfo info() {
        return this.contentController.client().redisInfo();
    }

    /**
     * redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.contentController.client();
    }
}
