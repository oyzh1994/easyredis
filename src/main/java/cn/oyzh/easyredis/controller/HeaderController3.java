package cn.oyzh.easyredis.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.system.OSUtil;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.pane.FXPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class HeaderController3 extends StageController {

    /**
     * 设置
     */
    @FXML
    private void setting() {
//        StageAdapter wrapper = StageManager.getStage(SettingController2.class);
//        if (wrapper != null) {
//            wrapper.toFront();
//        } else {
//            StageManager.showStage(SettingController2.class, this.stage);
//        }
        RedisEventUtil.showSetting();
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
//        StageManager.showStage(AboutController.class, this.stage);
        RedisEventUtil.showAbout();
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm(I18nHelper.quit() + " " + SysConst.projectName())) {
            StageManager.exit();
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
//        StageAdapter wrapper = StageManager.getStage(RedisTransportDataController.class);
//        if (wrapper != null) {
//            wrapper.toFront();
//        } else {
//            StageManager.showStage(RedisTransportDataController.class, this.stage);
//        }
        RedisEventUtil.showTransportData();
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
//        StageManager.showStage(RedisToolController.class, this.stage);
        RedisEventUtil.showTool();
    }

    /**
     * 布局1
     */
    @FXML
    private void layout1() {
        RedisEventUtil.layout1();
    }

    /**
     * 布局2
     */
    @FXML
    private void layout2() {
        RedisEventUtil.layout2();
    }

    /**
     * 迁移
     */
    @FXML
    private void migration() {
//        StageManager.showStage(RedisMigrationDataController.class, this.stage);
        RedisEventUtil.showMigrationData();
    }

    /**
     * 分割面板
     */
    @FXML
    private FXPane splitPane;

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        if (OSUtil.isWindows()) {
            this.splitPane.setFlexHeight("100% - 280");
        }
    }
}
