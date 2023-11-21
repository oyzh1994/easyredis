package cn.oyzh.easyredis.trees.set;

import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * redis set键树节点
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisSetKeyTreeItem extends RedisRowKeyTreeItem<RedisSetKey, RedisSetRow> {

    public RedisSetKeyTreeItem(@NonNull RedisSetKey value, @NonNull RedisConnectTreeItem root) {
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
    public void refreshNodeValue() {
        try {
            Set<String> value = this.client().smembers(this.dbIndex(), this.key());
            this.value.value(value);
            // 清空未保存的数据
            this.unsavedNodeData(null);
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
    public boolean checkExists() {
        if (this.unsavedNodeData() == null) {
            return false;
        }
        if (!Objects.equals(this.currentRow.getValue(), this.unsavedNodeData())) {
            return this.client().sismember(this.dbIndex(), this.key(), (String) this.unsavedNodeData());
        }
        return false;
    }
}
