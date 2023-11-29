package cn.oyzh.easyredis.search;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.tabs.RedisTabPane;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.search.SearchHandler;
import cn.oyzh.fx.plus.search.SearchParam;
import cn.oyzh.fx.plus.search.SearchValue;
import cn.oyzh.fx.plus.util.ControlUtil;
import cn.oyzh.fx.plus.util.TreeViewUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * redis主页搜索处理器
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Lazy
@Component
@Accessors(chain = true, fluent = true)
public class RedisSearchHandler extends SearchHandler {

    /**
     * 路径索引
     */
    private Integer pathIndex;

    // /**
    //  * 数据索引
    //  */
    // private Integer dataIndex;

    // /**
    //  * tab组件
    //  */
    // private RedisTabPane tabPane;

    // /**
    //  * 数据组件
    //  */
    // private FlexTextArea dataNode;

    /**
     * 树组件
     */
    private RedisTreeView treeNode;

    /**
     * 搜索开始
     */
    public void init(@NonNull RedisTreeView treeNode) {
        this.index = 0;
        this.treeNode = treeNode;
    }

    // /**
    //  * 搜索开始
    //  */
    // public void init(@NonNull RedisTreeView treeNode, @NonNull RedisTabPane tabPane) {
    //     this.index = 0;
    //     this.tabPane = tabPane;
    //     this.treeNode = treeNode;
    // }

    @Override
    public RedisSearchParam searchParam() {
        return (RedisSearchParam) super.searchParam();
    }

    @Override
    protected void resetSearch() {
        super.resetSearch();
        this.pathIndex = null;
    }

    @Override
    public void preSearch(SearchParam param) {
        this.treeNode.disable();
        super.preSearch(param);
        this.treeNode.enable();
    }

    // /**
    //  * 替换
    //  *
    //  * @param replaceKW 替换词
    //  * @param onResult  搜索结果回调
    //  */
    // public void replace(String replaceKW, Consumer<Boolean> onResult) {
    //     // 如果当前键不符合分析条件，则寻找下一个键
    //     if (!this.dataAnalyse()) {
    //         do {
    //             // 获取匹配键
    //             List<TreeItemExt> matchItems = this.getMatchItems();
    //             // 如果找不到任何匹配数据的键，则直接回调false
    //             if (matchItems.parallelStream().noneMatch(TreeItemExt::isMatchData)) {
    //                 // 更新搜索结果
    //                 this.updateResult();
    //                 // 函数回调
    //                 onResult.accept(false);
    //                 return;
    //             }
    //             // 搜索下一个
    //             this.searchNext(this.searchParam);
    //             // 如果当前键匹配数据，则执行数据分析
    //             if (this.searchResult().isMatchData()) {
    //                 this.dataAnalyse();
    //                 break;
    //             }
    //         } while (true);
    //     }
    //     Task task = TaskBuilder.newBuilder()
    //             .onStart(() -> {
    //                 if (this.dataNode != null) {
    //                     // 记录旧的索引位置，替换数据后，这个索引值会变成0
    //                     int index = this.dataIndex;
    //                     // 替换选中内容
    //                     this.dataNode.replaceSelection(replaceKW);
    //                     // 更新数据索引，防止索引错位导致重复替换
    //                     int len = replaceKW.length() - this.searchParam.getKw().length();
    //                     this.dataIndex = index + len;
    //                 }
    //             }).onFinish(() -> onResult.accept(true))
    //             .build();
    //     // 延迟处理
    //     ExecutorUtil.start(task, 50);
    // }

    @Override
    protected void doSearch(SearchParam param, String action) {
        this.treeNode.disable();
        super.doSearch(param, action);
        this.treeNode.enable();
    }

    @Override
    protected void applyValue(SearchValue value, String action) {
        super.applyValue(value, action);
        // 选中并滚动到此节点
        this.treeNode.selectAndScroll(value.getItem());
    }

    @Override
    protected void updateCurrentItem(TreeItem<?> item) {
        // 取消文本组件的选中
        this.pathIndex = 0;
        super.updateCurrentItem(item);
    }

