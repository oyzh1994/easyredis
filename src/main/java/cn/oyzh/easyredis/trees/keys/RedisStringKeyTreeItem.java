package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisStringValue;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisStringKeyTreeItem extends RedisKeyTreeItem {

    public RedisStringKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
    }

    @Override
    public boolean saveKeyValue() {
        Object value = this.data();
        try {
            if (value != null) {
                this.setKeyValue(value);
                // this.refreshKeyValue();
                // 更新值
                this.keyValue().setValue(value);
                // 刷新统计值
                this.flushCount();
                // 清除缓存
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
        if (value instanceof String string) {
            this.client().set(this.dbIndex(), this.key(), string);
        } else if (value instanceof byte[] bytes) {
            this.client().set(this.dbIndex(), this.keyBinary(), bytes);
        }
    }

    @Override
    public void refreshKeyValue() {
        try {
            RedisStringValue stringValue = this.keyValue();
            // 刷新值
            if (stringValue == null || !stringValue.hasValue()) {
                // 原始格式
                if (this.isRawEncoding(true)) {
                    byte[] val = this.client().get(this.dbIndex(), this.keyBinary());
                    this.value.valueOfString(val);
                } else {// 字符串格式
                    String val = this.client().get(this.dbIndex(), this.key());
                    this.value.valueOfString(val);
                }
                stringValue = this.keyValue();
            }
            // 刷新统计值
            if (stringValue.getCount() == null) {
                this.flushCount();
            }
            // 清空未保存的数据
            this.clearData();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public Object rawValue() {
        if (super.isDataUnsaved()) {
            return super.data();
        }
        this.refreshKeyValue();
        return this.value.value().getValue();
    }

    /**
     * 数据是否太长
     *
     * @return 结果
     */
    public boolean isDataTooLong() {
        Object o = this.data();
        if (o instanceof String s) {
            return s.length() > 1024 * 1024;
        }
        if (o instanceof byte[] bytes) {
            return bytes.length > 1024 * 1024;
        }
        return false;
    }

    /**
     * 获取统计值大小
     *
     * @return 统计值大小
     */
    public boolean isHyLog() {
        RedisStringValue stringValue = this.value.asStringValue();
        if (stringValue.getHyLog() == null) {
            this.flushCount();
        }
        return stringValue.getHyLog();
    }

    /**
     * 获取统计值
     *
     * @return 统计值
     */
    public Long count() {
        RedisStringValue stringValue = this.value.asStringValue();
        if (stringValue.getCount() == null) {
            this.flushCount();
        }
        return stringValue.getCount();
    }

    /**
     * 刷新统计值
     */
    public void flushCount() {
        RedisStringValue stringValue = this.value.asStringValue();
        try {
            stringValue.setCount(this.client().pfcount(this.dbIndex(), this.key()));
            stringValue.setHyLog(true);
        } catch (Exception ex) {
            if (StringUtil.containsAny(ex.getMessage(), "WRONGTYPE Key is not a valid HyperLogLog string value")) {
                stringValue.setHyLog(false);
            } else {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public RedisStringValue keyValue() {
        return (RedisStringValue) super.keyValue();
    }
}
