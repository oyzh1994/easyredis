package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.redis.RedisListKey;
import cn.oyzh.easyredis.redis.RedisListRow;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisListKeyTreeItem extends RedisRowKeyTreeItem<RedisListKey, RedisListRow> {

    public RedisListKeyTreeItem(@NonNull RedisListKey value, @NonNull RedisConnectTreeItem root) {
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
        this.client().lset(this.dbIndex(), this.key(), this.currentRow.getIndex() - 1, (String) value);
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().lrem(this.dbIndex(), this.key(), this.currentRow.getValue());
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
    public boolean reloadRow() {
        String value = this.client().lindex(this.dbIndex(), this.key(), this.currentRow.getIndex() - 1);
        if (value != null) {
            this.currentRow.setValue(value);
            return true;
        }
        return false;
    }

    @Override
    public void refreshNodeValue() {
        List<String> value = this.client().lrange(this.dbIndex(), this.key());
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

    @Override
    public List<RedisListRow> nodeValue() {
        try {
            List<String> value = this.client().lrange(this.dbIndex(), this.key());
            this.value.value(value);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return this.value.value();
    }
}
