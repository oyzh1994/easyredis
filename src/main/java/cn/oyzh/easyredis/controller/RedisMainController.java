package cn.oyzh.easyredis.controller;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.domain.RedisPageInfo;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.trees.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItemFilter;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.store.PageInfoStore;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.tabs.RedisTabPane;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeEnhance;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * redis键主页
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Lazy
@Slf4j
@Component
public class RedisMainController extends ParentController {

    /**
     * 配置对象
     */
    private final RedisSetting setting = RedisSettingStore.SETTING;

    /**
     * 当前激活的redis信息
     */
    private RedisInfo info;

    /**
     * 左侧组件
     */
    @FXML
    private FlexVBox mainLeft;

    /**
     * 左侧redis树
     */
    @FXML
    private RedisTreeView tree;

    /**
     * 大小调整增强
     */
    private ResizeEnhance resizeEnhance;

    /**
     * 倒序排序
     */
    private boolean ascSort;

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

    /**
     * 仅看收藏键
     */
    @FXML
    private FlexCheckBox onlyCollect;

    /**
     * 过滤hash键
     */
    @FXML
    private FlexCheckBox excludeHash;

    /**
     * 过滤hyperLogLog键
     */
    @FXML
    private FlexCheckBox excludeHyperLogLog;

    /**
     * 过滤stream键
     */
    @FXML
    private FlexCheckBox excludeStream;

    /**
     * 过滤string键
     */
    @FXML
    private FlexCheckBox excludeString;

    /**
     * 过滤set键
     */
    @FXML
    private FlexCheckBox excludeSet;

    /**
     * 过滤zset键
     */
    @FXML
    private FlexCheckBox excludeZSet;

    /**
     * 过滤list键
     */
    @FXML
    private FlexCheckBox excludeList;

    /**
     * redis切换面板
     */
    @FXML
    private RedisTabPane tabPane;

    /**
     * 页面信息
     */
    private final RedisPageInfo pageInfo = PageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final PageInfoStore pageInfoStore = PageInfoStore.INSTANCE;

    /**
     * 树节点过滤器
     */
    private final RedisTreeItemFilter treeItemFilter = new RedisTreeItemFilter();

    /**
     * 搜索Controller
     */
    @FXML
    private SearchController searchController;

    /**
     * 对子节点排序
     */
    @FXML
    private void sortNodes() {
        // 设置排序方式
        this.ascSort = !this.ascSort;
        this.sortAsc.setVisible(!this.ascSort);
        this.sortDesc.setVisible(this.ascSort);
        this.tree.sortItem(this.ascSort);
    }

    /**
     * redis信息修改事件
     *
     * @param info redis信息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_INFO_UPDATED, async = true)
    private void onInfoUpdate(RedisInfo info) {
        if (this.info == info) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        }
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
        if (item instanceof RedisKeyTreeItem<?> treeItem) {
            this.nodeTreeItemChanged(treeItem);
            this.connectTreeItemChanged(treeItem.root());
        } else if (item instanceof RedisConnectTreeItem treeItem) {
            this.nodeTreeItemChanged(null);
            this.connectTreeItemChanged(treeItem);
        } else {
            this.nodeTreeItemChanged(null);
            this.connectTreeItemChanged(null);
        }
    }

    /**
     * 连接节点变化事件
     *
     * @param item 连接节点
     */
    private void connectTreeItemChanged(RedisConnectTreeItem item) {
        if (item == null) {
            this.info = null;
            this.stage.restoreTitle();
        } else if (this.info != item.value()) {
            this.info = item.value();
            this.onInfoUpdate(this.info);
        }
        EventUtil.fire(RedisEventTypes.CONNECTION_CHANGED, item);
    }

    /**
     * 树节点变更事件
     *
     * @param item 树节点
     */
    private void nodeTreeItemChanged(RedisKeyTreeItem<?> item) {
        this.tabPane.initKeyTab(item);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        EventUtil.register(this.tree);
        EventUtil.register(this.tabPane);

        // 初始化过滤
        this.tree.itemFilter(this.treeItemFilter);
        this.treeItemFilter.initFilters();
        this.filter();

        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
        }
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
        EventUtil.unregister(this.tree);
        EventUtil.unregister(this.tabPane);
        // 关闭连接
        this.tree.closeConnects();
        // 保存页面拉伸
        this.savePageResize();
        // 取消F5按键监听
        KeyListener.unListenReleased(this.tree, KeyCode.F5);
        KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeMainLeft(Double newWidth) {
        if (newWidth != null && !Double.isNaN(newWidth)) {
            // 设置组件宽
            this.mainLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.mainLeft.parentAutosize();
        }
    }

    @Override
    public void onSystemExit() {
        // 保存页面拉伸
        this.savePageResize();
    }

    /**
     * 保存页面拉伸
     */
    private void savePageResize() {
        if (this.setting.isRememberPageResize()) {
            this.pageInfo.setMainLeftWidth(this.mainLeft.getMinWidth());
            this.pageInfoStore.update(this.pageInfo);
        }
    }

