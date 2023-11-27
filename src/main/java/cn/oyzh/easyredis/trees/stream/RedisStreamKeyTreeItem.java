package cn.oyzh.easyredis.trees.stream;

import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
//@Slf4j
public class RedisStreamKeyTreeItem extends RedisRowKeyTreeItem<RedisStreamKey, RedisStreamKeyTreeItemValue, RedisStreamRow> {

    public RedisStreamKeyTreeItem(@NonNull RedisStreamKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisStreamKeyTreeItemValue(this));
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().xdel(this.dbIndex(), this.key(), this.currentRow.getStreamId());
            if (count > 0) {
                this.nodeValue().remove(this.currentRow);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshNodeValue() {
        List<StreamEntry> value = this.client().xrange(this.dbIndex(), this.key());
        this.value.value(value);
        this.clearData();
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }
}
