package cn.oyzh.easyredis.tabs.home;

import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis主页tab内容组件
 *
 * @author oyzh
 * @since 2023/6/24
 */
@Lazy
@Component
public class RedisHomeTabContent implements Initializable {

    /**
     * 软件信息
     */
    @FXML
    private FXLabel softInfo;

    /**
     * 环境信息
     */
    @FXML
    private FXLabel jdkInfo;

    /**
     * 项目对象
     */
    @Autowired
    private Project project;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.softInfo.setText("程序信息 v" + this.project.getVersion() + " Powered by oyzh.");
        String jdkInfo = "";
        if (System.getProperty("java.vm.name") != null) {
            jdkInfo += System.getProperty("java.vm.name");
        }
        if (System.getProperty("java.vm.version") != null) {
            jdkInfo += System.getProperty("java.vm.version");
        }
        this.jdkInfo.setText("环境信息 " + jdkInfo);
    }

    /**
     * 新增连接
     */
    @FXML
    private void addConnect() {
        EventUtil.fire(RedisEventTypes.REDIS_ADD_CONNECT);
    }

    /**
     * 添加分组
     */
    @FXML
    private void addGroup() {
        EventUtil.fire(RedisEventTypes.REDIS_ADD_GROUP);
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        RedisEventUtil.terminalOpen();
    }

}
