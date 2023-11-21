package cn.oyzh.easyredis.trees.stream;

import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
@Slf4j
public class RedisStreamKeyTreeItem extends RedisRowKeyTreeItem<RedisStreamKey, RedisStreamRow> {

    public RedisStreamKeyTreeItem(@NonNull RedisStreamKey value, @NonNull RedisConnectTreeItem root) {
        super(value, root);
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
        this.unsavedNodeData(null);
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }
}
