package cn.oyzh.easyredis.trees.type;

import cn.oyzh.easyredis.controller.key.RedisKeyAddController;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.treeView.RichTreeItemFilter;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.thread.BackgroundService;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/12/08
 */
public class RedisTypeTreeItem extends RedisTreeItem<RedisTypeTreeItemValue> {

    /**
     * 键类型
     */
    @Getter
    @Accessors(fluent = true, chain = false)
    private final RedisKeyType value;

    /**
     * 父节点
     */
    private final RedisDBTreeItem parent;

    public RedisTypeTreeItem(RedisDBTreeItem parent, RedisKeyType type) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = type;
        this.setValue(new RedisTypeTreeItemValue(this));
    }

    // /**
    //  * 刷新值
    //  */
    // private void flushValue() {
    //     BackgroundService.submitFXLater(() -> {
    //         this.getValue().flushNum();
    //         this.getValue().flushGraphicColor();
    //     });
    // }

    // @Override
    // public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //     super.doFilter(itemFilter);
    //     this.flushValue();
    // }

    /**
     * 是否流类型
     *
     * @return 结果
     */
    public boolean isStreamType() {
        return this.value == RedisKeyType.STREAM;
    }

    /**
     * 是否字符串类型
     *
     * @return 结果
     */
    public boolean isStringType() {
        return this.value == RedisKeyType.STRING;
    }

    /**
     * 是否有序集合类型
     *
     * @return 结果
     */
    public boolean isZSetType() {
        return this.value == RedisKeyType.ZSET;
    }

    /**
     * 是否集合类型
     *
     * @return 结果
     */
    public boolean isSetType() {
        return this.value == RedisKeyType.SET;
    }

    /**
     * 是否列表类型
     *
     * @return 结果
     */
    public boolean isListType() {
        return this.value == RedisKeyType.LIST;
    }

    /**
     * 是否哈希表类型
     *
     * @return 结果
     */
    public boolean isHashType() {
        return this.value == RedisKeyType.HASH;
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addKey("12", this::addKey);
        items.add(add);
        return items;
    }

    /**
     * 添加键
     */
    public void addKey() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyAddController.class, this.window());
        fxView.setProp("type", this.value);
        fxView.setProp("dbItem", this.parent);
        fxView.display();
    }
}
