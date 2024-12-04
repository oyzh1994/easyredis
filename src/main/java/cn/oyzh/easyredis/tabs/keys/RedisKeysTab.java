package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.keys.RedisKeySearchTextField;
import cn.oyzh.easyredis.fx.keys.RedisKeySearchTypeComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeysTreeView;
import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisKeysTab extends DynamicTab {

    public RedisKeysTab(RedisDatabaseTreeItem treeItem) {
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

    public RedisDatabaseTreeItem treeItem() {
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
        private RedisDatabaseTreeItem treeItem;

        @FXML
        private RedisKeysTreeView treeView;

        @FXML
        private RedisKeySearchTextField searchKW;

        @FXML
        private RedisKeySearchTypeComboBox searchType;

        /**
         * 节点排序(正序)
         */
        @FXML
        private SVGGlyph sortAsc;

        /**
         * 节点排序(倒序)
         */
        @FXML
        private SVGGlyph sortDesc;

        public void init(RedisDatabaseTreeItem treeItem) {
            try {
                this.treeItem = treeItem;
                this.client = treeItem.client();
                this.treeView.dbItem(this.treeItem);
                // 加载根节点
                this.treeView.loadItems();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        }

        @FXML
        private void doSearch() {
            String kw = this.searchKW.getTextTrim();
            int mode = this.searchKW.getSelectedIndex();
            int type = this.searchType.getSelectedIndex();
            this.treeView.itemFilter().setKw(kw);
            this.treeView.itemFilter().setType((byte) type);
            this.treeView.itemFilter().setMatchMode((byte) mode);
            this.treeView.filter();
        }

        @FXML
        private void sortAsc() {
            this.sortAsc.disappear();
            this.sortDesc.display();
            this.treeView.sortAsc();
        }

        @FXML
        private void sortDesc() {
            this.sortDesc.disappear();
            this.sortAsc.display();
            this.treeView.sortDesc();
        }

        @FXML
        private void positionNode() {
            this.treeView.positionItem();
        }

        @Override
        protected void bindListeners() {
            super.bindListeners();
            // 监听选中变化
            this.treeView.selectItemChanged(this::initItem);
            // 搜索处理
            this.searchType.selectedIndexChanged((observable, oldValue, newValue) -> this.doSearch());
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
            if (this.activeItem != null) {
                RedisKeyTab<?> keyTab = RedisKeyTab.ofItem(this.activeItem);
                RedisKeyInfoTab infoTab = new RedisKeyInfoTab(this.activeItem);
                this.tabPane.setTab(keyTab, infoTab);
            }
        }
    }

}
