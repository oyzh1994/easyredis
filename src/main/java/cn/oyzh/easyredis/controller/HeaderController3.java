package cn.oyzh.easyredis.controller;

import cn.oyzh.common.dto.Project;
import cn.oyzh.easyredis.controller.data.RedisDataMigrationController;
import cn.oyzh.easyredis.controller.data.RedisDataTransportController;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class HeaderController3 extends StageController {

    /**
     * 项目信息
     */
    private final Project project = Project.load();

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageAdapter wrapper = StageManager.getStage(SettingController2.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(SettingController2.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageManager.showStage(AboutController.class, this.stage);
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm(I18nHelper.quit() + " " + this.project.getName())) {
            StageManager.exit();
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        StageAdapter wrapper = StageManager.getStage(RedisDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(RedisDataTransportController.class);
        }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
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
        StageManager.showStage(RedisDataMigrationController.class);
    }
}
