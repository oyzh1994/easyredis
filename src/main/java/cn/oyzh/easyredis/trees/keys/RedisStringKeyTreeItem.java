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
        // this.setValue(new RedisKeyTreeItemValue(this));
    }

    @Override
    public boolean saveKeyValue() {
        Object value = this.data();
        try {
            if (value != null) {
                this.setKeyValue(value);
                this.refreshKeyValue();
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
            Object val;
            if (this.isRawEncoding(true)) {
                val = this.client().get(this.dbIndex(), this.keyBinary());
            } else {
                val = this.client().get(this.dbIndex(), this.key());
            }
            this.value.valueOfString(val);
            this.flushCount();
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
     * 是否raw格式
     *
     * @return 结果
     */
    public boolean isRawEncoding() {
        return this.isRawEncoding(false);
    }

    /**
     * 是否raw格式
     *
     * @param flushEncoding 刷新编码
     * @return 结果
     */
    public boolean isRawEncoding(boolean flushEncoding) {
        if (this.value.objectedEncoding() == null || flushEncoding) {
            this.value.objectedEncoding(this.client().objectEncoding(this.dbIndex(), this.key()));
        }
        return this.value.isRawEncoding();
    }

    // /**
    //  * 获取数据大小
    //  *
    //  * @return 数据大小
    //  */
    // public Integer size() {
    //     return this.value.size();
    // }

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

}
