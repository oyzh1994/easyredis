package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

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
            RedisClient client = this.controller().client();
            RedisConnectUtil.close(client, true);
        });
        this.loadContent();
    }

    @Override
    public RedisTerminalTabContent controller() {
        return (RedisTerminalTabContent) super.controller();
    }

    @Override
    protected String url() {
        return  "/tabs/terminal/redisTerminalTabContent.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TerminalSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param info redis信息
     */
    public void init(RedisConnect info) {
        try {
            if (info == null) {
                info = new RedisConnect();
                info.setName(I18nHelper.unnamedConnection());

            }
            // 设置文本
            this.setText(info.getName());
            // 刷新图标
            this.flushGraphic();
            // 初始化redis连接
            this.controller().client(new RedisClient(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    public RedisConnect info() {
        return this.controller().info();
    }
}
