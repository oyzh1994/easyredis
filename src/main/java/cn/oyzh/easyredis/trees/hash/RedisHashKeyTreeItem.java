package cn.oyzh.easyredis.trees.hash;

import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisDBTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisHashKeyTreeItem extends RedisRowKeyTreeItem<RedisHashKey, RedisHashKeyTreeItemValue, RedisHashRow> {

    public RedisHashKeyTreeItem(@NonNull RedisHashKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisHashKeyTreeItemValue(this));
    }

    /**
     * 字段属性
     */
    private SimpleStringProperty fieldProperty;

    /**
     * 获取字段属性
     *
     * @return 字段属性
     */
    public SimpleStringProperty fieldProperty() {
        if (this.fieldProperty == null) {
            this.fieldProperty = new SimpleStringProperty();
        }
        return this.fieldProperty;
    }

    /**
     * 获取字段
     *
     * @return 字段
     */
    public String field() {
        return this.fieldProperty == null ? null : this.fieldProperty().get();
    }

    /**
     * 设置字段
     *
     * @param field 字段
     */
    public void field(String field) {
        this.fieldProperty().set(field);
    }

    @Override
    public boolean dataUnsaved() {
        return this.field() != null || super.dataUnsaved();
    }

    @Override
    public void clearData() {
        this.field(null);
        super.clearData();
    }

    @Override
    public boolean checkExists() {
        String field1 = this.field();
        String field2 = this.currentRow.getField();
        if (field1 != null && !Objects.equals(field1, field2)) {
            return this.client().hexists(this.dbIndex(), this.key(), field1);
        }
        return false;
    }

    @Override
    public boolean saveNodeValue() {
        String value = (String) this.data();
        if (value == null) {
            value = this.currentRow.getValue();
        }
        String field = this.field();
        String oldField = this.field();
        if (field == null) {
            field = this.currentRow.getField();
        } else {
            oldField = this.currentRow.getField();
        }
        try {
            this.currentRow.setField(field);
            this.setNodeValue(value);
            this.currentRow.setValue(value);
            this.clearData();
            // 如果字段变化，则删除旧字段
            if (oldField != null) {
                this.client().hdel(this.dbIndex(), this.key(), oldField);
            }
            return true;
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
    public boolean reloadRow() {
        String value = this.client().hget(this.dbIndex(), this.key(), this.currentRow.getField());
        if (value != null) {
            this.currentRow.setValue(value);
            return true;
        }
        return false;
    }
}
