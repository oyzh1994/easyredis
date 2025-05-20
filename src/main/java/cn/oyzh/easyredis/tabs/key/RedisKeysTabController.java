package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.common.util.CostUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.filter.RedisKeyFilterTextField;
import cn.oyzh.easyredis.filter.RedisKeySearchTypeComboBox;
import cn.oyzh.easyredis.popups.RedisKeyFilterPopupController;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.key.RedisKeyTreeView;
import cn.oyzh.easyredis.util.RedisViewFactory;
import cn.oyzh.fx.gui.svg.pane.CollectSVGPane;
import cn.oyzh.fx.gui.svg.pane.SortSVGPane;
import cn.oyzh.fx.gui.tabs.ParentTabController;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeWidthResizer;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisKeysTabController extends ParentTabController {

    /**
     * 根节点
     */
    @FXML
    private FXHBox root;

    /**
     * tab节点
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * 键数据
     */
    @FXML
    private RedisKeyDataController keyDataController;

    /**
     * 键信息
     */
    @FXML
    private RedisKeyInfoController keyInfoController;

    /**
     * 左侧节点
     */
    @FXML
    private FXVBox leftBox;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * db树节点
     */
    private RedisDatabaseTreeItem treeItem;

    public RedisClient getClient() {
        return client;
    }

    public void setClient(RedisClient client) {
        this.client = client;
    }

    public RedisDatabaseTreeItem getTreeItem() {
        return treeItem;
    }

    public void setTreeItem(RedisDatabaseTreeItem treeItem) {
        this.treeItem = treeItem;
    }

    public RedisKeyTreeItem getActiveItem() {
        return activeItem;
    }

    public void setActiveItem(RedisKeyTreeItem activeItem) {
        this.activeItem = activeItem;
    }

    /**
     * 当前激活的节点
     */
    private RedisKeyTreeItem activeItem;

    /**
     * 节点数
     */
    @FXML
    private RedisKeyTreeView treeView;

    /**
     * 过滤内容
     */
    @FXML
    private RedisKeyFilterTextField filterKW;

    /**
     * 过滤类型
     */
    @FXML
    private RedisKeySearchTypeComboBox filterType;

    /**
     * 收藏面板
     */
    @FXML
    private CollectSVGPane collectPane;

    /**
     * 排序面板
     */
    @FXML
    private SortSVGPane sortPane;

    /**
     * 初始化
     *
     * @param treeItem db节点
     */
    public void init(RedisDatabaseTreeItem treeItem) {
        this.treeItem = treeItem;
        this.treeView.dbItem(this.treeItem);
        this.client = treeItem.client();
        // 加载根节点
        StageManager.showMask(() -> {
            try {
                this.treeView.loadItems();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        });
    }

    @FXML
    private void doFilter() {
        String kw = this.filterKW.getTextTrim();
        // 过滤模式
        byte mode = this.filterKW.filterMode();
        // 过滤范围
        byte scope = this.filterKW.filterScope();
        // 过滤类型
        int type = this.filterType.getSelectedIndex();
        // 设置高亮是否匹配大小写
        this.treeView.setHighlightMatchCase(mode == 3 || mode == 1);
        // 仅在过滤键的情况下设置节点高亮
        if (scope == 2 || scope == 0) {
            this.treeView.setHighlightText(kw);
        } else {
            this.treeView.setHighlightText(null);
        }
//        // 仅在过滤数据的情况下设置内容高亮
//        if (scope == 2 || scope == 1&&this.keyDataController) {
//            this.nodeData.setHighlightText(kw);
//        } else {
//            this.nodeData.setHighlightText(this.dataSearch.getTextTrim());
//        }
        this.treeView.getItemFilter().setKw(kw);
        this.treeView.getItemFilter().setScope(scope);
        this.treeView.getItemFilter().setMatchMode(mode);
        this.treeView.getItemFilter().setType((byte) type);
        this.treeView.filter();
    }

    @FXML
    private void addKey() {
//        StageAdapter adapter = StageManager.parseStage(RedisKeyAddController.class);
//        adapter.setProp("dbItem", this.treeItem);
//        adapter.display();
        RedisViewFactory.addKey(this.treeItem, null);
    }

    @FXML
    private void deleteKey() {
        if (this.activeItem != null) {
            this.activeItem.delete();
        }
    }

    @FXML
    private void collectKey() {
        if (this.activeItem != null) {
            if (this.collectPane.isCollect()) {
                this.activeItem.unCollect();
                this.collectPane.unCollect();
            } else {
                this.activeItem.collect();
                this.collectPane.collect();
            }
        }
    }

    @FXML
    private void refreshKey() {
        StageManager.showMask(() -> {
            try {
                this.treeView.loadItems();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        });
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
        // 过滤处理
        this.filterType.selectedIndexChanged((observable, oldValue, newValue) -> this.doFilter());
        // 拉伸辅助
        NodeWidthResizer resizer = new NodeWidthResizer(this.leftBox, Cursor.DEFAULT, this::resizeLeft);
        resizer.widthLimit(240f, 750f);
        resizer.initResizeEvent();
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

    /**
     * 初始化节点
     *
     * @param treeItem 节点
     */
    private void initItem(TreeItem<?> treeItem) {
        StageManager.showMask(() -> {
            CostUtil.record();
            try {
                if (treeItem instanceof RedisKeyTreeItem keyTreeItem) {
                    // 设置激活节点
                    this.activeItem = keyTreeItem;
                    // 初始化数据
                    this.initData();
                    // 刷新tab
                    this.flushTab();
                    // 启用组件
                    this.tabPane.enable();
                    // 设置焦点
                    this.treeView.focusNode();
                } else {
                    this.tabPane.disable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                CostUtil.printCost();
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData() {
        if (this.activeItem != null) {
            this.keyDataController.init(this.activeItem);
            this.keyInfoController.init(this.activeItem);
            this.collectPane.setCollect(this.activeItem.isCollect());
        }
    }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        this.keyDataController.flushTTL();
    }

    @FXML
    private void sortTree() {
        if (this.sortPane.isAsc()) {
            this.treeView.sortAsc();
            this.sortPane.desc();
        } else {
            this.treeView.sortDesc();
            this.sortPane.asc();
        }
    }

    @Override
    public List<? extends RichTabController> getSubControllers() {
        return List.of(this.keyDataController, this.keyInfoController);
    }

    /**
     * 键过滤
     */
    @FXML
    private void doKeyFilter(MouseEvent event) {
        String filterPattern = this.treeItem.getFilterPattern();
        PopupAdapter popup = PopupManager.parsePopup(RedisKeyFilterPopupController.class);
        popup.setProp("pattern", filterPattern);
        SVGGlyph glyph = (SVGGlyph) event.getSource();
        if (glyph == null) {
            glyph = (SVGGlyph) event.getTarget();
        }
        popup.setSubmitHandler(o -> {
            if (o instanceof String pattern && !StringUtil.equals(pattern, filterPattern)) {
                this.treeItem.setFilterPattern(pattern);
                RedisEventUtil.keyFiltered(this.treeItem);
            }
        });
        popup.showPopup(glyph);
    }
}
