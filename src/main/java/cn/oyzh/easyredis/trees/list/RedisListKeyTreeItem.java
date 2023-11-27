package cn.oyzh.easyredis.trees.list;

import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.row.RedisListRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/06/30
 */
//@Slf4j
public class RedisListKeyTreeItem extends RedisRowKeyTreeItem<RedisListKey,RedisListKeyTreeItemValue, RedisListRow> {

    public RedisListKeyTreeItem(@NonNull RedisListKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisListKeyTreeItemValue(this));
    }

    @Override
    public boolean saveNodeValue() {
        String value = (String) this.data();
        try {
            if (value != null) {
                this.setNodeValue(value);
                this.currentRow.setValue(value);
                this.clearData();
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
        this.clearData();
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
