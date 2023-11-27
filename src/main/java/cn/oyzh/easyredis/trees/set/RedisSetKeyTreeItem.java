package cn.oyzh.easyredis.trees.set;

import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
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
//@Slf4j
public class RedisSetKeyTreeItem extends RedisRowKeyTreeItem<RedisSetKey, RedisSetKeyTreeItemValue, RedisSetRow> {

    public RedisSetKeyTreeItem(@NonNull RedisSetKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisSetKeyTreeItemValue(this));
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
    public boolean checkExists() {
        if (this.dataUnsaved()) {
            if (!Objects.equals(this.currentRow.getValue(), this.data())) {
                return this.client().sismember(this.dbIndex(), this.key(), (String) this.data());
            }
        }
        return false;
    }
}
