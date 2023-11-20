package cn.oyzh.easyredis.handler;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.dto.RedisSearchParam;
import cn.oyzh.easyredis.dto.RedisSearchResult;
import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.easyredis.tabs.RedisTabPane;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.BaseTreeItem;
import cn.oyzh.easyredis.trees.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.util.ControlUtil;
import cn.oyzh.fx.plus.util.TreeViewUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * redis主页搜索处理器
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Accessors(chain = true, fluent = true)
public class RedisMainSearchHandler {

    /**
     * 键索引
     */
    private Integer index;

    /**
     * 最后操作
     */
    private String lastAction;

    /**
     * 路径索引
     */
    private Integer pathIndex;

    /**
     * 数据索引
     */
    private Integer dataIndex;

    /**
     * tab组件
     */
    private RedisTabPane tabPane;

    /**
     * 数据组件
     */
    private FlexTextArea dataNode;

    /**
     * 当前搜索键
     */
    @Getter
    private TreeItem<?> currentItem;

    /**
     * 当前搜索参数
     */
    @Getter
    private RedisSearchParam searchParam;

    /**
     * 树组件
     */
    private RedisTreeView treeNode;

    /**
     * 当前搜索结果
     */
    private RedisSearchResult searchResult;

    /**
     * 搜索开始
     */
    public void init(@NonNull RedisTreeView treeNode, @NonNull RedisTabPane tabPane) {
        this.index = 0;
        this.tabPane = tabPane;
        this.treeNode = treeNode;
    }

    /**
     * 清除搜索
     */
    public void clear() {
        this.resetSearch();
        this.currentItem = null;
        this.searchParam = null;
        this.searchResult = null;
    }

    /**
     * 重置搜索
     */
    private void resetSearch() {
        this.index = 0;
        this.updateCurrentItem(null);
        this.pathIndex = null;
        this.dataIndex = null;
        this.lastAction = null;
    }

    /**
     * 预搜索
     *
     * @param param 搜索参数
     */
    public void preSearch(RedisSearchParam param) {
        this.treeNode.disable();
        // 初始化搜索参数
        if (this.searchParam == null || !this.searchParam.equalsTo(param)) {
            this.searchParam = param;
            this.resetSearch();
        }
        // 获取匹配键
        List<TreeItemExt> matchItems = this.getMatchItems();
        // 更新搜索信息
        this.searchResult().setIndex(0);
        this.searchResult().setMatchType(null);
        this.searchResult().setCount(matchItems.size());
        this.treeNode.enable();
    }

    /**
     * 更新搜索结果
     */
    public void updateResult() {
        // 初始化搜索参数
        if (this.searchParam != null) {
            // 获取匹配键
            List<TreeItemExt> matchItems = this.getMatchItems();
            // 更新搜索信息
            this.searchResult().setCount(matchItems.size());
            if (matchItems.isEmpty()) {
                this.searchResult().setIndex(0);
                this.searchResult().setMatchType(null);
            }
        }
    }

    /**
     * 搜索下一个
     *
     * @param param 搜索参数
     */
    public void searchNext(RedisSearchParam param) {
        this.doSearch(param, "next");
    }

    /**
     * 搜索上一个
     *
     * @param param 搜索参数
     */
    public void searchPrev(RedisSearchParam param) {
        this.doSearch(param, "prev");
    }

