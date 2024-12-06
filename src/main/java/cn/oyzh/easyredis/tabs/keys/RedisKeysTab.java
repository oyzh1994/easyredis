package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.key.RedisKeyAddController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.fx.keys.RedisKeySearchTextField;
import cn.oyzh.easyredis.fx.keys.RedisKeySearchTypeComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeysTreeView;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeResizeHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
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
        this.controller().init(treeItem);
        super.flush();
    }

    public void flushData() {
        this.controller().initData();
    }

    @Override
    protected String getTabTitle() {
        String name = this.treeItem().info().getName();
        Integer dbIndex = this.treeItem().dbIndex();
        if (dbIndex != null) {
            name += "@" + dbIndex;
        }
        RedisKeyTreeItem keyItem = this.activeItem();
        if (keyItem != null) {
            name += "#" + keyItem.key();
        }
        return name;
    }


    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph graphic = this.treeItem().itemGraphic();
        if (graphic == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null || !StringUtil.notEquals(glyph.getUrl(), graphic.getUrl())) {
            glyph = graphic.clone();
            glyph.disableTheme();
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph graphic = this.treeItem().itemGraphic();
        if (graphic == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            return;
        }
        if (graphic.getColor() != glyph.getColor()) {
            glyph.setColor(graphic.getColor());
        }
    }

    /**
     * redis键节点
     */
    public RedisKeyTreeItem activeItem() {
        return this.controller().getActiveItem();
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisKeysTab.fxml";
    }

    @Override
    protected RedisKeysTabController controller() {
        return (RedisKeysTabController) super.controller();
    }

    /**
     * ttl更新事件
     */
    public void flushTTL() {
        this.controller().flushTTL();
    }

    public RedisDatabaseTreeItem treeItem() {
        return this.controller().treeItem();
    }

    public int dbIndex() {
        RedisDatabaseTreeItem treeItem = this.treeItem();
        return treeItem == null ? -1 : treeItem.dbIndex();
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

        /**
         * 键信息
         */
        @FXML
        private RedisKeyInfoController keyInfoController;

        /**
         * 左侧节点
         */
        @FXML
        private FlexVBox leftBox;

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisClient client;

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisDatabaseTreeItem treeItem;

        /**
         * 当前激活的节点
         */
        @Getter
        private RedisKeyTreeItem activeItem;

        @FXML
        private RedisKeysTreeView treeView;

        /**
         * 搜索内容
         */
        @FXML
        private RedisKeySearchTextField searchKW;

        /**
         * 搜索类型
         */
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
        private void addNode() {
            StageAdapter fxView = StageManager.parseStage(RedisKeyAddController.class);
            fxView.setProp("dbItem", this.treeItem);
            fxView.display();
        }

        @FXML
        private void refreshNode() {
            this.treeView.loadItems();
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
            // 拉伸辅助
            NodeResizeHelper resizeHelper = new NodeResizeHelper(this.leftBox, Cursor.DEFAULT, this::resizeLeft);
            resizeHelper.widthLimit(240f, 750f);
            resizeHelper.initResizeEvent();
        }

        /**
         * 左侧组件重新布局
         *
         * @param newWidth 新宽度
         */
        private void resizeLeft(Float newWidth) {
            if (newWidth != null && !Float.isNaN(newWidth)) {
                // 设置组件宽
                this.leftBox.setRealWidth(newWidth);
                this.tabPane.setLayoutX(newWidth);
                this.tabPane.setFlexWidth("100% - " + newWidth);
                this.leftBox.parentAutosize();
            }
        }

        public void initItem(TreeItem<?> treeItem) {
            if (treeItem instanceof RedisKeyTreeItem keyTreeItem) {
                try {
                    this.activeItem = keyTreeItem;
                    // 初始化数据
                    this.initData();
                    // // 触发事件
                    // RedisEventUtil.keySelected(this.activeItem);
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

        /**
         * 初始化数据
         */
        private void initData() {
            if (this.activeItem != null) {
                RedisKeyTab<?> keyTab = RedisKeyTab.ofItem(this.activeItem);
                if (this.tabPane.tabSize() == 1) {
                    this.tabPane.addTab(0, keyTab);
                    this.tabPane.select(keyTab);
                } else if (this.tabPane.tabSize() == 2) {
                    this.tabPane.setTab(0, keyTab);
                    this.keyInfoController.init(this.activeItem);
                }
            }
        }

        /**
         * 刷新ttl
         */
        public void flushTTL() {
            RedisKeyTab<?> keyTab = this.tabPane.getTab(0);
            if (keyTab != null) {
                keyTab.flushTTL();
            }
        }
    }

}
