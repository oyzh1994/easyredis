package cn.oyzh.easyredis.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisSearchHistoryPopup;
import cn.oyzh.easyredis.search.RedisSearchHandler;
import cn.oyzh.easyredis.store.RedisSearchHistoryStore;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.controls.textfield.SearchTextField;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.search.SearchResult;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * redis搜索子组件
 *
 * @author oyzh
 * @since 2023/4/11
 */
@Lazy
@Component
public class SearchController extends SubController {

    /**
     * 搜索-搜索词
     */
    @FXML
    private SearchTextField searchKW;

    /**
     * 搜索中标志位
     */
    private boolean searching;

    /**
     * 搜索-主面板
     */
    @FXML
    private FlexVBox searchMain;

    /**
     * 搜索-更多2
     */
    @FXML
    private FlexHBox searchMore2;

    /**
     * 搜索-下一个
     */
    @FXML
    private SVGGlyph searchNext;

    /**
     * 搜索-上一个
     */
    @FXML
    private SVGGlyph searchPrev;

    /**
     * 搜索-分析
     */
    @FXML
    private SVGGlyph searchAnalyse;

    /**
     * 搜索-搜索模式
     */
    @FXML
    private FlexCheckBox mode;

    /**
     * 搜索-全文匹配
     */
    @FXML
    private FlexCheckBox fullMatch;

    /**
     * 搜索-匹配大小写
     */
    @FXML
    private FlexCheckBox compareCase;

    /**
     * 搜索-搜索结果
     */
    @FXML
    private FlexText searchResult;

    /**
     * 搜索-更多
     */
    @FXML
    private SVGGlyph showSearchMore;

    /**
     * 搜索-更少
     */
    @FXML
    private SVGGlyph hideSearchMore;

    /**
     * redis树
     */
    private RedisTreeView treeView;

    /**
     * redis主页搜索处理
     */
    @Autowired
    private RedisSearchHandler searchHandler;

    /**
     * 搜索历史储存
     */
    private final RedisSearchHistoryStore historyStore = RedisSearchHistoryStore.INSTANCE;

    /**
     * 搜索历史点击事件
     *
     * @param kw 点击关键词
     */
    @EventReceiver(RedisEventTypes.REDIS_SEARCH_HISTORY_SELECTED)
    private void searchHistorySelected(String kw) {
        if (!this.searchKW.getTextTrim().equals(kw)) {
            this.searchKW.setText(kw);
        }
    }

    /**
     * 搜索-更多
     */
    @FXML
    private void showSearchMore() {
        this.searchMore2.display();
        this.searchMain.setRealHeight(60);
        this.treeView.setFlexHeight("100% - 132");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.display();
        this.showSearchMore.disappear();
    }

    /**
     * 搜索-更少
     */
    @FXML
    private void hideSearchMore() {
        this.searchMore2.disappear();
        this.searchMain.setRealHeight(30);
        this.treeView.setFlexHeight("100% - 92");
        // 重新布局
        this.searchMain.autosize();
        this.hideSearchMore.disappear();
        this.showSearchMore.display();
    }

