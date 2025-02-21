package cn.oyzh.easyredis.controller;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.controller.connect.RedisAddConnectController;
import cn.oyzh.easyredis.controller.connect.RedisUpdateConnectController;
import cn.oyzh.easyredis.controller.data.RedisExportDataController;
import cn.oyzh.easyredis.controller.data.RedisImportDataController;
import cn.oyzh.easyredis.controller.data.RedisMigrationDataController;
import cn.oyzh.easyredis.controller.data.RedisMigrationTipsController;
import cn.oyzh.easyredis.controller.data.RedisTransportDataController;
import cn.oyzh.easyredis.controller.tool.RedisToolController;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.event.window.RedisShowAboutEvent;
import cn.oyzh.easyredis.event.window.RedisShowAddConnectEvent;
import cn.oyzh.easyredis.event.window.RedisShowExportDataEvent;
import cn.oyzh.easyredis.event.window.RedisShowImportDataEvent;
import cn.oyzh.easyredis.event.window.RedisShowMainEvent;
import cn.oyzh.easyredis.event.window.RedisShowMigrationDataEvent;
import cn.oyzh.easyredis.event.window.RedisShowSettingEvent;
import cn.oyzh.easyredis.event.window.RedisShowToolEvent;
import cn.oyzh.easyredis.event.window.RedisShowTransportDataEvent;
import cn.oyzh.easyredis.event.window.RedisShowUpdateConnectEvent;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.titlebar.TitleBar;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

import java.util.Arrays;
import java.util.List;

/**
 * 主页
 *
 * @author oyzh
 * @since 2023/8/19
 */
@StageAttribute(
        usePrimary = true,
        fullScreenAble = true,
        alwaysOnTopAble = true,
        value = FXConst.FXML_PATH + "main.fxml"
)
public class MainController extends ParentStageController {

    /**
     * 项目信息
     */
    private final Project project = Project.load();

     /**
      * 头部页面
      */
     @FXML
     private HeaderController3 headerController;

    /**
     * redis主页业务
     */
    @FXML
    private RedisMainController redisMainController;

    // /**
    //  * 页面信息
    //  */
    // private final RedisPageInfo pageInfo = RedisPageInfoStore.PAGE_INFO;

    /**
     * redis相关配置
     */
    private final RedisSetting setting = RedisSettingStore.SETTING;

    /**
     * 设置存储
     */
    private final RedisSettingStore settingStore = RedisSettingStore.INSTANCE;

    // /**
    //  * 页面信息储存
    //  */
    // private final RedisPageInfoStore pageInfoStore = RedisPageInfoStore.INSTANCE;

