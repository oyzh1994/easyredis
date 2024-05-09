package cn.oyzh.easyredis.controller;

import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
@Lazy
@Component
public class HeaderController extends SubController {

    /**
     * 项目信息
     */
    @Resource
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
    private final NodeMutexes treeMutexes = new NodeMutexes();

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageWrapper wrapper = StageUtil.getStage(SettingController.class);
        if (wrapper != null) {
            wrapper.toFront();
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
        if (MessageBox.confirm(I18nResourceBundle.i18nString("base.quit") + this.project.getName())) {
            StageUtil.exit();
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        StageWrapper wrapper = StageUtil.getStage(RedisInfoTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageUtil.showStage(RedisInfoTransportController.class);
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

    /**
     * 搜索
     */
    @FXML
    private void search() {
        RedisEventUtil.searchFire();
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }
}
