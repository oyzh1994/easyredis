package cn.oyzh.easyredis.trees.string;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;
import cn.oyzh.easyredis.trees.keys.RedisDatabaseTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisStringKeyTreeItem extends RedisKeyTreeItem<RedisStringKey, RedisKeyTreeItemValue> {

    public RedisStringKeyTreeItem(@NonNull RedisStringKey value, @NonNull RedisDatabaseTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisStringKeyTreeItemValue(this));
    }

    @Override
    public boolean saveNodeValue() {
        Object value = this.data();
        try {
            if (value != null) {
                this.setNodeValue(value);
                this.refreshNodeValue();
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
        if (value instanceof String string) {
            this.client().set(this.dbIndex(), this.key(), string);
        } else if (value instanceof byte[] bytes) {
            this.client().set(this.dbIndex(), this.keyBinary(), bytes);
        }
    }

    @Override
    public void refreshNodeValue() {
        try {
            Object val;
            if (this.isRawEncoding(true)) {
                val = this.client().get(this.dbIndex(), this.keyBinary());
            } else {
                val = this.client().get(this.dbIndex(), this.key());
            }
            this.value.value(val);
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
        if (super.dataUnsaved()) {
            return super.data();
        }
        this.refreshNodeValue();
        return this.value.value();
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
        if (this.value.hyLog() == null) {
            this.flushCount();
        }
        return this.value.isHyLog();
    }

    /**
     * 获取统计值
     *
     * @return 统计值
     */
    public Long count() {
        if (this.value.count() == null) {
            this.flushCount();
        }
        return this.value.count();
    }

    /**
     * 刷新统计值
     */
    public void flushCount() {
        try {
            this.value.count(this.client().pfcount(this.dbIndex(), this.key()));
            this.value.hyLog(true);
        } catch (Exception ex) {
            if (StrUtil.containsAny(ex.getMessage(), "WRONGTYPE Key is not a valid HyperLogLog string value")) {
                this.value.hyLog(false);
            } else {
                ex.printStackTrace();
            }
        }
    }
}
