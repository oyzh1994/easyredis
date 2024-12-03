package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.tabs.key.string.RedisStringKeyTab;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeysTreeView;
import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisKeysTab extends DynamicTab {

    public RedisKeysTab(RedisConnectTreeItem treeItem) {
        super();
        super.flush();
        this.controller().init(treeItem);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new FilterSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisKeysTab.fxml";
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.filter.main");
    }

    @Override
    protected RedisKeysTabController controller() {
        return (RedisKeysTabController) super.controller();
    }

    public RedisConnectTreeItem treeItem() {
        return this.controller().treeItem();
    }

    public RedisClient client() {
        return this.controller().client();
    }

    public RedisConnect redisConnect() {
        return this.client().redisInfo();
    }

    /**
     * @author oyzh
     * @since 2024-12-03
     */
    public static class RedisKeysTabController extends DynamicTabController {

        /**
         * tab节点
         */
        @FXML
        private FlexTabPane tabPane;

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisClient client;

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisConnectTreeItem treeItem;

        @FXML
        private RedisKeysTreeView treeView;

        public void init(RedisConnectTreeItem treeItem) {
            try {
                this.treeItem = treeItem;
                this.client = treeItem.client();
                this.treeView.client(this.client);
                // 加载根节点
                this.treeView.loadDatabases();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        }

        public void doSearch(ActionEvent actionEvent) {
        }

        public void sortAsc(MouseEvent event) {
        }

        public void sortDesc(MouseEvent event) {
        }

        public void positionNode(MouseEvent event) {
        }

        @Override
        protected void bindListeners() {
            super.bindListeners();
            // 监听选中变化
            this.treeView.selectItemChanged(this::initItem);
        }

        private RedisKeyTreeItem<?> activeItem;

        public void initItem(TreeItem<?> treeItem) {
            if (treeItem instanceof RedisKeyTreeItem<?> keyTreeItem) {
                try {
                    this.activeItem = keyTreeItem;
                    // 初始化数据
                    this.initData();
                    // 触发事件
                    RedisEventUtil.keySelected(this.activeItem);
                    // 刷新tab
                    this.flushTab();
                    this.tabPane.enable();
                } catch (Exception ex) {
                    MessageBox.exception(ex);
                }
            } else {
                // 禁用组件
                this.tabPane.disable();
            }
        }

        private void initData() {
            if (this.activeItem instanceof RedisStringKeyTreeItem treeItem) {
                RedisStringKeyTab keyTab = new RedisStringKeyTab();
                keyTab.init(treeItem);
                this.tabPane.setTab(keyTab);
            }
        }
    }

}
