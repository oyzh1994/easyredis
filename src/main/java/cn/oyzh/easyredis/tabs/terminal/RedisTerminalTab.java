package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class RedisTerminalTab extends DynamicTab {

//    {
//        this.setClosable(true);
//        this.loadContent();
//    }

    public RedisTerminalTab(RedisClient client, Integer dbIndex) {
        this.init(client, dbIndex);
    }

//    @Override
//    protected void onTabCloseRequest(Event event) {
//        super.onTabCloseRequest(event);
//        // 关闭redis连接
//        RedisClient client = this.controller().client();
//        RedisConnectUtil.close(client, true);
//    }

    @Override
    public RedisTerminalTabController controller() {
        return (RedisTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/terminal/redisTerminalTab.fxml";
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
     * @param client zk客户端
     */
    private void init(RedisClient client, Integer dbIndex) {
        try {
            if (client == null) {
                RedisConnect connect = new RedisConnect();
                connect.setName(I18nHelper.unnamedConnection());
                // 刷新图标
                this.flushGraphic();
//                // 设置标题
//                super.setTitle(connect.getName());
                // 初始化zk连接
                this.controller().init(new RedisClient(connect), dbIndex);
            } else {
                // 刷新图标
                this.flushGraphic();
//                // 设置标题
//                super.setTitle(client.connectName());
                // 初始化zk连接
                this.controller().init(client, dbIndex);
            }
            this.flushTitle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    /**
//     * 初始化
//     *
//     * @param redisConnect redis信息
//     */
//    public void init(RedisConnect redisConnect, Integer dbIndex) {
//        try {
//            if (redisConnect == null) {
//                redisConnect = new RedisConnect();
//                redisConnect.setName(I18nHelper.unnamedConnection());
//            }
//            // 初始化redis连接
//            this.controller().init(new RedisClient(redisConnect), dbIndex);
//            // 刷新tab
//            this.flush();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

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

    public RedisClient client() {
        return this.controller().client();
    }
}
