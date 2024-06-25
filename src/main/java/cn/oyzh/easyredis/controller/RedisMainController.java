package cn.oyzh.easyredis.controller;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.domain.RedisPageInfo;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.event.RedisInfoUpdatedEvent;
import cn.oyzh.easyredis.event.RedisLeftCollapseEvent;
import cn.oyzh.easyredis.event.RedisLeftExtendEvent;
import cn.oyzh.easyredis.fx.RedisMsgTextArea;
import cn.oyzh.easyredis.store.RedisPageInfoStore;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.tabs.RedisTabPane;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeEnhance;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * redis主页
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Lazy
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
     * 仅看收藏
     */
    @FXML
    private FlexCheckBox onlyCollect;

    /**
     * redis切换面板
     */
    @FXML
    public RedisTabPane tabPane;

    /**
     * 消息文本框
     */
    @FXML
    private RedisMsgTextArea msgArea;

    /**
     * 过滤hash键
     */
    @FXML
    private FlexCheckBox showHash;

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
     * 搜索Controller
     */
    @FXML
    private SearchController searchController;

    /**
     * 页面信息
     */
    private final RedisPageInfo pageInfo = RedisPageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final RedisPageInfoStore pageInfoStore = RedisPageInfoStore.INSTANCE;

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
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        RedisEventUtil.terminalOpen();
    }

    /**
     * 执行过滤
     */
    private void filter() {
        TaskManager.startDelay("redis:tree:filter", () -> {
            this.tree.disable();
            if (this.onlyCollect.isSelected()) {
                this.tree.itemFilter().setOnlyCollect(true);
                this.tree.itemFilter().setExcludeSetType(false);
                this.tree.itemFilter().setExcludeHashType(false);
                this.tree.itemFilter().setExcludeListType(false);
                this.tree.itemFilter().setExcludeZSetType(false);
                this.tree.itemFilter().setExcludeStringType(false);
                this.tree.itemFilter().setExcludeStreamType(false);
            } else {
                this.tree.itemFilter().setOnlyCollect(false);
                this.tree.itemFilter().setExcludeSetType(!this.showSet.isSelected());
                this.tree.itemFilter().setExcludeListType(!this.showList.isSelected());
                this.tree.itemFilter().setExcludeHashType(!this.showHash.isSelected());
                this.tree.itemFilter().setExcludeZSetType(!this.showZSet.isSelected());
                this.tree.itemFilter().setExcludeStringType(!this.showString.isSelected());
                this.tree.itemFilter().setExcludeStreamType(!this.showStream.isSelected());
            }
            this.tree.filter();
            this.tree.enable();
        }, 100);
    }

    /**
     * redis信息修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onInfoUpdate(RedisInfoUpdatedEvent event) {
        if (this.info == event.data()) {
            this.flushViewTitle(event.data());
        }
    }

    /**
     * 刷新窗口标题
     *
     * @param info redis信息
     */
    private void flushViewTitle(RedisInfo info) {
        if (info != null) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
        this.info = info;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        EventUtil.register(this.tree);
        EventUtil.register(this.tabPane);
        EventUtil.register(this.msgArea);
        this.filter();

        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
        }
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        EventUtil.unregister(this.tree);
        EventUtil.unregister(this.tabPane);
        EventUtil.unregister(this.msgArea);
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
            } else {
                this.showSet.enable();
                this.showZSet.enable();
                this.showHash.enable();
                this.showList.enable();
                this.showString.enable();
                this.showStream.enable();
            }
            this.filter();
        });
        this.showSet.selectedChanged((obs, o, n) -> this.filter());
        this.showHash.selectedChanged((obs, o, n) -> this.filter());
        this.showList.selectedChanged((obs, o, n) -> this.filter());
        this.showZSet.selectedChanged((obs, o, n) -> this.filter());
        this.showString.selectedChanged((obs, o, n) -> this.filter());
        this.showStream.selectedChanged((obs, o, n) -> this.filter());
        this.sortAsc.managedBindVisible();
        this.sortDesc.managedBindVisible();
        // redis树变化事件
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

        // 搜索触发事件
        KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> RedisEventUtil.searchFire()));
        // 刷新触发事件
        KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        // 刷新触发事件
        KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
        if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
            this.flushViewTitle(treeItem.info());
            RedisEventUtil.treeChildSelected(treeItem);
        } else if (item instanceof RedisConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else {
            this.flushViewTitle(null);
        }
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 展开左侧
     */
    @Subscribe
    private void leftExtend(RedisLeftExtendEvent event) {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
    }

    /**
     * 收缩左侧
     */
    @Subscribe
    private void leftCollapse(RedisLeftCollapseEvent event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
    }

    @Override
    public List<SubController> getSubControllers() {
        List<SubController> list = new ArrayList<>();
        list.add(this.searchController);
        return list;
    }

    /**
     * 清空消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }
}