    /**
     * 替换
     *
     * @param replaceKW 替换词
     * @param onResult  搜索结果回调
     */
    public void replace(String replaceKW, Consumer<Boolean> onResult) {
        // 如果当前键不符合分析条件，则寻找下一个键
        if (!this.dataAnalyse()) {
            do {
                // 获取匹配键
                List<TreeItemExt> matchItems = this.getMatchItems();
                // 如果找不到任何匹配数据的键，则直接回调false
                if (matchItems.parallelStream().noneMatch(TreeItemExt::isMatchData)) {
                    // 更新搜索结果
                    this.updateResult();
                    // 函数回调
                    onResult.accept(false);
                    return;
                }
                // 搜索下一个
                this.searchNext(this.searchParam);
                // 如果当前键匹配数据，则执行数据分析
                if (this.searchResult().isMatchData()) {
                    this.dataAnalyse();
                    break;
                }
            } while (true);
        }
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    if (this.dataNode != null) {
                        // 记录旧的索引位置，替换数据后，这个索引值会变成0
                        int index = this.dataIndex;
                        // 替换选中内容
                        this.dataNode.replaceSelection(replaceKW);
                        // 更新数据索引，防止索引错位导致重复替换
                        int len = replaceKW.length() - this.searchParam.getKw().length();
                        this.dataIndex = index + len;
                    }
                }).onFinish(() -> onResult.accept(true))
                .build();
        // 延迟处理
        ExecutorUtil.start(task, 50);
    }

    /**
     * 执行搜索
     *
     * @param param  搜索参数
     * @param action 操作
     */
    private void doSearch(RedisSearchParam param, String action) {
        // 禁用树
        this.treeNode.disable();
        try {
            // 初始化搜索参数
            if (this.searchParam == null || !this.searchParam.equalsTo(param)) {
                this.searchParam = param;
                this.resetSearch();
            }
            // 获取匹配键
            List<TreeItemExt> matchItems = this.getMatchItems();
            // 更新搜索信息
            this.searchResult().setCount(matchItems.size());
            // 内容为空
            if (matchItems.isEmpty()) {
                // 更新键
                this.updateCurrentItem(null);
                return;
            }
            // 操作不一致，更新索引
            if (this.lastAction != null && !Objects.equals(action, this.lastAction)) {
                this.index = "next".equals(this.lastAction) ? this.index - 2 : this.index + 2;
            }
            // 重置索引位置
            if (this.index >= matchItems.size()) {
                this.index = 0;
            } else if (this.index < 0) {
                this.index = matchItems.size() - 1;
            }
            // 数据排序
            matchItems.sort(Comparator.comparing(TreeItemExt::level));
            // 获取索引数据
            TreeItemExt itemExt = matchItems.get("next".equals(action) ? this.index++ : this.index--);
            // 获取键
            TreeItem<?> item = itemExt.item;
            // 更新键
            this.updateCurrentItem(item);
            // 展开其父键
            TreeViewUtil.expandAll(item.getParent());
            // 选中并滚动到此键
            this.treeNode.selectAndScroll(item);
            // 更新搜索结果及参数
            this.searchResult().setMatchType(itemExt.matchType);
            this.searchResult().setIndex("next".equals(action) ? this.index : this.index + 2);
            this.lastAction = action;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.treeNode.enable();
        }
    }

    /**
     * 获取当前搜索结果
     *
     * @return 搜索结果
     */
    public RedisSearchResult searchResult() {
        if (this.searchResult == null) {
            this.searchResult = new RedisSearchResult();
        }
        return this.searchResult;
    }

    /**
     * 更新当前键
     *
     * @param item 键
     */
    private void updateCurrentItem(TreeItem<?> item) {
        this.pathIndex = 0;
        this.dataIndex = 0;
        // 取消文本选中
        if (this.currentItem != null) {
            RedisTreeItemValue value = (RedisTreeItemValue) this.currentItem.getValue();
            ControlUtil.deselect(value.nodeNameText());
        }
        this.currentItem = item;
        // 取消文本组件的选中
        ControlUtil.deselect(this.dataNode);
        // 清除索引信息
        if (item == null) {
            this.index = 0;
            this.searchResult().setIndex(0);
            this.searchResult().setMatchType(null);
        }
    }

    /**
     * 获取匹配的节点列表
     *
     * @return 匹配的节点列表，扩展了属性
     */
    private List<TreeItemExt> getMatchItems() {
        // 全部节点
        List<TreeItem<?>> allItem = TreeViewUtil.getAllItem(this.treeNode);
        if (CollUtil.isEmpty(allItem)) {
            return Collections.emptyList();
        }
        List<TreeItemExt> items = new ArrayList<>(allItem.size());
        for (TreeItem<?> item : allItem) {
            // 获取匹配类型
            String matchType = this.isMatchParam(item);
            if (matchType != null) {
                // 生成对象
                TreeItemExt itemExt = new TreeItemExt();
                itemExt.item(item);
                itemExt.matchType(matchType);
                itemExt.level(this.treeNode.getTreeItemLevel(item));
                items.add(itemExt);
            }
        }
        return items;
    }

    /**
     * 执行分析
     */
    public void doAnalyse() {
        try {
            // 判断节点是否存在
            if (this.currentItem == null) {
                return;
            }
            // 执行路径分析
            if (this.searchParam.isSearchKey() && this.pathIndex != -100 && this.nameAnalyse()) {
                return;
            }
            // 执行数据分析
            if (this.searchParam.isSearchData() && this.dataIndex != -100 && this.dataAnalyse()) {
                return;
            }
            // 初始化索引及文本组件
            this.dataIndex = 0;
            this.pathIndex = 0;
            RedisTreeItemValue value = (RedisTreeItemValue) this.currentItem.getValue();
            ControlUtil.deselect(this.dataNode);
            ControlUtil.deselect(value.nodeNameText());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 寻找数据节点
     *
     * @return 数据节点
     */
    private FlexTextArea findDataNode() {
        RedisKeyTab<?> itemTab = this.tabPane.getKeyTab();
        if (itemTab != null) {
            return itemTab.getNodeDataNode();
        }
        return null;
    }

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
            Text text = value.nodeNameText();
            String path = value.nodeName();
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

    /**
     * 数据节点分析
     *
     * @return 结果
     */
    private boolean dataAnalyse() {
        try {
            this.dataNode = this.findDataNode();
            if (this.dataNode == null) {
                return false;
            }
            String kw = this.searchParam.getKw();
            String text = this.dataNode.getText();
            // 搜索索引
            int index = TextUtil.findIndex(text, kw, this.dataIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
            if (index != -1) {
                int end = index + kw.length();
                this.dataNode.selectRange(index, end);
                this.dataIndex = end;
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.dataIndex = -100;
        return false;
    }

    /**
     * 是否满足参数
     *
     * @param item 键
     * @return 匹配类型
     */
    private String isMatchParam(TreeItem<?> item) {
        if (item == null || item instanceof RedisDBTreeItem || this.searchParam == null) {
            return null;
        }
        boolean m1 = false, m2 = false;
        // 路径
        if (this.searchParam.isSearchKey() && item instanceof BaseTreeItem treeItem) {
            String value = treeItem.itemValue().nodeName();
            m1 = this.searchParam.isMatch(value);
        }
        // 数据
        if (this.searchParam.isSearchData() && item instanceof RedisStringKeyTreeItem treeItem) {
            String data = (String) treeItem.rawValue();
            m2 = this.searchParam.isMatch(data);
        }
        // 匹配全部
        if (m1 && m2) {
            return "all";
        }
        // 匹配名称
        if (m1) {
            return "name";
        }
        // 匹配数据
        if (m2) {
            return "data";
        }
        return null;
    }

    /**
     * 键扩展
     */
    @Data
    private static class TreeItemExt {

        /**
         * 键等级
         */
        private Integer level;

        /**
         * 键
         */
        private TreeItem<?> item;

        /**
         * 匹配类型
         */
        private String matchType;

        public boolean isMatchData() {
            return Objects.equals(this.matchType, "data") || Objects.equals(this.matchType, "all");
        }
    }
}
