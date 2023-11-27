// package cn.oyzh.easyredis.controller.filter;
//
// import cn.hutool.core.map.MapUtil;
// import cn.oyzh.easyredis.RedisConst;
// import cn.oyzh.easyredis.RedisStyle;
// import cn.oyzh.easyredis.domain.RedisFilter;
// import cn.oyzh.easyredis.dto.RedisFilterVO;
// import cn.oyzh.easyredis.event.RedisEventTypes;
// import cn.oyzh.easyredis.store.RedisFilterStore;
// import cn.oyzh.fx.common.dto.Paging;
// import cn.oyzh.fx.plus.controller.Controller;
// import cn.oyzh.fx.plus.controls.PagePane;
// import cn.oyzh.fx.plus.controls.ToggleSwitch;
// import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
// import cn.oyzh.fx.plus.controls.table.FXTableCell;
// import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
// import cn.oyzh.fx.plus.event.EventReceiver;
// import cn.oyzh.fx.plus.event.EventUtil;
// import cn.oyzh.fx.plus.information.MessageBox;
// import cn.oyzh.fx.plus.stage.StageAttribute;
// import cn.oyzh.fx.plus.stage.StageUtil;
// import javafx.fxml.FXML;
// import javafx.scene.Node;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableView;
// import javafx.scene.control.cell.PropertyValueFactory;
// import javafx.scene.layout.HBox;
// import javafx.stage.Modality;
// import javafx.stage.WindowEvent;
// import lombok.NonNull;
//
//
// /**
//  * 过滤配置列表业务
//  *
//  * @author oyzh
//  * @since 2023/06/30
//  */
// //@Slf4j
// @StageAttribute(
//         title = "过滤配置列表",
//         modality = Modality.WINDOW_MODAL,
//         iconUrls = RedisConst.ICON_PATH,
//         cssUrls = RedisStyle.COMMON,
//         value = RedisConst.FXML_BASE_PATH + "filter/redisFilterMain.fxml"
// )
// public class RedisFilterMainController extends Controller {
//
//     /**
//      * 分页组件
//      */
//     @FXML
//     private PagePane<RedisFilter> pagePane;
//
//     /**
//      * 搜索词汇
//      */
//     @FXML
//     private ClearableTextField searchKeyWord;
//
//     /**
//      * 数据列表
//      */
//     @FXML
//     private TableView<RedisFilter> listTable;
//
//     /**
//      * 数据索id列
//      */
//     @FXML
//     private TableColumn<RedisFilterVO, String> index;
//
//     /**
//      * 关键词列
//      */
//     @FXML
//     private TableColumn<RedisFilterVO, String> kw;
//
//     /**
//      * 数据状态列
//      */
//     @FXML
//     private TableColumn<RedisFilterVO, String> enable;
//
//     /**
//      * 数据名称列
//      */
//     @FXML
//     private TableColumn<RedisFilterVO, String> partMatch;
//
//     /**
//      * 数据操作列
//      */
//     @FXML
//     private TableColumn<RedisFilterVO, String> action;
//
//     /**
//      * 分页数据
//      */
//     private Paging<RedisFilter> pageData;
//
//     /**
//      * redis过滤配置储存
//      */
//     private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;
//
//     /**
//      * 初始化数据列表
//      *
//      * @param pageNo 数据页码
//      */
//     private void initDataList(int pageNo) {
//         this.pageData = this.filterStore.getPage(10, MapUtil.of("searchKeyWord", this.searchKeyWord.getText()));
//         this.listTable.getItems().clear();
//         this.listTable.getItems().addAll(RedisFilterVO.convert(this.pageData.page(pageNo)));
//         this.pagePane.setPaging(this.pageData);
//     }
//
//     /**
//      * 初始化列表控件
//      */
//     private void initTable() {
//         // 操作栏初始化
//         this.action.setCellFactory((cell) -> new FXTableCell<>() {
//             private HBox hBox;
//
//             @Override
//             public Node initGraphic() {
//                 if (this.hBox == null) {
//                     // 删除按钮
//                     SVGGlyph del = new SVGGlyph("/font/delete.svg", 14.d);
//                     del.setTipText("删除");
//                     del.setOnMousePrimaryClicked((event) -> deleteInfo(this.getTableItem()));
//                     this.hBox = new HBox(del);
//                     this.hBox.setSpacing(5);
//                 }
//                 return hBox;
//             }
//         });
//
//         // 状态栏初始化
//         this.enable.setCellFactory((cell) -> new FXTableCell<>() {
//             @Override
//             public ToggleSwitch initGraphic() {
//                 RedisFilterVO filterVO = this.getTableItem();
//                 if (filterVO != null) {
//                     ToggleSwitch toggleSwitch = new ToggleSwitch();
//                     toggleSwitch.setRealHeight(20);
//                     toggleSwitch.setSelectedText("已启用");
//                     toggleSwitch.setUnselectedText("已禁用");
//                     toggleSwitch.setSelected(filterVO.isEnable());
//                     toggleSwitch.selectedChanged((abs, o, n) -> {
//                         filterVO.setEnable(n);
//                         if (!filterStore.update(filterVO)) {
//                             MessageBox.warn("修改状态失败！");
//                         } else {
//                             EventUtil.fire(RedisEventTypes.REDIS_KEY_FILTER);
//                         }
//                     });
//                     return toggleSwitch;
//                 }
//                 return null;
//             }
//         });
//
//         // 匹配模式栏初始化
//         this.partMatch.setCellFactory((cell) -> new FXTableCell<>() {
//             @Override
//             public ToggleSwitch initGraphic() {
//                 RedisFilterVO filterVO = this.getTableItem();
//                 if (filterVO != null) {
//                     ToggleSwitch toggleSwitch = new ToggleSwitch();
//                     toggleSwitch.setRealHeight(20);
//                     toggleSwitch.setSelectedText("模糊匹配");
//                     toggleSwitch.setUnselectedText("完全匹配");
//                     toggleSwitch.setSelected(filterVO.isPartMatch());
//                     toggleSwitch.selectedChanged((obs, o, n) -> {
//                         filterVO.setPartMatch(n);
//                         if (!filterStore.update(filterVO)) {
//                             MessageBox.warn("修改匹配方式失败！");
//                         } else if (filterVO.isEnable()) {
//                             EventUtil.fire(RedisEventTypes.REDIS_KEY_FILTER);
//                         }
//                     });
//                     return toggleSwitch;
//                 }
//                 return null;
//             }
//         });
//     }
//
//     /**
//      * 删除过滤配置
//      *
//      * @param info 过滤配置
//      */
//     private void deleteInfo(RedisFilter info) {
//         if (MessageBox.confirm("确定删除此Redis过滤配置？")) {
//             if (this.filterStore.delete(info)) {
//                 EventUtil.fire(RedisEventTypes.REDIS_KEY_FILTER);
//                 this.firstPage();
//             } else {
//                 MessageBox.warn("删除Redis过滤配置失败！");
//             }
//         }
//     }
//
//     /**
//      * 添加过滤配置
//      */
//     @FXML
//     private void toAdd() {
//         StageUtil.showStage(RedisFilterAddController.class, this.stage);
//     }
//
//     /**
//      * 首页
//      */
//     private void firstPage() {
//         this.initDataList(0);
//     }
//
//     /**
//      * 上一页
//      */
//     @FXML
//     private void prevPage() {
//         this.initDataList(this.pageData.currentPage() - 1);
//     }
//
//     /**
//      * 下一页
//      */
//     @FXML
//     private void nextPage() {
//         this.initDataList(this.pageData.currentPage() + 1);
//     }
//
//     @Override
//     public void onStageShown(@NonNull WindowEvent event) {
//         // 注册事件处理
//         EventUtil.register(this);
//         this.kw.setCellValueFactory(new PropertyValueFactory<>("kw"));
//         this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
//         this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
//         // 初始化表单
//         this.initTable();
//         // 显示首页
//         this.firstPage();
//         this.stage.hideOnEscape();
//     }
//
//     @Override
//     public void onStageHidden(WindowEvent event) {
//         // 取消注册事件处理
//         EventUtil.unregister(this);
//     }
//
//     /**
//      * 过滤新增事件
//      */
//     @EventReceiver(RedisEventTypes.REDIS_FILTER_ADDED)
//     private void filterAdded() {
//         this.initDataList(Integer.MAX_VALUE);
//     }
// }
