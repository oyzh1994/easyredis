package cn.oyzh.easyredis.controller;

import cn.oyzh.common.dto.Project;
import cn.oyzh.easyredis.controller.data.RedisDataMigrationController;
import cn.oyzh.easyredis.controller.data.RedisDataTransportController;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.window.StageAdapter;
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
public class HeaderController extends SubStageController {

    /**
     * 项目信息
     */
    private final Project project = Project.load();

    /**
     * 展开redis树
     */
    @FXML
    private SVGLabel expandTree;

    /**
     * 收缩redis树
     */
    @FXML
    private SVGLabel collapseTree;

    /**
     * redis树互斥器
     */
    private final NodeMutexes treeMutexes = new NodeMutexes();

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageAdapter wrapper = StageManager.getStage(SettingController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(SettingController.class, this.stage);
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
     * 过滤
     */
    @FXML
    private void filter() {
        RedisEventUtil.filterMain();
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
        // StageAdapter wrapper = StageManager.getStage(RedisInfoTransportController.class);
        // if (wrapper != null) {
        //     wrapper.toFront();
        // } else {
        //     StageManager.showStage(RedisInfoTransportController.class);
        // }
        StageAdapter wrapper = StageManager.getStage(RedisDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(RedisDataTransportController.class);
        }
    }

    /**
     * 收缩左侧redis树
     */
    @FXML
    private void collapseTree() {
        this.treeMutexes.visible(this.expandTree);
        RedisEventUtil.leftCollapse();
    }

    /**
     * 展开左侧redis树
     */
    @FXML
    private void expandTree() {
        this.treeMutexes.visible(this.collapseTree);
        RedisEventUtil.leftExtend();
    }

    // /**
    //  * 搜索
    //  */
    // @FXML
    // private void search() {
    //     RedisEventUtil.searchFire();
    // }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }

    /**
     * 迁移
     */
    @FXML
    private void migration() {
        StageManager.showStage(RedisDataMigrationController.class);
    }
}
