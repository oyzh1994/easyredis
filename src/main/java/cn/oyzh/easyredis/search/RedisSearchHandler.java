// package cn.oyzh.easyredis.search;
//
// import cn.oyzh.easyredis.trees.RedisTreeItem;
// import cn.oyzh.easyredis.trees.connect.RedisTreeView;
// import cn.oyzh.easyredis.trees.connect.RedisDBTreeItem;
// import cn.oyzh.easyredis.trees.type.RedisTypeTreeItem;
// import cn.oyzh.fx.common.util.TextUtil;
// import cn.oyzh.fx.plus.controls.search.SearchHandler;
// import cn.oyzh.fx.plus.controls.search.SearchParam;
// import cn.oyzh.fx.plus.controls.search.SearchValue;
// import cn.oyzh.fx.plus.trees.RichTreeItemValue;
// import cn.oyzh.fx.plus.util.ControlUtil;
// import javafx.scene.control.TreeItem;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Text;
// import lombok.NonNull;
// import lombok.experimental.Accessors;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
//
// /**
//  * redis主页搜索处理器
//  *
//  * @author oyzh
//  * @since 2023/06/22
//  */
// @Lazy
// @Component
// @Accessors(chain = true, fluent = true)
// public class RedisSearchHandler extends SearchHandler {
//
//     /**
//      * 路径索引
//      */
//     private Integer pathIndex;
//
//     /**
//      * 树组件
//      */
//     private RedisTreeView treeNode;
//
//     /**
//      * 搜索开始
//      */
//     public void init(@NonNull RedisTreeView treeNode) {
//         this.index = 0;
//         this.treeNode = treeNode;
//     }
//
//     @Override
//     public RedisSearchParam searchParam() {
//         return (RedisSearchParam) super.searchParam();
//     }
//
//     @Override
//     protected void resetSearch() {
//         super.resetSearch();
//         this.pathIndex = null;
//     }
//
//     @Override
//     public void preSearch(SearchParam param) {
//         this.treeNode.disable();
//         super.preSearch(param);
//         this.treeNode.enable();
//     }
//
//     @Override
//     protected void doSearch(SearchParam param, String action) {
//         this.treeNode.disable();
//         super.doSearch(param, action);
//         this.treeNode.enable();
//     }
//
//     @Override
//     protected void applyValue(SearchValue value, int index) {
//         super.applyValue(value, index);
//         // 选中并滚动到此节点
//         this.treeNode.selectAndScroll(value.getItem());
//     }
//
//     @Override
//     protected void updateCurrentItem(TreeItem<?> item) {
//         // 取消文本组件的选中
//         this.pathIndex = 0;
//         super.updateCurrentItem(item);
//     }
//
//     @Override
//     protected List<SearchValue> getMatchValues() {
//         return super.getMatchValues(this.treeNode.root());
//     }
//
//     @Override
//     public void doAnalyse() {
//         try {
//             // 判断节点是否存在
//             if (this.currentItem == null) {
//                 return;
//             }
//             // 执行路径分析
//             if (this.pathIndex != -100 && this.nameAnalyse()) {
//                 return;
//             }
//             this.pathIndex = 0;
//             RichTreeItemValue value = (RichTreeItemValue) this.currentItem.getValue();
//             ControlUtil.deselect(value.text());
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//     }
//
//     /**
//      * 名称节点分析
//      *
//      * @return 结果
//      */
//     private boolean nameAnalyse() {
//         try {
//             if (this.currentItem instanceof RedisDBTreeItem || this.currentItem instanceof RedisTypeTreeItem) {
//                 return false;
//             }
//             String kw = this.searchParam.getKw();
//             RichTreeItemValue value = (RichTreeItemValue) this.currentItem.getValue();
//             Text text = value.text();
//             String path = value.name();
//             // 搜索索引
//             int index = TextUtil.findIndex(path, kw, this.pathIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
//             if (index != -1) {
//                 int end = index + kw.length();
//                 text.setSelectionStart(index);
//                 text.setSelectionEnd(end);
//                 text.setSelectionFill(Color.ORANGERED);
//                 this.pathIndex = end;
//                 return true;
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//         this.pathIndex = -100;
//         return false;
//     }
//
//     @Override
//     public String getMatchType(TreeItem<?> item) {
//         if (item == null || item instanceof RedisDBTreeItem || this.searchParam == null) {
//             return null;
//         }
//         boolean m1 = false;
//         // // 路径
//         if (item instanceof RedisTreeItem<?> treeItem) {
//             String value = treeItem.getValue().name();
//             m1 = this.searchParam.isMatch(value);
//         }
//         // 匹配名称
//         if (m1) {
//             return "name";
//         }
//         return null;
//     }
// }
