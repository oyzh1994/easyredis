package cn.oyzh.easyredis.tabs.home;

import cn.oyzh.common.dto.Project;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.svg.glyph.HomeSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis主页tab
 *
 * @author oyzh
 * @since 2023/6/24
 */
public class RedisHomeTab extends DynamicTab {

    public RedisHomeTab() {
        super();
        super.flush();
    }

    @Override
    protected String url() {
        return "/tabs/home/redisHomeTabContent.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new HomeSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.home");
    }

    /**
     * redis主页tab内容组件
     *
     * @author oyzh
     * @since 2023/6/24
     */
    public static class RedisHomeTabController extends DynamicTabController {

        /**
         * 软件信息
         */
        @FXML
        private FXText softInfo;

        /**
         * 环境信息
         */
        @FXML
        private FXText jdkInfo;

        /**
         * 项目对象
         */
        private final Project project = Project.load();

        @Override
        public void initialize(URL url, ResourceBundle resource) {
            super.initialize(url, resource);
            this.softInfo.setText(I18nHelper.soft() + ": v" + this.project.getVersion() + " Powered by oyzh.");
            String jdkInfo = "";
            if (System.getProperty("java.vm.name") != null) {
                jdkInfo += System.getProperty("java.vm.name");
            }
            if (System.getProperty("java.vm.version") != null) {
                jdkInfo += System.getProperty("java.vm.version");
            }
            this.jdkInfo.setText(I18nHelper.env() + ": " + jdkInfo);
        }

        /**
         * 新增连接
         */
        @FXML
        private void addConnect() {
            RedisEventUtil.addConnect();
        }

        /**
         * 添加分组
         */
        @FXML
        private void addGroup() {
            RedisEventUtil.addGroup();
        }

        /**
         * 打开终端
         */
        @FXML
        private void openTerminal() {
            RedisEventUtil.terminalOpen();
        }

        /**
         * 更新日志
         */
        @FXML
        private void changelog() {
            RedisEventUtil.changelog();
        }
    }
}
