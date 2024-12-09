package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListValue;
import cn.oyzh.easyredis.redis.key.RedisStreamValue;
import cn.oyzh.easyredis.redis.key.RedisStringValue;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisStreamKeyTreeItem extends RedisRowKeyTreeItem<RedisStreamValue.RedisStreamRow> {

    public RedisStreamKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().xdel(this.dbIndex(), this.key(), this.currentRow.getStreamId());
            if (count > 0) {
                this.rows().remove(this.currentRow);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshKeyValue() {
        List<StreamEntry> value = this.client().xrange(this.dbIndex(), this.key());
        this.value.valueOfStream(value);
        this.clearData();
    }

    @Override
    public RedisStreamValue.RedisStreamRow rawValue() {
        return this.currentRow;
    }
}
