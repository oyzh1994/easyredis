package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.redis.RedisHyperLogLogKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisHyperLogLogKeyTreeItem extends RedisKeyTreeItem<RedisHyperLogLogKey> {

    public RedisHyperLogLogKeyTreeItem(@NonNull RedisHyperLogLogKey value, @NonNull RedisConnectTreeItem root) {
        super(value, root);
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
