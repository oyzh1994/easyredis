package cn.oyzh.easyredis;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.system.OSUtil;
import cn.oyzh.common.system.SystemUtil;
import cn.oyzh.easyredis.controller.data.RedisMigrationTipsController;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.exception.RedisExceptionParser;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.store.RedisStoreUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalManager;
import cn.oyzh.easyredis.util.RedisViewFactory;
import cn.oyzh.event.EventFactory;
import cn.oyzh.event.EventListener;
import cn.oyzh.fx.gui.tray.DesktopTrayItem;
import cn.oyzh.fx.gui.tray.QuitTrayItem;
import cn.oyzh.fx.gui.tray.SettingTrayItem;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.event.FxEventBus;
import cn.oyzh.fx.plus.event.FxEventConfig;
import cn.oyzh.fx.plus.ext.FXApplication;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.terminal.util.TerminalManager;
import cn.oyzh.i18n.I18nManager;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class EasyRedisApp extends FXApplication implements EventListener {

    /**
     * 项目信息
     */
    private static final Project PROJECT = Project.load();

    public static void main(String[] args) {
        try {
            // 开启fx的预览功能
            System.setProperty("javafx.enablePreview", "true");
            System.setProperty("javafx.suppressPreviewWarning", "true");
            SysConst.projectName(PROJECT.getName());
            SysConst.storeDir(RedisConst.getStorePath());
            SysConst.cacheDir(RedisConst.getCachePath());
            JulLog.info("项目启动中...");
            // 储存初始化
            RedisStoreUtil.init();
            if (OSUtil.isWindows()) {
                FXConst.appIcon(RedisConst.ICON_32_PATH);
            } else {
                FXConst.appIcon(RedisConst.ICON_PATH);
            }
            EventFactory.registerEventBus(FxEventBus.class);
            EventFactory.syncEventConfig(FxEventConfig.SYNC);
            EventFactory.asyncEventConfig(FxEventConfig.ASYNC);
            EventFactory.defaultEventConfig(FxEventConfig.DEFAULT);
            // TerminalConst.scanBase("cn.oyzh.easyredis.terminal");
            // 初始化时区处理器
            // System.setProperty("java.time.zone.DefaultZoneRulesProvider", LocalZoneRulesProvider.class.getName());
            launch(EasyRedisApp.class, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void init() {
        try {
            // fx程序实例
            FXConst.INSTANCE = this;
            // 日志开始
            JulLog.info("{} init start.", SysConst.projectName());
            // 禁用fx的css日志
            FXUtil.disableCSSLogger();
            // 配置对象
            RedisSetting setting = RedisSettingStore.SETTING;
            // 应用区域
            I18nManager.apply(setting.getLocale());
            // 应用字体
            FontManager.apply(setting.fontConfig());
            // 应用主题
            ThemeManager.apply(setting.themeConfig());
            // 应用透明度
            OpacityManager.apply(setting.opacityConfig());
            // 注册异常处理器
            MessageBox.registerExceptionParser(RedisExceptionParser.INSTANCE);
            // 注册事件处理
            EventListener.super.register();
            // 调用父类
            super.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            super.start(primaryStage);
            // 注册命令
            TerminalManager.setLoadHandlerAction(RedisTerminalManager::registerHandlers);
            // 显示迁移弹窗
            if (RedisStoreUtil.checkOlder()) {
//                FXUtil.runWait(() -> StageManager.showStage(RedisMigrationTipsController.class), 1000);
                this.migrationTips();
            }
            // 开启定期gc
            SystemUtil.gcInterval(60_000);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("start error", ex);
        }
    }

    @Override
    public void stop() {
        // 储存销毁
        RedisStoreUtil.destroy();
        EventListener.super.unregister();
        super.stop();
    }

    @Override
    protected void showMainView() {
//        try {
//            // 显示主页面
//            StageManager.showStage(MainController.class);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JulLog.warn("showMainView error", ex);
//        }
        RedisViewFactory.main();
    }

    @Override
    protected void initSystemTray() {
        try {
            if (!TrayManager.supported()) {
                JulLog.warn("tray is not supported.");
                return;
            }
            if (TrayManager.exist()) {
                return;
            }
            // 初始化
            if (OSUtil.isWindows()) {
                TrayManager.init(RedisConst.ICON_24_PATH);
            } else {
                TrayManager.init(RedisConst.ICON_PATH);
            }
            // 设置标题
            TrayManager.setTitle(PROJECT.getName() + " v" + PROJECT.getVersion());
            // 打开主页
            TrayManager.addMenuItem(new DesktopTrayItem("12", RedisViewFactory::main));
            // 打开设置
            TrayManager.addMenuItem(new SettingTrayItem("12", RedisViewFactory::setting));
            // 退出程序
            TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
                JulLog.warn("exit app by tray.");
                StageManager.exit();
            }));
            // 鼠标事件
            TrayManager.onMouseClicked(e -> {
                // 单击鼠标主键，显示主页
                if (e.getButton() == MouseEvent.BUTTON1) {
                    RedisViewFactory.main();
                }
            });
            // 显示托盘
            TrayManager.show();
        } catch (Exception ex) {
            JulLog.warn("不支持系统托盘!", ex);
        }
    }

//    /**
//     * 显示主页
//     */
//    private void showMain() {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.getStage(MainController.class);
//                if (adapter != null) {
//                    JulLog.info("front main.");
//                    adapter.toFront();
//                } else {
//                    JulLog.info("show main.");
//                    StageManager.showStage(MainController.class);
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示设置
//     */
//    @EventSubscribe
//    private void showSetting(RedisShowSettingEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.getStage(SettingController2.class);
//                if (adapter != null) {
//                    JulLog.info("front setting.");
//                    adapter.toFront();
//                } else {
//                    JulLog.info("show setting.");
//                    StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示传输数据
//     */
//    @EventSubscribe
//    private void transportData(RedisShowTransportDataEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisTransportDataController.class);
//                adapter.setProp("sourceConnect", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示导出数据
//     */
//    @EventSubscribe
//    private void exportData(RedisShowExportDataEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisExportDataController.class);
//                adapter.setProp("connect", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示导入数据
//     */
//    @EventSubscribe
//    private void importData(RedisShowImportDataEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisImportDataController.class);
//                adapter.setProp("connect", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示添加连接
//     */
//    @EventSubscribe
//    private void addConnect(RedisShowAddConnectEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisAddConnectController.class);
//                adapter.setProp("group", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示修改连接
//     */
//    @EventSubscribe
//    private void updateConnect(RedisShowUpdateConnectEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisUpdateConnectController.class);
//                adapter.setProp("redisConnect", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示工具页面
//     */
//    @EventSubscribe
//    private void tool(RedisShowToolEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageManager.showStage(RedisToolController.class, StageManager.getPrimaryStage());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示关于页面
//     */
//    @EventSubscribe
//    private void about(RedisShowAboutEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageManager.showStage(AboutController.class, StageManager.getPrimaryStage());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示迁移数据页面
//     */
//    @EventSubscribe
//    private void migrationData(RedisShowMigrationDataEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageManager.showStage(RedisMigrationDataController.class, StageManager.getPrimaryStage());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

    /**
     * 显示迁移提示页面
     */
    private void migrationTips() {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(RedisMigrationTipsController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        });
    }

//    /**
//     * 显示添加键页面
//     */
//    @EventSubscribe
//    private void addKey(RedisShowAddKeyEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisKeyAddController.class);
//                adapter.setProp("dbItem", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }
//
//    /**
//     * 显示键ttl页面
//     */
//    @EventSubscribe
//    private void ttlKey(RedisShowTTLKeyEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisKeyTTLController.class);
//                adapter.setProp("treeItem", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示导出连接页面
//     */
//    @EventSubscribe
//    private void exportConnect(RedisShowExportConnectEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageManager.showStage(RedisExportConnectController.class, StageManager.getPrimaryStage());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }

//    /**
//     * 显示导入连接页面
//     */
//    @EventSubscribe
//    private void importConnect(RedisShowImportConnectEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter adapter = StageManager.parseStage(RedisImportConnectController.class, StageManager.getPrimaryStage());
//                adapter.setProp("file", event.data());
//                adapter.display();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex);
//            }
//        });
//    }
}
