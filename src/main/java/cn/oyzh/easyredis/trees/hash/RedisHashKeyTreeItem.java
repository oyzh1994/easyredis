package cn.oyzh.easyredis.trees.hash;

import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisHashKeyTreeItem extends RedisRowKeyTreeItem<RedisHashKey, RedisHashRow> {

    public RedisHashKeyTreeItem(@NonNull RedisHashKey value, @NonNull RedisConnectTreeItem root) {
        super(value, root);
    }

    @Override
    public boolean saveNodeValue() {
        String value = (String) this.unsavedNodeData();
        try {
            if (value != null) {
                this.setNodeValue(value);
                this.currentRow.setValue(value);
                this.unsavedNodeData(null);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    protected void setNodeValue(Object value) {
        this.client().hset(this.dbIndex(), this.key(), this.currentRow.getField(), (String) value);
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().hdel(this.dbIndex(), this.key(), this.currentRow.getField());
            if (count > 0) {
                this.nodeValue().remove(this.currentRow);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshNodeValue() {
        Map<String, String> value = this.client().hgetAll(this.dbIndex(), this.key());
        this.value.value(value);
        // 清空未保存的数据
        this.unsavedNodeData(null);
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }

    @Override
    public boolean reloadRow() {
        String value = this.client().hget(this.dbIndex(), this.key(), this.currentRow.getField());
        if (value != null) {
            this.currentRow.setValue(value);
            return true;
        }
        return false;
    }
}
