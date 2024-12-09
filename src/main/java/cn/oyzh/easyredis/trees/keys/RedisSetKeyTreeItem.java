package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisSetValue;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * redis set键树节点
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisSetKeyTreeItem extends RedisRowKeyTreeItem<RedisSetValue.RedisSetRow> {

    public RedisSetKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
    }

    @Override
    public RedisSetValue.RedisSetRow data() {
        return (RedisSetValue.RedisSetRow) super.data();
    }

    @Override
    public void data(Object data) {
        if (data instanceof RedisSetValue.RedisSetRow row) {
            super.data(row.clone());
        } else {
            super.clearData();
        }
    }

    @Override
    public void saveKeyValue() {
        RedisSetValue.RedisSetRow row = this.data();
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
        if (value instanceof RedisSetValue.RedisSetRow row) {
            try {
                String rowValue = row.getValue();
                String currentRowValue = this.currentRow.getValue();
                // 删除当前成员
                if (!Objects.equals(rowValue, currentRowValue)) {
                    this.client().srem(this.dbIndex(), this.key(), currentRowValue);
                }
                // 添加成员
                this.client().sadd(this.dbIndex(), this.key(), rowValue);
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().srem(this.dbIndex(), this.key(), this.currentRow.getValue());
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
        try {
            // 更新数据
            Set<String> value = this.client().smembers(this.dbIndex(), this.key());
            this.value.valueOfSet(value);
            // 清空未保存的数据
            this.clearData();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }

    @Override
    public boolean checkRowExists() {
        if (this.isDataUnsaved()) {
            String rowValue = this.data().getValue();
            if (!Objects.equals(this.currentRow.getValue(), rowValue)) {
                return this.client().sismember(this.dbIndex(), this.key(), rowValue);
            }
        }
        return false;
    }

}
