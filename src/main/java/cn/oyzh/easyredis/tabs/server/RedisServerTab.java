package cn.oyzh.easyredis.tabs.server;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ExecutorUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.dto.RedisInfoProp;
import cn.oyzh.easyredis.dto.RedisServerItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.svg.glyph.ServerSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.concurrent.Future;

/**
 * redis服务信息tab
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class RedisServerTab extends DynamicTab {

    @Override
    public RedisServerTabController controller() {
        return (RedisServerTabController) super.controller();
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
    public RedisConnect redisConnect() {
        return this.controller().client().redisConnect();
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

    /**
     * redis终端tab内容组件
     *
     * @author oyzh
     * @since 2023/08/01
     */
    public static class RedisServerTabController extends DynamicTabController {

        /**
         * redis客户端
         */
        @Getter
        @Accessors(chain = true, fluent = true)
        private RedisClient client;

        /**
         * 发布及订阅tab
         */
        @FXML
        private FXTab pubsub;

        /**
         * 慢查日志tab
         */
        @FXML
        private FXTab slowlog;

        /**
         * 客户端信息tab
         */
        @FXML
        private FXTab clientInfo;

        /**
         * tab面板
         */
        @FXML
        private FlexTabPane tabPane;

        /**
         * 订阅组件
         */
        @FXML
        private RedisPubsubController pubsubController;

        /**
         * 慢查日志组件
         */
        @FXML
        private RedisSlowlogController slowlogController;

        /**
         * 服务信息组件
         */
        @FXML
        private RedisServerInfoController serverInfoController;

        /**
         * 客户端信息组件
         */
        @FXML
        private RedisClientInfoController clientInfoController;

        /**
         * 汇总组件
         */
        @FXML
        private RedisAggregationController aggregationController;

        /**
         * 属性表格
         */
        @FXML
        private FlexTableView<RedisServerItem> propTable;

        /**
         * 服务版本
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> serverVersion;

        /**
         * 服务角色
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> role;

        /**
         * 已用内存
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> usedMemory;

        /**
         * 客户端数量
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> connectedClients;

        /**
         * 已处理命令
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> totalCommandsProcessed;

        /**
         * 正常运行时间
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> uptime;

        /**
         * 键数量
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> keyCount;

        /**
         * 命中率
         */
        @FXML
        private FlexTableColumn<RedisServerItem, String> hitRate;

        /**
         * 刷新任务
         */
        private Future<?> refreshTask;

        /**
         * 服务属性
         */
        private final SimpleObjectProperty<RedisInfoProp> propProperty = new SimpleObjectProperty<>();

        /**
         * 设置redis客户端
         *
         * @param client redis客户端
         */
        public void init(@NonNull RedisClient client) {
            this.client = client;
            if (!client.isSentinelMode()) {
                this.pubsubController.init(client);
                this.slowlogController.init(client);
                this.clientInfoController.init(client);
            } else {
                this.tabPane.removeTab(this.pubsub);
                this.tabPane.removeTab(this.slowlog);
                this.tabPane.removeTab(this.clientInfo);
            }
            this.serverInfoController.init(this.propProperty);
            this.aggregationController.init(this.propProperty);

            this.role.setCellValueFactory(new PropertyValueFactory<>("role"));
            this.uptime.setCellValueFactory(new PropertyValueFactory<>("uptime"));
            this.hitRate.setCellValueFactory(new PropertyValueFactory<>("hitRate"));
            this.keyCount.setCellValueFactory(new PropertyValueFactory<>("keyCount"));
            this.usedMemory.setCellValueFactory(new PropertyValueFactory<>("usedMemory"));
            this.serverVersion.setCellValueFactory(new PropertyValueFactory<>("serverVersion"));
            this.connectedClients.setCellValueFactory(new PropertyValueFactory<>("connectedClients"));
            this.totalCommandsProcessed.setCellValueFactory(new PropertyValueFactory<>("totalCommandsProcessed"));
            this.initRefreshTask();
        }

        /**
         * 初始化自动刷新任务
         */
        private void initRefreshTask() {
            this.refreshTask = ExecutorUtil.start(this::renderPane, 0, 3_000);
            JulLog.debug("RefreshTask started.");
        }

        /**
         * 关闭自动刷新任务
         */
        public void closeRefreshTask() {
            try {
                ExecutorUtil.cancel(this.refreshTask);
                JulLog.debug("RefreshTask closed.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * 渲染主面板
         */
        private void renderPane() {
            try {
                RedisInfoProp infoProp = new RedisInfoProp();
                infoProp.parse(this.client.info(null));
                this.propProperty.set(infoProp);
                RedisServerItem serverItem;
                if (this.propTable.getItems().isEmpty()) {
                    serverItem = new RedisServerItem();
                    serverItem.setServerVersion(infoProp.getRedisVersion());
                    try {
                        serverItem.setRole((String) CollectionUtil.getFirst(this.client.role()));
                    } catch (Exception ignored) {
                    }
                    this.propTable.addItem(serverItem);
                } else {
                    serverItem = this.propTable.getItems().getFirst();
                }
                serverItem.update(
                        infoProp.getUptimeInDays(),
                        infoProp.getUsedMemoryHuman(),
                        infoProp.getTotalCommandsProcessed(),
                        infoProp.getKeyspaceHits(),
                        infoProp.getKeyspaceMisses(),
                        infoProp.keyCount(),
                        infoProp.getConnectedClients()
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