    /**
     * 搜索-搜索下一个
     */
    @FXML
    private void searchNext() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.searching) {
            return;
        }
        this.searching = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    // 执行搜索下一个
                    this.searchHandler.searchNext(this.getSearchParam());
                    // 更新搜索结果
                    this.updateSearchResult();
                    // 更新搜索历史
                    this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
                })
                .onFinish(() -> this.searching = false)
                .onError(MessageBox::exception)
                .build();
        TaskManager.startDelay("redis:search:searchNext", task, 100);
    }

    /**
     * 搜索-搜索上一个
     */
    @FXML
    private void searchPrev() {
        // 内容为空
        if (this.searchKW.isEmpty() || this.searching) {
            return;
        }
        this.searching = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    // 执行搜索上一个
                    this.searchHandler.searchPrev(this.getSearchParam());
                    // 更新搜索结果
                    this.updateSearchResult();
                    // 更新搜索历史
                    this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
                })
                .onFinish(() -> this.searching = false)
                .onError(MessageBox::exception)
                .build();
        TaskManager.startDelay("redis:search:searchPrev", task, 100);
    }

    /**
     * 预搜索
     */
    private void preSearch() {
        TaskManager.startDelay("redis:search:preSearch", () -> {
            try {
                this.searchCheck();
                this.treeView.disable();
                RedisSearchParam param = this.getSearchParam();
                if (!this.searchNext.isDisable()) {
                    // 执行预搜索
                    this.searchResult.setText("搜索中...");
                    this.searchHandler.preSearch(param);
                    // 触发事件
                    RedisEventUtil.searchStart(param);
                    // 更新搜索结果
                    this.searchResult.setText("");
                    this.updateSearchResult();
                    // 搜索结束
                    RedisEventUtil.searchFinish(param);
                } else {// 搜索结束
                    RedisEventUtil.searchFinish(param);
                }
                this.treeView.enable();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 300);
    }

    /**
     * 搜索分析
     */
    @FXML
    private void searchAnalyse() {
        this.searchHandler.doAnalyse();
    }

    /**
     * 获取搜索参数
     *
     * @return 搜索参数
     */
    private RedisSearchParam getSearchParam() {
        RedisSearchParam searchParam = new RedisSearchParam();
        searchParam.setKw(this.searchKW.getTextTrim());
        searchParam.setFullMatch(this.fullMatch.isSelected());
        searchParam.setMode(this.mode.isSelected() ? 1 : 0);
        searchParam.setCompareCase(this.compareCase.isSelected());
        return searchParam;
    }

    /**
     * 检查搜索配置
     */
    private void searchCheck() {
        try {
            // 搜索相关检查
            this.searchKW.enable();
            if (StrUtil.isBlank(this.searchKW.getText())) {
                this.searchNext.disable();
                this.searchResult.setText("");
                this.searchHandler.clear();
            } else {
                this.searchNext.enable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 更新搜索结果
     */
    private void updateSearchResult() {
        SearchResult result = this.searchHandler.searchResult();
        if (result != null) {
            this.searchResult.setText(result.getIndex() + "/" + result.getCount());
        }
    }

    @Override
    protected void bindListeners() {
        // 搜索相关处理
        this.searchMore2.managedBindVisible();
        this.searchPrev.disableProperty().bind(this.searchNext.disableProperty());
        this.searchAnalyse.disableProperty().bind(this.searchNext.disableProperty());
        this.showSearchMore.managedProperty().bind(this.showSearchMore.visibleProperty());
        this.hideSearchMore.managedProperty().bind(this.hideSearchMore.visibleProperty());
        this.mode.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.fullMatch.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.compareCase.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
        this.searchKW.addTextChangeListener((observable, oldValue, newValue) -> this.preSearch());
        // 搜索触发事件
        KeyListener.listen(this.stage, new KeyHandler().keyType(KeyEvent.KEY_RELEASED).keyCode(KeyCode.F).controlDown(true).handler(e -> {
            this.searchKW.requestFocus();
            this.searchKW.selectEnd();
        }));
    }

    /**
     * 刷新搜索结果
     */
    @EventReceiver(value = RedisEventTypes.TREE_CHILD_CHANGED, async = true, verbose = true)
    public void flushSearchResult() {
        TaskManager.startDelay("redis:search:flushSearchResult", () -> {
            this.searchHandler.updateResult();
            this.updateSearchResult();
        }, 300);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        this.treeView = this.parent().tree;
        // 初始化搜索
        this.searchHandler.init(this.treeView);
        // this.searchHandler.init(this.treeView, this.parent().tabPane);
        this.searchKW.setHistoryPopup(new RedisSearchHistoryPopup());
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
    }

    @Override
    public RedisMainController parent() {
        return (RedisMainController) super.parent();
    }

    /**
     * redis搜索控件按键事件
     *
     * @param e 事件
     */
    @FXML
    private void onSearchKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.searchNext();
        } else if (e.getCode() == KeyCode.UP) {
            String currKW = this.searchKW.getTextTrim();
            List<String> list = this.historyStore.getSearchKw();
            String historyKW = this.getHistoryKW(currKW, list, true);
            if (historyKW != null) {
                this.searchHistorySelected(historyKW);
            }
        } else if (e.getCode() == KeyCode.DOWN) {
            String currKW = this.searchKW.getTextTrim();
            List<String> list = this.historyStore.getSearchKw();
            String historyKW = this.getHistoryKW(currKW, list, false);
            if (historyKW != null) {
                this.searchHistorySelected(historyKW);
            }
        }
    }

    /**
     * 获取历史词汇
     *
     * @param currKW 当前词汇
     * @param list   词汇列表
     * @param isUp   是否向上查找
     * @return 搜索词
     */
    private String getHistoryKW(String currKW, List<String> list, boolean isUp) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String kw;
        if (isUp) {
            // 获取首个
            if (currKW == null) {
                kw = CollUtil.getFirst(list);
            } else {
                int index = list.indexOf(currKW) + 1;
                // 获取最后一个
                if (index >= list.size()) {
                    kw = CollUtil.getLast(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        } else {
            // 获取最后一个
            if (currKW == null) {
                kw = CollUtil.getLast(list);
            } else {
                int index = list.indexOf(currKW) - 1;
                // 获取首个
                if (index <= 0) {
                    kw = CollUtil.getFirst(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        }
        return kw;
    }
}
