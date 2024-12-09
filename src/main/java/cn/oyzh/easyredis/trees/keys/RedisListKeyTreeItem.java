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
    }

    @Override
    public RedisListValue.RedisListRow data() {
        return (RedisListValue.RedisListRow) super.data();
    }

    @Override
    public void data(Object data) {
        if (data instanceof RedisListValue.RedisListRow row) {
            super.data(row.clone());
        } else {
            super.clearData();
        }
    }

    @Override
    public void saveKeyValue() {
        RedisListValue.RedisListRow row = this.data();
        try {
            if (row != null) {
                // 更新数据
                this.setKeyValue(row);
                // 更新当前行
                this.currentRow.setValue(row.getValue());
                // 清除数据
                this.clearData();
                // 刷新节点
                this.refresh();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void setKeyValue(Object value) {
        // 更新数据
        if (value instanceof RedisListValue.RedisListRow row) {
            this.client().lset(this.dbIndex(), this.key(), row.getIndex() - 1, row.getValue());
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().lrem(this.dbIndex(), this.key(), this.currentRow.getValue());
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
    public boolean reloadRow() {
        String value = this.client().lindex(this.dbIndex(), this.key(), this.currentRow.getIndex() - 1);
        if (value != null) {
            this.currentRow.setValue(value);
            return true;
        }
        return false;
    }

    @Override
    public void refreshKeyValue() {
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
    public List<RedisListValue.RedisListRow> rows() {
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
