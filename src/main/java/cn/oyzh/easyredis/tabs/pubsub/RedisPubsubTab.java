package cn.oyzh.easyredis.tabs.pubsub;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.textarea.ReadOnlyTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import redis.clients.jedis.JedisPubSub;

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

    @Override
    public RedisPubsubTabContent controller() {
        return (RedisPubsubTabContent) super.controller();
    }

    @Override
    protected String url() {
        return  "/tabs/terminal/redisTerminalTabContent.fxml";
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
            this.setText(item.getClient().infoName() + "-" + item.getChannel());
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

    public RedisClient client() {
        return this.item.getClient();
    }

    /**
     * redis发布订阅内容组件
     *
     * @author oyzh
     * @since 2023/08/02
     */
    public static class RedisPubsubTabContent extends DynamicTabController {

        /**
         * 订阅组件
         */
        private JedisPubSub pubSub;

        /**
         * 文本域
         */
        @FXML
        private ReadOnlyTextArea textArea;

        /**
         * 初始化
         *
         * @param item redis发布订阅键
         */
        public void init(RedisPubsubItem item) {
            this.textArea.appendText(RedisI18nHelper.pubsubTip1() + item.getChannel());
            this.pubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    textArea.appendLine(I18nHelper.receiveMessage() + ": " + message);
                }
            };
            ThreadUtil.startVirtual(() -> item.getClient().subscribe(this.pubSub, item.getChannel()));
        }

        /**
         * 取消订阅
         */
        public void unsubscribe() {
            try {
                this.pubSub.unsubscribe();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
