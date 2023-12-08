package cn.oyzh.easyredis.trees.hylog;

import cn.oyzh.easyredis.redis.key.RedisHyLogKey;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import lombok.NonNull;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisHyLogKeyTreeItem extends RedisKeyTreeItem<RedisHyLogKey,RedisHyLogKeyTreeItemValue> {

    public RedisHyLogKeyTreeItem(@NonNull RedisHyLogKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisHyLogKeyTreeItemValue(this));
    }

    @Override
    public void refreshNodeValue() {
        try {
            Long pfcount = this.client().pfcount(this.dbIndex(), this.key());
            this.value.count(pfcount);
            this.value.value(this.client().get(this.dbIndex(), this.keyBinary()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object rawValue() {
        this.refreshNodeValue();
        return this.value.value();
    }

    /**
     * 获取统计值
     *
     * @return 统计值
     */
    public Long count() {
        return this.value.count();
    }

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    public Integer size() {
        return this.value.size();
    }
}
