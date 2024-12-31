package cn.oyzh.easyredis;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.controller.MainController;
import cn.oyzh.easyredis.controller.SettingController2;
import cn.oyzh.easyredis.controller.data.RedisMigrationTipsController;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.exception.RedisExceptionParser;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.store.RedisStoreUtil;
import cn.oyzh.event.EventFactory;
import cn.oyzh.fx.gui.tray.DesktopTrayItem;
import cn.oyzh.fx.gui.tray.QuitTrayItem;
import cn.oyzh.fx.gui.tray.SettingTrayItem;
import cn.oyzh.fx.plus.event.FxEventBus;
import cn.oyzh.fx.plus.event.FxEventConfig;
import cn.oyzh.fx.plus.ext.FXApplication;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.terminal.TerminalConst;
import cn.oyzh.i18n.I18nManager;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class EasyRedisApp extends FXApplication {

    /**
     * 项目信息
     */
    private static final Project PROJECT = Project.load();

    public static void main(String[] args) {
        SysConst.storeDir(RedisConst.STORE_PATH);
        SysConst.cacheDir(RedisConst.CACHE_PATH);
        SysConst.projectName(PROJECT.getName());
        EventFactory.registerEventBus(FxEventBus.class);
        EventFactory.syncEventConfig(FxEventConfig.SYNC);
        EventFactory.asyncEventConfig(FxEventConfig.ASYNC);
        EventFactory.defaultEventConfig(FxEventConfig.DEFAULT);
        TerminalConst.scanBase("cn.oyzh.easyredis.terminal");
        JulLog.info("项目启动中...");
        launch(EasyRedisApp.class, args);
    }

    @Override
    public void init() {
        try {
            // 储存初始化
            RedisStoreUtil.init();
            // 设置对象
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
            // 调用父类
            super.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        super.start(primaryStage);
        // 显示迁移弹窗
        if (RedisStoreUtil.checkOlder()) {
            FXUtil.runWait(() -> StageManager.showStage(RedisMigrationTipsController.class), 1000);
        }
    }

    @Override
    protected void showMainView() {
        // 显示主页面
        StageManager.showStage(MainController.class);
    }

    // @Override
    // protected String appIcon() {
    //     return RedisConst.ICON_PATH;
    // }
    //
    // @Override
    // protected String appName() {
    //     return PROJECT.getName();
    // }

    @Override
    protected void initSystemTray() {
        if (!TrayManager.supported()) {
            JulLog.warn("tray is not supported.");
            return;
        }
        if (TrayManager.exist()) {
            return;
        }
        try {
            // 初始化
            TrayManager.init(RedisConst.TRAY_ICON_PATH);
            // 设置标题
            TrayManager.setTitle(PROJECT.getName() + " v" + PROJECT.getVersion());
            // 打开主页
            TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
            // 打开设置
            TrayManager.addMenuItem(new SettingTrayItem("12", this::showSetting));
            // 退出程序
            TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
                JulLog.warn("exit app by tray.");
                StageManager.exit();
            }));
            // 鼠标事件
            TrayManager.onMouseClicked(e -> {
                // 单击鼠标主键，显示主页
                if (e.getButton() == MouseEvent.BUTTON1) {
                    this.showMain();
                }
            });
            // 显示托盘
            TrayManager.show();
        } catch (Exception ex) {
            JulLog.warn("不支持系统托盘!", ex);
        }
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(MainController.class);
            if (wrapper != null) {
                JulLog.info("front main.");
                wrapper.toFront();
            } else {
                JulLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        });
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(SettingController2.class);
            if (wrapper != null) {
                JulLog.info("front setting.");
                wrapper.toFront();
            } else {
                JulLog.info("show setting.");
                StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
            }
        });
    }
}
