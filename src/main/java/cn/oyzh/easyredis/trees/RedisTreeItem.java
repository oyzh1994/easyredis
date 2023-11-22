package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.controls.tree.FlexTreeView;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/06/27
 */
@Slf4j
public abstract class RedisTreeItem extends RichTreeItem {

    /**
     * redis树
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisTreeView treeView;

    @Override
    public void treeView(FlexTreeView treeView) {
        this.treeView = (RedisTreeView) treeView;
    }

    /**
     * 当前窗口对象
     *
     * @return 窗口对象
     */
    public Window window() {
        return this.treeView().window();
    }

    // /**
    //  * 开始等待
    //  *
    //  * @param runnable 待执行业务
    //  */
    // public void startWaiting(Runnable runnable) {
    //     if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
    //         glyph.startWaiting(runnable);
    //     }
    // }

    /**
     * 添加多个子节点
     *
     * @param items 节点列表
     */
    public void addChildes(@NonNull List<? extends TreeItem> items) {
        this.getChildren().addAll(items);
        this.sort(this.treeView().sortOrder());
    }

    /**
     * 替换多个子节点
     *
     * @param items 节点列表
     */
    public void replaceChildes(@NonNull List<? extends TreeItem> items) {
        this.getChildren().setAll(items);
        this.sort(this.treeView().sortOrder());
    }

    /**
     * 获取子节点数量
     *
     * @return 子节点数量
     */
    public int getChildrenSize() {
        return this.getChildren().size();
    }

    /**
     * 清空子节点
     */
    public void clearChild() {
        this.getChildren().clear();
    }

    /**
     * 排序
     *
     * @param sortOrder 排序方式
     */
    public void sort(Boolean sortOrder) {
        if (sortOrder != null && !this.isChildEmpty()) {
            // 执行排序
            ObservableList<RedisTreeItem> subs = this.getChildren();
            if (sortOrder) {
                subs.sort((a, b) -> CharSequence.compare(a.itemValue().name(), b.itemValue().name()));
            } else {
                subs.sort((a, b) -> CharSequence.compare(b.itemValue().name(), a.itemValue().name()));
            }
        }
    }

    /**
     * 过滤
     */
    public void filter(@NonNull RedisTreeItemFilter filter) {

    }

    /**
     * 获取节点值
     *
     * @return RedisTreeItemValue
     */
    public RedisTreeItemValue itemValue() {
        return (RedisTreeItemValue) super.getValue();
    }

    /**
     * 设置节点值
     *
     * @param itemValue 节点值
     */
    public void itemValue(RedisTreeItemValue itemValue) {
        super.setValue(itemValue);
    }
}
