package cn.oyzh.easyredis.controller;

import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
@Lazy
//@Slf4j
@Component
public class HeaderController extends SubController {

    /**
     * 项目信息
     */
    @Autowired
    private Project project;

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
    private final NodeGroupManage treeMutexes = new NodeGroupManage();

    /**
     * 过滤
     */
    @FXML
    private void filter() {
        // StageWrapper StageWrapper = StageUtil.getStage(RedisFilterMainController.class);
        // if (StageWrapper != null) {
        //     StageWrapper.toFront();
        // } else {
        //     StageUtil.showStage(RedisFilterMainController.class);
        // }
        RedisEventUtil.filterMain();
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        StageWrapper StageWrapper = StageUtil.getStage(RedisInfoTransportController.class);
        if (StageWrapper != null) {
            StageWrapper.toFront();
        } else {
            StageUtil.showStage(RedisInfoTransportController.class);
        }
    }

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageWrapper StageWrapper = StageUtil.getStage(SettingController.class);
        if (StageWrapper != null) {
            StageWrapper.toFront();
        } else {
            StageUtil.showStage(SettingController.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageUtil.showStage(AboutController.class, this.stage);
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm("确定退出" + this.project.getName() + "？")) {
            EventUtil.fire(RedisEventTypes.APP_EXIT);
        }
    }

    /**
     * 收缩左侧redis树
     */
    @FXML
    private void collapseTree() {
        this.treeMutexes.visible(this.expandTree);
        EventUtil.fire(RedisEventTypes.LEFT_COLLAPSE);
    }

    /**
     * 展开左侧redis树
     */
    @FXML
    private void expandTree() {
        this.treeMutexes.visible(this.collapseTree);
        EventUtil.fire(RedisEventTypes.LEFT_EXTEND);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }
}
