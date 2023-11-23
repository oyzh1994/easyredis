package cn.oyzh.easyredis.controller;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.domain.RedisPageInfo;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisSearchFinishMsg;
import cn.oyzh.easyredis.store.PageInfoStore;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.tabs.RedisTabPane;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItemFilter;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeEnhance;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


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
     * 左侧redis树
     */
    @FXML
    public RedisTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * 大小调整增强
     */
    private ResizeEnhance resizeEnhance;

    // /**
    //  * 倒序排序
    //  */
    // private boolean ascSort;

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
    private FlexCheckBox showHash;

    /**
     * 过滤hyperLogLog键
     */
    @FXML
    private FlexCheckBox showHyLog;

    /**
     * 过滤stream键
     */
    @FXML
    private FlexCheckBox showStream;

    /**
     * 过滤string键
     */
    @FXML
    private FlexCheckBox showString;

    /**
     * 过滤set键
     */
    @FXML
    private FlexCheckBox showSet;

    /**
     * 过滤zset键
     */
    @FXML
    private FlexCheckBox showZSet;

    /**
     * 过滤list键
     */
    @FXML
    private FlexCheckBox showList;

    /**
     * redis切换面板
     */
    @FXML
    public RedisTabPane tabPane;

    /**
     * 页面信息
     */
    private final RedisPageInfo pageInfo = PageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final PageInfoStore pageInfoStore = PageInfoStore.INSTANCE;

    // /**
    //  * 树节点过滤器
    //  */
    // private final RedisTreeItemFilter treeItemFilter = new RedisTreeItemFilter();

    /**
     * 消息文本框
     */
    @FXML
    private MsgTextArea msgArea;

    /**
     * 搜索Controller
     */
    @FXML
    private SearchController searchController;

    // /**
    //  * 对子节点排序
    //  */
    // @FXML
    // private void sortNodes() {
    //     // 设置排序方式
    //     this.ascSort = !this.ascSort;
    //     this.sortAsc.setVisible(!this.ascSort);
    //     this.sortDesc.setVisible(this.ascSort);
    //     this.tree.sortItem(this.ascSort);
    // }

    /**
     * 对子节点排序，正序
     */
    @FXML
    private void sortAsc() {
        this.sortAsc.disappear();
        this.sortDesc.display();
        this.tree.sortAsc();
    }

    /**
     * 对子节点排序，倒序
     */
    @FXML
    private void sortDesc() {
        this.sortDesc.disappear();
        this.sortAsc.display();
        this.tree.sortDesc();
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
        // this.tree.itemFilter(this.treeItemFilter);
        // this.treeItemFilter.initFilters();
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
            this.tabPaneLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.tabPaneLeft.parentAutosize();
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
            this.pageInfo.setMainLeftWidth(this.tabPaneLeft.getMinWidth());
            this.pageInfoStore.update(this.pageInfo);
        }
    }

    @Override
    protected void bindListeners() {
        // 左侧栏业务
        this.onlyCollect.selectedChanged((obs, o, n) -> {
            if (n) {
                this.showSet.disable();
                this.showZSet.disable();
                this.showHash.disable();
                this.showList.disable();
                this.showString.disable();
                this.showStream.disable();
                this.showHyLog.disable();
            } else {
                this.showSet.enable();
                this.showZSet.enable();
                this.showHash.enable();
                this.showList.enable();
                this.showString.enable();
                this.showStream.enable();
                this.showHyLog.enable();
            }
            this.filter();
        });
        this.showSet.selectedChanged((obs, o, n) -> this.filter());
        this.showHash.selectedChanged((obs, o, n) -> this.filter());
        this.showList.selectedChanged((obs, o, n) -> this.filter());
        this.showZSet.selectedChanged((obs, o, n) -> this.filter());
        this.showString.selectedChanged((obs, o, n) -> this.filter());
        this.showStream.selectedChanged((obs, o, n) -> this.filter());
        this.showHyLog.selectedChanged((obs, o, n) -> this.filter());

        this.sortAsc.managedBindVisible();
        this.sortDesc.managedBindVisible();
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
        this.resizeEnhance = new ResizeEnhance(this.tabPaneLeft, Cursor.DEFAULT);
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
        this.tree.itemFilter().initFilters();
        this.filter();
        log.info("REDIS_NODE_FILTER.");
    }

    /**
     * 展开左侧
     */
    @EventReceiver(value = RedisEventTypes.LEFT_EXTEND, async = true, verbose = true)
    private void leftExtend() {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
        log.info("LEFT_EXTEND.");
    }

    /**
     * 收缩左侧
     */
    @EventReceiver(value = RedisEventTypes.LEFT_COLLAPSE, async = true, verbose = true)
    private void leftCollapse() {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
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
    public TreeItem<?> activeItem() {
        return tree.getSelectedItem();
    }

    /**
     * 执行过滤
     */
    private void filter() {
        TaskManager.startDelayTask("redis:tree:filter", () -> {
            this.tree.disable();
            if (this.onlyCollect.isSelected()) {
                this.tree.itemFilter().setOnlyCollect(true);
                this.tree.itemFilter().setExcludeSetType(false);
                this.tree.itemFilter().setExcludeHashType(false);
                this.tree.itemFilter().setExcludeListType(false);
                this.tree.itemFilter().setExcludeZSetType(false);
                this.tree.itemFilter().setExcludeHyLogType(false);
                this.tree.itemFilter().setExcludeStringType(false);
                this.tree.itemFilter().setExcludeStreamType(false);
            } else {
                this.tree.itemFilter().setOnlyCollect(false);
                this.tree.itemFilter().setExcludeSetType(!this.showSet.isSelected());
                this.tree.itemFilter().setExcludeListType(!this.showList.isSelected());
                this.tree.itemFilter().setExcludeHashType(!this.showHash.isSelected());
                this.tree.itemFilter().setExcludeZSetType(!this.showZSet.isSelected());
                this.tree.itemFilter().setExcludeHyLogType(!this.showHyLog.isSelected());
                this.tree.itemFilter().setExcludeStringType(!this.showString.isSelected());
                this.tree.itemFilter().setExcludeStreamType(!this.showStream.isSelected());
            }
            this.tree.filter();
            this.tree.enable();
        }, 100);
    }

    /**
     * 清空消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }

    /**
     * 树节点过滤
     */
    @EventReceiver(value = RedisEventTypes.TREE_CHILD_FILTER, async = true, verbose = true)
    private void onTreeChildFilter() {
        this.tree.itemFilter().initFilters();
        this.filter();
    }

    /**
     * 搜索开始事件
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_START, verbose = true)
    private void onSearchStart() {
        this.tree.itemFilter().setSearchParam(null);
        this.filter();
    }

    /**
     * 搜索结束事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_SEARCH_FINISH, verbose = true)
    private void onSearchFinish(RedisSearchFinishMsg msg) {
        this.tree.itemFilter().setSearchParam(msg.searchParam());
        this.filter();
    }
}
