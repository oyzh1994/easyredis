package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListValue;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisListKeyTreeItem extends RedisRowKeyTreeItem<RedisListValue.RedisListRow> {

    public RedisListKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
        // this.setValue(new RedisKeyTreeItemValue(this));
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
        this.value.valueOfList(value);
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
    public List<RedisListValue.RedisListRow> nodeValue() {
        try {
            List<String> value = this.client().lrange(this.dbIndex(), this.key());
            this.value.valueOfList(value);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return this.value.asListValue().getValue();
    }

}
