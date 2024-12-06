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
        // this.setValue(new RedisKeyTreeItemValue(this));
    }

    @Override
    public boolean saveKeyValue() {
        String value = (String) this.data();
        try {
            if (value != null) {
                this.setKeyValue(value);
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
    protected void setKeyValue(Object value) {
        try {
            if (!Objects.equals(value, this.currentRow.getValue())) {
                this.client().srem(this.dbIndex(), this.key(), this.currentRow.getValue());
            }
            this.client().sadd(this.dbIndex(), this.key(), (String) value);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().srem(this.dbIndex(), this.key(), this.currentRow.getValue());
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
    public void refreshKeyValue() {
        try {
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
            if (!Objects.equals(this.currentRow.getValue(), this.data())) {
                return this.client().sismember(this.dbIndex(), this.key(), (String) this.data());
            }
        }
        return false;
    }

}