    @Override
    protected void bindListeners() {
        // 左侧栏业务
        this.onlyCollect.selectedChanged((obs, o, n) -> {
            if (n) {
                this.excludeSet.disable();
                this.excludeZSet.disable();
                this.excludeHash.disable();
                this.excludeList.disable();
                this.excludeString.disable();
                this.excludeStream.disable();
                this.excludeHyperLogLog.disable();
            } else {
                this.excludeSet.enable();
                this.excludeZSet.enable();
                this.excludeHash.enable();
                this.excludeList.enable();
                this.excludeString.enable();
                this.excludeStream.enable();
                this.excludeHyperLogLog.enable();
            }
            this.filter();
        });
        this.excludeSet.selectedChanged((obs, o, n) -> this.filter());
        this.excludeHash.selectedChanged((obs, o, n) -> this.filter());
        this.excludeList.selectedChanged((obs, o, n) -> this.filter());
        this.excludeZSet.selectedChanged((obs, o, n) -> this.filter());
        this.excludeString.selectedChanged((obs, o, n) -> this.filter());
        this.excludeStream.selectedChanged((obs, o, n) -> this.filter());
        this.excludeHyperLogLog.selectedChanged((obs, o, n) -> this.filter());

        this.sortAsc.managedProperty().bind(this.sortAsc.visibleProperty());
        this.sortDesc.managedProperty().bind(this.sortDesc.visibleProperty());
        this.tabPane.selectedTabChanged((abs, o, n) -> {
            if (o != null) {
                o.getStyleClass().remove("tab-active");
            }
            if (n != null) {
                n.getStyleClass().add("tab-active");
            }
        });
        // redis树键变化事件
        this.tree.selectItemChanged(this::treeItemChanged);
        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.dragContent(), this.tree.root()::dragFile);
        // 拖动改变redis树大小处理
        this.resizeEnhance = new ResizeEnhance(this.mainLeft, Cursor.DEFAULT);
        this.resizeEnhance.minWidth(390d);
        this.resizeEnhance.maxWidth(800d);
        this.resizeEnhance.triggerThreshold(8d);
        this.resizeEnhance.mouseDragged(event -> {
            double sceneX = event.getSceneX();
            if (this.resizeEnhance.resizeWidthAble(sceneX)) {
                // 左侧组件重新布局
                this.resizeMainLeft(sceneX);
            }
        });
        // 初始化拉伸事件
        this.tree.setOnMouseMoved(this.resizeEnhance.mouseMoved());
        this.resizeEnhance.initResizeEvent();

        // // 监听图标变化事件
        // this.tree.graphicChanged(i -> {
        //     if (i instanceof RedisKeyTreeItem) {
        //         this.tabPane.flushGraphic();
        //     }
        // });

        // 监听节点变化
        this.tree.childChanged(() -> this.searchController.flushSearchResult());

        // 监听F5按键
         KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        this.tabPane.initTerminalTab(null);
    }

    /**
     * 键过滤
     */
    @EventReceiver(value = RedisEventTypes.REDIS_KEY_FILTER, async = true, verbose = true)
    private void keyFilter() {
        this.treeItemFilter.initFilters();
        this.filter();
        log.info("REDIS_NODE_FILTER.");
    }

    /**
     * 展开左侧
     */
    @EventReceiver(value = RedisEventTypes.LEFT_EXTEND, async = true, verbose = true)
    private void leftExtend() {
        this.mainLeft.display();
        double w = this.mainLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.mainLeft.parentAutosize();
        log.info("LEFT_EXTEND.");
    }

    /**
     * 收缩左侧
     */
    @EventReceiver(value = RedisEventTypes.LEFT_COLLAPSE, async = true, verbose = true)
    private void leftCollapse() {
        this.mainLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.mainLeft.parentAutosize();
        log.info("LEFT_COLLAPSE.");
    }

    @Override
    public List<SubController> getSubControllers() {
        return Collections.singletonList(this.searchController);
    }

    /**
     * 当前活跃的redis树节点
     *
     * @return redis树节点
     */
    public RedisKeyTreeItem<?> activeItem() {
        if (this.tabPane.getSelectedItem() instanceof RedisKeyTab<?> nodeTab) {
            return nodeTab.treeItem();
        }
        return null;
    }

    /**
     * 执行过滤
     */
    private void filter() {
        TaskManager.startDelayTask("redis:tree:filter",() -> {
            this.tree.disable();
            if (this.onlyCollect.isSelected()) {
                this.treeItemFilter.setOnlyCollect(true);
                this.treeItemFilter.setExcludeSetType(false);
                this.treeItemFilter.setExcludeHashType(false);
                this.treeItemFilter.setExcludeListType(false);
                this.treeItemFilter.setExcludeZSetType(false);
                this.treeItemFilter.setExcludeStringType(false);
                this.treeItemFilter.setExcludeStreamType(false);
                this.treeItemFilter.setExcludeHyperLogLogType(false);
            } else {
                this.treeItemFilter.setOnlyCollect(false);
                this.treeItemFilter.setExcludeSetType(this.excludeSet.isSelected());
                this.treeItemFilter.setExcludeListType(this.excludeList.isSelected());
                this.treeItemFilter.setExcludeHashType(this.excludeHash.isSelected());
                this.treeItemFilter.setExcludeZSetType(this.excludeZSet.isSelected());
                this.treeItemFilter.setExcludeStringType(this.excludeString.isSelected());
                this.treeItemFilter.setExcludeStreamType(this.excludeStream.isSelected());
                this.treeItemFilter.setExcludeHyperLogLogType(this.excludeHyperLogLog.isSelected());
            }
            this.tree.filterItem();
            this.tree.enable();
        }, 100);
    }
}
