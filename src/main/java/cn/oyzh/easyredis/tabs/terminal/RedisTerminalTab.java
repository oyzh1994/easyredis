package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.tabs.RedisBaseTab;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class RedisTerminalTab extends DynamicTab {

    {
        this.setClosable(true);
        this.setOnCloseRequest(event -> {
            // 关闭redis连接
            RedisClient client = this.contentController.client();
            RedisConnectUtil.close(client, true);
        });
        this.loadContent();
    }

    /**
     * 内容controller
     */
    private RedisTerminalTabContentController contentController;

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/terminal/redisTerminalTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/code library.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param info redis信息
     */
    public void init(RedisInfo info) {
        try {
            if (info == null) {
                info = new RedisInfo();
                info.setName("未命名连接");
            }
            // 设置文本
            this.setText(info.getName());
            // 刷新图标
            this.flushGraphic();
            // 初始化redis连接
            this.contentController.client(new RedisClient(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    public RedisInfo info() {
        return this.contentController.info();
    }
}
