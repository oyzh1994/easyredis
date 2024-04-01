package cn.oyzh.easyredis.controller;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisPageInfo;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.store.RedisPageInfoStore;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.MouseEvent;
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
        title = "EasyRedis主页",
        iconUrls = RedisConst.ICON_PATH,
        value = RedisConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends ParentController {

    /**
     * 项目信息
     */
    @Autowired
    private Project project;

    /**
     * 头部页面
     */
    @FXML
    private HeaderController headerController;

    /**
     * redis主页业务
     */
    @FXML
    private RedisMainController redisMainController;

    /**
     * 页面信息
     */
    private final RedisPageInfo pageInfo = RedisPageInfoStore.PAGE_INFO;

    /**
     * redis相关配置
     */
    private final RedisSetting setting = RedisSettingStore.SETTING;

    /**
     * 页面信息储存
     */
    private final RedisPageInfoStore pageInfoStore = RedisPageInfoStore.INSTANCE;

    /**
     * 初始化系统托盘
     */
    private void initSystemTray() {
        if (!TrayManager.exist()) {
            try {
                // 初始化托盘
                TrayManager.init(RedisConst.ICON_PATH);
                // 设置标题
                TrayManager.setTitle(this.project.getName() + " v" + this.project.getVersion());
                // 打开主页
                TrayManager.addMenuItem("打开", new SVGGlyph("/font/desktop.svg", "12"), this::showMain);
                // 打开设置
                TrayManager.addMenuItem("设置", new SVGGlyph("/font/setting.svg", "12"), this::showSetting);
                // 退出程序
                TrayManager.addMenuItem("退出", new SVGGlyph("/font/poweroff.svg", "12"), () -> {
                    StaticLog.warn("exit app by tray.");
                    this.exit();
                });
                // 鼠标事件
                TrayManager.onMouseClicked(e -> {
                    // 单击鼠标主键，显示主页
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        this.showMain();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageWrapper StageWrapper = StageUtil.getStage(SettingController.class);
            if (StageWrapper != null) {
                StaticLog.info("front setting.");
                StageWrapper.toFront();
            } else {
                StaticLog.info("show setting.");
                StageUtil.showStage(SettingController.class, this.stage);
            }
        });
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageWrapper StageWrapper = StageUtil.getStage(MainController.class);
            if (StageWrapper != null) {
                StaticLog.info("front main.");
                StageWrapper.toFront();
            } else {
                StaticLog.info("show main.");
                StageUtil.showStage(MainController.class);
            }
        });
    }

    @Override
    public List<Controller> getSubControllers() {
        return Arrays.asList(this.redisMainController, this.headerController);
    }

    @Override
    public void onStageCloseRequest(WindowEvent event) {
        StaticLog.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            StaticLog.info("exit directly.");
            this.exit();
            return;
        }

        // 总是询问
        if (this.setting.isExitAsk()) {
            if (MessageBox.confirm("确定退出" + this.project.getName() + "？")) {
                StaticLog.info("exit by confirm.");
                this.exit();
            } else {
                StaticLog.info("cancel by confirm.");
                event.consume();
            }
            return;
        }

        // 系统托盘
        if (this.setting.isExitTray()) {
            if (TrayManager.exist()) {
                StaticLog.info("show tray.");
                TrayManager.show();
            } else {
                StaticLog.error("tray not support!");
                MessageBox.warn("不支持系统托盘！");
            }
        }
    }

    @Override
    public void onStageShowing(WindowEvent event) {
        super.onStageShowing(event);
        this.stage.setTitleExt(this.project.getName() + "-v" + this.project.getVersion());
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        try {
            this.initSystemTray();
            TrayManager.show();
        } catch (Exception ex) {
            StaticLog.warn("不支持系统托盘!");
            ex.printStackTrace();
        }
    }

    /**
     * 应用退出
     */
    // @EventReceiver(RedisEventTypes.APP_EXIT)
    public void exit() {
        StageUtil.exit();
    }

    @Override
    public void onSystemExit() {
        boolean savePageInfo = false;
        // 记住页面大小
        if (this.setting.isRememberPageSize()) {
            this.pageInfo.setWidth(this.stage.getWidth());
            this.pageInfo.setHeight(this.stage.getHeight());
            this.pageInfo.setMaximized(this.stage.isMaximized());
            savePageInfo = true;
        }
        // 记住页面位置
        if (this.setting.isRememberPageLocation()) {
            this.pageInfo.setScreenX(this.stage.getX());
            this.pageInfo.setScreenY(this.stage.getY());
            savePageInfo = true;
        }
        // 保存页面信息
        if (savePageInfo) {
            this.pageInfoStore.update(this.pageInfo);
        }

        // 关闭托盘
        TrayManager.destroy();
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageWrapper view) {
        super.onStageInitialize(view);
        // 设置上次保存的页面大小
        if (this.setting.isRememberPageSize()) {
            if (this.pageInfo.isMaximized()) {
                this.stage.setMaximized(true);
                StaticLog.debug("view setMaximized");
            } else if (this.pageInfo.getWidth() != null && this.pageInfo.getHeight() != null) {
                this.stage.setWidth(this.pageInfo.getWidth());
                this.stage.setHeight(this.pageInfo.getHeight());
                StaticLog.debug("view setWidth:{} setHeight:{}", this.pageInfo.getWidth(), this.pageInfo.getHeight());
            }
        }
        // 设置上次保存的页面位置
        if (this.setting.isRememberPageLocation() && !this.pageInfo.isMaximized() && this.pageInfo.getScreenX() != null && this.pageInfo.getScreenY() != null) {
            this.stage.setX(this.pageInfo.getScreenX());
            this.stage.setY(this.pageInfo.getScreenY());
            StaticLog.debug("view setX:{} setY:{}", this.pageInfo.getScreenX(), this.pageInfo.getScreenY());
        }
    }
}
