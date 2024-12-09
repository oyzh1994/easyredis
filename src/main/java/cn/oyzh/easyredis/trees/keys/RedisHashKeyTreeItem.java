package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.redis.key.RedisHashValue;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisHashKeyTreeItem extends RedisRowKeyTreeItem<RedisHashValue.RedisHashRow> {

    public RedisHashKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
    }

    // /**
    //  * 字段属性
    //  */
    // private SimpleStringProperty fieldProperty;
    //
    // /**
    //  * 获取字段属性
    //  *
    //  * @return 字段属性
    //  */
    // public SimpleStringProperty fieldProperty() {
    //     if (this.fieldProperty == null) {
    //         this.fieldProperty = new SimpleStringProperty();
    //     }
    //     return this.fieldProperty;
    // }

    @Override
    public RedisHashValue.RedisHashRow data() {
        return (RedisHashValue.RedisHashRow) super.data();
    }

    @Override
    public void data(Object data) {
        if (data instanceof RedisHashValue.RedisHashRow row) {
            super.data(row.clone());
        } else {
            super.clearData();
        }
    }

    /**
     * 获取字段
     *
     * @return 字段
     */
    public String field() {
        // return this.fieldProperty == null ? null : this.fieldProperty().get();
        if (this.data() == null) {
            return null;
        }
        return this.data().getField();
    }

    /**
     * 设置字段
     *
     * @param field 字段
     */
    public void field(String field) {
        // this.fieldProperty().set(field);
        if (this.data() != null) {
            this.data().setField(field);
        }
    }

    // @Override
    // public boolean isDataUnsaved() {
    //     return this.field() != null || super.isDataUnsaved();
    // }

    // @Override
    // public void clearData() {
    //     this.field(null);
    //     super.clearData();
    // }

    @Override
    public boolean checkRowExists() {
        String field1 = this.field();
        String field2 = this.currentRow.getField();
        if (field1 != null && !Objects.equals(field1, field2)) {
            return this.client().hexists(this.dbIndex(), this.key(), field1);
        }
        return false;
    }

    @Override
    public void saveKeyValue() {
        RedisHashValue.RedisHashRow row = this.data();
        try {
            // 保存数据
            this.setKeyValue(row);
            // 更新行
            this.currentRow.setField(row.getField());
            this.currentRow.setValue(row.getValue());
            // 清除数据
            this.clearData();
            // 刷新节点
            this.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void setKeyValue(Object value) {
        if (value instanceof RedisHashValue.RedisHashRow row) {
            String field = row.getField();
            String oldField = this.currentRow.getField();
            this.client().hset(this.dbIndex(), this.key(), field, row.getValue());
            // 删除旧字段
            if (!StringUtil.equals(field, oldField)) {
                this.client().hdel(this.dbIndex(), this.key(), oldField);
            }
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().hdel(this.dbIndex(), this.key(), this.currentRow.getField());
            if (count > 0) {
                this.rows().remove(this.currentRow);
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
    public void refreshKeyValue() {
        Map<String, String> value = this.client().hgetAll(this.dbIndex(), this.key());
        this.value.valueOfHash(value);
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
