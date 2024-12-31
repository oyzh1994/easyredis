package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.terminal.RedisTerminalTextTextArea;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import lombok.NonNull;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class RedisTerminalTab extends DynamicTab {

    {
        this.setClosable(true);
        this.loadContent();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        super.onTabCloseRequest(event);
        // 关闭redis连接
        RedisClient client = this.controller().client();
        RedisConnectUtil.close(client, true);
    }

    @Override
    public RedisTerminalTabController controller() {
        return (RedisTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/terminal/redisTerminalTabContent.fxml";
    }

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph graphic = (TerminalSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TerminalSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        Integer dbIndex = this.dbIndex();
        RedisConnect redisConnect = this.redisConnect();
        // 设置文本
        if (dbIndex != null) {
            return redisConnect.getName() + "@" + dbIndex;
        }
        return redisConnect.getName();
    }

    /**
     * 初始化
     *
     * @param redisConnect redis信息
     */
    public void init(RedisConnect redisConnect, Integer dbIndex) {
        try {
            if (redisConnect == null) {
                redisConnect = new RedisConnect();
                redisConnect.setName(I18nHelper.unnamedConnection());
            }
            // 初始化redis连接
            this.controller().init(new RedisClient(redisConnect), dbIndex);
            // 刷新tab
            this.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * db索引
     *
     * @return 当前db索引
     */
    public Integer dbIndex() {
        return this.controller().dbIndex();
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    public RedisConnect redisConnect() {
        return this.controller().redisConnect();
    }

    /**
     * redis命令行tab内容组件
     *
     * @author oyzh
     * @since 2023/07/21
     */
    public static class RedisTerminalTabController extends DynamicTabController {

        /**
         * redis命令行文本域
         */
        @FXML
        private RedisTerminalTextTextArea terminal;

        /**
         * 初始化
         *
         * @param client redis客户端
         */
        public void init(@NonNull RedisClient client, Integer dbIndex) {
            this.terminal.init(client, dbIndex);
        }

        /**
         * redis信息
         *
         * @return 当前redis信息
         */
        protected RedisConnect redisConnect() {
            return this.terminal.redisConnect();
        }

        public Integer dbIndex() {
            return this.terminal.dbIndex();
        }

        public RedisClient client() {
            return this.terminal.client();
        }
    }
}
