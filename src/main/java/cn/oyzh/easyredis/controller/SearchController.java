// package cn.oyzh.easyredis.controller;
//
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.easyredis.event.RedisEventUtil;
// import cn.oyzh.easyredis.event.RedisSearchFireEvent;
// import cn.oyzh.easyredis.event.TreeChildChangedEvent;
// import cn.oyzh.easyredis.search.RedisSearchHistoryPopup;
// import cn.oyzh.easyredis.trees.connect.RedisTreeView;
// import cn.oyzh.common.thread.Task;
// import cn.oyzh.common.thread.TaskBuilder;
// import cn.oyzh.common.thread.TaskManager;
// import cn.oyzh.event.EventSubscribe;
// import cn.oyzh.fx.plus.controller.SubStageController;
// import cn.oyzh.fx.plus.controls.box.FXVBox;
// import cn.oyzh.fx.plus.controls.button.FXCheckBox;
// import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
// import cn.oyzh.fx.plus.controls.text.FXText;
// import cn.oyzh.i18n.I18nHelper;
// import cn.oyzh.fx.plus.information.MessageBox;
// import cn.oyzh.fx.plus.controls.search.SearchResult;
// import cn.oyzh.fx.plus.controls.search.SearchTextField;
// import com.google.common.eventbus.Subscribe;
// import javafx.fxml.FXML;
// import javafx.stage.WindowEvent;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.Resource;
//
// /**
//  * redis搜索子组件
//  *
//  * @author oyzh
//  * @since 2023/4/11
//  */
// @Lazy
// @Component
// public class SearchController extends SubStageController {
//
//     /**
//      * 搜索-搜索词
//      */
//     @FXML
//     private SearchTextField searchKW;
//
//     /**
//      * 搜索中标志位
//      */
//     private boolean searching;
//
//     /**
//      * 搜索-主面板
//      */
//     @FXML
//     private FXVBox searchMain;
//
//     /**
//      * 搜索-下一个
//      */
//     @FXML
//     private SVGGlyph searchNext;
//
//     /**
//      * 搜索-上一个
//      */
//     @FXML
//     private SVGGlyph searchPrev;
//
//     /**
//      * 搜索-分析
//      */
//     @FXML
//     private SVGGlyph searchAnalyse;
//
//     /**
//      * 搜索-过滤模式
//      */
//     @FXML
//     private FXCheckBox filterMode;
//
//     /**
//      * 搜索-全文匹配
//      */
//     @FXML
//     private FXCheckBox matchFull;
//
//     /**
//      * 搜索-匹配大小写
//      */
//     @FXML
//     private FXCheckBox matchCase;
//
//     /**
//      * 搜索-搜索结果
//      */
//     @FXML
//     private FXText searchResult;
//
//     /**
//      * redis树
//      */
//     private RedisTreeView treeView;
//
//     /**
//      * redis主页搜索处理
//      */
//     @Resource
//     private RedisSearchHandler searchHandler;
//
//     /**
//      * 搜索历史储存
//      */
//     private final RedisSearchHistoryStore historyStore = RedisSearchHistoryStore.INSTANCE;
//
//     /**
//      * 搜索-搜索下一个
//      */
//     @FXML
//     private void searchNext() {
//         // 内容为空
//         if (this.searchKW.isEmpty() || this.searching) {
//             return;
//         }
//         this.searching = true;
//         Task task = TaskBuilder.newBuilder()
//                 .onStart(() -> {
//                     // 执行搜索下一个
//                     this.searchHandler.searchNext(this.getSearchParam());
//                     // 更新搜索结果
//                     this.updateSearchResult();
//                     // 更新搜索历史
//                     this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
//                 })
//                 .onFinish(() -> this.searching = false)
//                 .onError(MessageBox::exception)
//                 .build();
//         TaskManager.startDelay("redis:search:searchNext", task, 100);
//     }
//
//     /**
//      * 搜索-搜索上一个
//      */
//     @FXML
//     private void searchPrev() {
//         // 内容为空
//         if (this.searchKW.isEmpty() || this.searching) {
//             return;
//         }
//         this.searching = true;
//         Task task = TaskBuilder.newBuilder()
//                 .onStart(() -> {
//                     // 执行搜索上一个
//                     this.searchHandler.searchPrev(this.getSearchParam());
//                     // 更新搜索结果
//                     this.updateSearchResult();
//                     // 更新搜索历史
//                     this.historyStore.addSearchHistory(this.searchKW.getTextTrim());
//                 })
//                 .onFinish(() -> this.searching = false)
//                 .onError(MessageBox::exception)
//                 .build();
//         TaskManager.startDelay("redis:search:searchPrev", task, 100);
//     }
//
//     /**
//      * 预搜索
//      */
//     private void preSearch() {
//         TaskManager.startDelay("redis:search:preSearch", () -> {
//             try {
//                 this.searchCheck();
//                 this.treeView.disable();
//                 RedisSearchParam param = this.getSearchParam();
//                 if (!this.searchNext.isDisable()) {
//                     // 执行预搜索
//                     this.searchResult.setText(I18nHelper.searching());
//                     this.searchHandler.preSearch(param);
//                     // 搜索开始
//                     RedisEventUtil.searchStart(param);
//                     // 更新搜索结果
//                     this.searchResult.setText("");
//                     this.updateSearchResult();
//                 } else {// 搜索结束
//                     RedisEventUtil.searchFinish(param);
//                 }
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//             } finally {
//                 this.treeView.enable();
//             }
//         }, 300);
//     }
//
//     /**
//      * 搜索分析
//      */
//     @FXML
//     private void searchAnalyse() {
//         this.searchHandler.doAnalyse();
//     }
//
//     /**
//      * 获取搜索参数
//      *
//      * @return 搜索参数
//      */
//     private RedisSearchParam getSearchParam() {
//         RedisSearchParam searchParam = new RedisSearchParam();
//         searchParam.setMode(this.filterMode.isSelected() ? 1 : 0);
//         searchParam.setKw(this.searchKW.getTextTrim());
//         searchParam.setFullMatch(this.matchFull.isSelected());
//         searchParam.setCompareCase(this.matchCase.isSelected());
//         // 返回搜索参数
//         return searchParam;
//     }
//
//     /**
//      * 检查搜索配置
//      */
//     private void searchCheck() {
//         try {
//             // 搜索相关检查
//             this.searchKW.enable();
//             if (StringUtil.isBlank(this.searchKW.getText())) {
//                 this.searchNext.disable();
//                 this.searchResult.setText("");
//                 this.searchHandler.clear();
//             } else {
//                 this.searchNext.enable();
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//     }
//
//     /**
//      * 更新搜索结果
//      */
//     private void updateSearchResult() {
//         SearchResult result = this.searchHandler.searchResult();
//         if (result != null) {
//             this.searchResult.setText(result.getIndex() + "/" + result.getCount());
//         }
//     }
//
//     @Override
//     protected void bindListeners() {
//         // 搜索相关处理
//         this.searchMain.managedBindVisible();
//         this.searchPrev.disableProperty().bind(this.searchNext.disableProperty());
//         this.searchAnalyse.disableProperty().bind(this.searchNext.disableProperty());
//         this.matchCase.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
//         this.matchFull.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
//         this.filterMode.selectedChanged((observable, oldValue, newValue) -> this.preSearch());
//         this.searchKW.addTextChangeListener((observable, oldValue, newValue) -> this.preSearch());
//
//         // 监听搜索组件显示事件
//         this.searchMain.visibleProperty().addListener((t1, t2, newValue) -> {
//             if (newValue) {
//                 this.preSearch();
//             } else {
//                 this.searchHandler.preSearch(null);
//                 RedisEventUtil.searchFinish(null);
//             }
//         });
//     }
//
//     /**
//      * 刷新搜索结果
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     public void flushSearchResult(TreeChildChangedEvent event) {
//         if (this.treeView.searching()) {
//             TaskManager.startDelay("redis:search:flushSearchResult", () -> {
//                 this.searchHandler.updateResult();
//                 this.updateSearchResult();
//             }, 300);
//         }
//     }
//
//     /**
//      * 搜索触发
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     public void searchFire(RedisSearchFireEvent event) {
//         if (this.searchMain.isVisible()) {
//             this.searchMain.disappear();
//             this.treeView.setFlexHeight("100% - 60");
//         } else {
//             this.searchMain.display();
//             this.treeView.setFlexHeight("100% - 120");
//         }
//     }
//
//     @Override
//     public void onWindowShown(WindowEvent event) {
//         super.onWindowShown(event);
//         this.treeView = this.parent().tree;
//         // 初始化搜索
//         this.searchHandler.init(this.treeView);
//         this.searchKW.setHistoryPopup(new RedisSearchHistoryPopup());
//     }
//
//     @Override
//     public RedisMainController parent() {
//         return (RedisMainController) super.parent();
//     }
// }