    @Override
    protected List<SearchValue> getMatchValues() {
        return super.getMatchValues(this.treeNode.root());
    }

    @Override
    public void doAnalyse() {
        try {
            // 判断节点是否存在
            if (this.currentItem == null) {
                return;
            }
            // 执行路径分析
            if (this.pathIndex != -100 && this.nameAnalyse()) {
                return;
            }
            // // 执行路径分析
            // if (this.searchParam.isSearchKey() && this.pathIndex != -100 && this.nameAnalyse()) {
            //     return;
            // }
            // // 执行数据分析
            // if (this.searchParam.isSearchData() && this.dataIndex != -100 && this.dataAnalyse()) {
            //     return;
            // }
            // 初始化索引及文本组件
            // this.dataIndex = 0;
            this.pathIndex = 0;
            RedisTreeItemValue value = (RedisTreeItemValue) this.currentItem.getValue();
            // ControlUtil.deselect(this.dataNode);
            ControlUtil.deselect(value.text());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // /**
    //  * 寻找数据节点
    //  *
    //  * @return 数据节点
    //  */
    // private FlexTextArea findDataNode() {
    //     RedisKeyTab<?> itemTab = this.tabPane.getKeyTab();
    //     if (itemTab != null) {
    //         return itemTab.getNodeDataNode();
    //     }
    //     return null;
    // }

    /**
     * 名称节点分析
     *
     * @return 结果
     */
    private boolean nameAnalyse() {
        try {
            if (this.currentItem instanceof RedisDBTreeItem) {
                return false;
            }
            String kw = this.searchParam.getKw();
            RedisTreeItemValue value = (RedisTreeItemValue) this.currentItem.getValue();
            Text text = value.text();
            String path = value.name();
            // 搜索索引
            int index = TextUtil.findIndex(path, kw, this.pathIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
            if (index != -1) {
                int end = index + kw.length();
                text.setSelectionStart(index);
                text.setSelectionEnd(end);
                text.setSelectionFill(Color.ORANGERED);
                this.pathIndex = end;
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.pathIndex = -100;
        return false;
    }
    //
    // /**
    //  * 数据节点分析
    //  *
    //  * @return 结果
    //  */
    // private boolean dataAnalyse() {
    //     try {
    //         this.dataNode = this.findDataNode();
    //         if (this.dataNode == null) {
    //             return false;
    //         }
    //         String kw = this.searchParam.getKw();
    //         String text = this.dataNode.getText();
    //         // 搜索索引
    //         int index = TextUtil.findIndex(text, kw, this.dataIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
    //         if (index != -1) {
    //             int end = index + kw.length();
    //             this.dataNode.selectRange(index, end);
    //             this.dataIndex = end;
    //             return true;
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     this.dataIndex = -100;
    //     return false;
    // }

    @Override
    public String getMatchType(TreeItem<?> item) {
        if (item == null || item instanceof RedisDBTreeItem || this.searchParam == null) {
            return null;
        }
        boolean m1 = false, m2 = false;
        // // 路径
        if (item instanceof RedisTreeItem<?> treeItem) {
            String value = treeItem.getValue().name();
            m1 = this.searchParam.isMatch(value);
        }
        // if (this.searchParam.isSearchKey() && item instanceof RedisTreeItem treeItem) {
        //     String value = treeItem.itemValue().name();
        //     m1 = this.searchParam.isMatch(value);
        // }
        // // 数据
        // if (this.searchParam.isSearchData() && item instanceof RedisStringKeyTreeItem treeItem) {
        //     Object data = treeItem.rawValue();
        //     if (data instanceof String string) {
        //         m2 = this.searchParam.isMatch(string);
        //     }
        // }
        // // 匹配全部
        // if (m1 && m2) {
        //     return "all";
        // }
        // 匹配名称
        if (m1) {
            return "name";
        }
        // // 匹配数据
        // if (m2) {
        //     return "data";
        // }
        return null;
    }
}