    // /**
    //  * 初始化系统托盘
    //  */
    // private void initSystemTray() {
    //     if (!TrayManager.supported()) {
    //         JulLog.warn("tray is not supported.");
    //         return;
    //     }
    //     if (!TrayManager.exist()) {
    //         try {
    //             // 初始化
    //             TrayManager.init(RedisConst.ICON_PATH);
    //             // 设置标题
    //             TrayManager.setTitle(this.project.getName() + " v" + this.project.getVersion());
    //             // 打开主页
    //             TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
    //             // 打开设置
    //             TrayManager.addMenuItem(new SettingTrayItem("12", this::showSetting));
    //             // 退出程序
    //             TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
    //                 JulLog.warn("exit app by tray.");
    //                 StageManager.exit();
    //             }));
    //             // 鼠标事件
    //             TrayManager.onMouseClicked(e -> {
    //                 // 单击鼠标主键，显示主页
    //                 if (e.getButton() == MouseEvent.BUTTON1) {
    //                     this.showMain();
    //                 }
    //             });
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }

    // /**
    //  * 显示设置
    //  */
    // private void showSetting() {
    //     FXUtil.runLater(() -> {
    //         StageAdapter wrapper = StageManager.getStage(SettingController.class);
    //         if (wrapper != null) {
    //             JulLog.info("front setting.");
    //             wrapper.toFront();
    //         } else {
    //             JulLog.info("show setting.");
    //             StageManager.showStage(SettingController.class, this.stage);
    //         }
    //     });
    // }
    //
    // /**
    //  * 显示主页
    //  */
    // private void showMain() {
    //     FXUtil.runLater(() -> {
    //         StageAdapter wrapper = StageManager.getStage(MainController.class);
    //         if (wrapper != null) {
    //             JulLog.info("front main.");
    //             wrapper.toFront();
    //         } else {
    //             JulLog.info("show main.");
    //             StageManager.showStage(MainController.class);
    //         }
    //     });
    // }

    @Override
    public List<? extends StageController> getSubControllers() {
         return Arrays.asList(this.redisMainController, this.headerController);
//        return Collections.singletonList(this.redisMainController);
    }

    @Override
    public void onWindowCloseRequest(WindowEvent event) {
        JulLog.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            JulLog.info("exit directly.");
            StageManager.exit();
        } else if (this.setting.isExitAsk()) { // 总是询问
            if (MessageBox.confirm(I18nHelper.quit() + " " + this.project.getName())) {
                JulLog.info("exit by confirm.");
                StageManager.exit();
            } else {
                JulLog.info("cancel by confirm.");
                event.consume();
            }
        } else if (this.setting.isExitTray()) {// 系统托盘
            if (TrayManager.exist()) {
                JulLog.info("show tray.");
                TrayManager.show();
            } else {
                JulLog.error("tray not support!");
                // MessageBox.warn(I18nHelper.trayNotSupport());
            }
        }
    }

    @Override
    public void onSystemExit() {
        boolean savePageInfo = false;
        // 记住页面大小
        if (this.setting.isRememberPageSize()) {
            this.setting.setPageWidth(this.stage.getWidth());
            this.setting.setPageHeight(this.stage.getHeight());
            this.setting.setPageMaximized(this.stage.isMaximized());
            savePageInfo = true;
        }
        // 记住页面位置
        if (this.setting.isRememberPageLocation()) {
            this.setting.setPageScreenX(this.stage.getX());
            this.setting.setPageScreenY(this.stage.getY());
            savePageInfo = true;
        }
        // 保存页面信息
        if (savePageInfo) {
            this.settingStore.replace(this.setting);
        }
        // 关闭托盘
        TrayManager.destroy();
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        try {
            super.onStageInitialize(stage);
            // 设置上次保存的页面大小
            if (this.setting.isRememberPageSize()) {
                if (this.setting.isPageMaximized()) {
                    this.stage.setMaximized(true);
                    JulLog.debug("view maximized");
                } else if (this.setting.getPageWidth() != null && this.setting.getPageHeight() != null) {
                    this.stage.setSize(this.setting.getPageWidth(), this.setting.getPageHeight());
                    JulLog.debug("view width:{} height:{}", this.setting.getPageWidth(), this.setting.getPageHeight());
                }
            }
            // 设置上次保存的页面位置
            if (this.setting.isRememberPageLocation() && !this.setting.isPageMaximized() && this.setting.getPageScreenX() != null && this.setting.getPageScreenY() != null) {
                this.stage.setLocation(this.setting.getPageScreenX(), this.setting.getPageScreenY());
                JulLog.debug("view x:{} y:{}", this.setting.getPageScreenX(), this.setting.getPageScreenY());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("onStageInitialize error", ex);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        try {
            super.onWindowShown(event);
            TitleBar titleBar = this.stage.getTitleBar();
            // 加载标题
            if (titleBar != null && !titleBar.isHasContent()) {
                titleBar.loadContent("/fxml/header2.fxml");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("onStageInitialize error", ex);
        }
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("redis.title.main");
    }

    /**
     * 显示主页
     */
    @EventSubscribe
    private void showMain(RedisShowMainEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.getStage(MainController.class);
                if (adapter != null) {
                    JulLog.info("front main.");
                    adapter.toFront();
                } else {
                    JulLog.info("show main.");
                    StageManager.showStage(MainController.class);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示设置
     */
    @EventSubscribe
    private void showSetting(RedisShowSettingEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.getStage(SettingController2.class);
                if (adapter != null) {
                    JulLog.info("front setting.");
                    adapter.toFront();
                } else {
                    JulLog.info("show setting.");
                    StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示传输数据
     */
    @EventSubscribe
    private void transportData(RedisShowTransportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(RedisTransportDataController.class);
                adapter.setProp("sourceInfo", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示导出数据
     */
    @EventSubscribe
    private void exportData(RedisShowExportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(RedisExportDataController.class);
                adapter.setProp("connect", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示导入数据
     */
    @EventSubscribe
    private void importData(RedisShowImportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(RedisImportDataController.class);
                adapter.setProp("connect", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示添加连接
     */
    @EventSubscribe
    private void addConnect(RedisShowAddConnectEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(RedisAddConnectController.class);
                adapter.setProp("group", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示修改连接
     */
    @EventSubscribe
    private void updateConnect(RedisShowUpdateConnectEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(RedisUpdateConnectController.class);
                adapter.setProp("zkConnect", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示工具页面
     */
    @EventSubscribe
    private void tool(RedisShowToolEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(RedisToolController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示关于页面
     */
    @EventSubscribe
    private void about(RedisShowAboutEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(AboutController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示迁移数据页面
     */
    @EventSubscribe
    private void migrationData(RedisShowMigrationDataEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(RedisMigrationDataController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示迁移提示页面
     */
    @EventSubscribe
    private void migrationTips(RedisShowMigrationDataEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(RedisMigrationTipsController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }
}
