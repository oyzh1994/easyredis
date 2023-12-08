package cn.oyzh.easyredis.trees.type;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/08
 */
public class RedisTypeTreeItem extends RedisTreeItem<RedisTypeTreeItemValue> {

    @Getter
    @Accessors(fluent = true, chain = false)
    private RedisKeyType value;


    private final RedisDBTreeItem parent;

    public RedisTypeTreeItem(RedisDBTreeItem parent, RedisKeyType type) {
        super(parent.getTreeView());
        this.parent = parent;
        this.value = type;
        this.setValue(new RedisTypeTreeItemValue(this));
    }

    /**
     * 刷新值
     */
    private void flushValue() {
        BackgroundService.submitFXLater(() -> this.getValue().flushNum());
    }

    @Override
    public synchronized void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.flushValue();
    }
}
