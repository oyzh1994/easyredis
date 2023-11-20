package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.redis.RedisStringKey;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisStringKeyTreeItem extends RedisKeyTreeItem<RedisStringKey> {

    public RedisStringKeyTreeItem(@NonNull RedisStringKey value, @NonNull RedisConnectTreeItem root) {
        super(value, root);
    }

    @Override
    public boolean saveNodeValue() {
        Object value = this.unsavedNodeData();
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
        try {
            if (value instanceof String string) {
                this.client().set(this.dbIndex(), this.key(), string);
            } else if (value instanceof byte[] bytes) {
                this.client().set(this.dbIndex(), this.keyBinary(), bytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
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
            // 清空未保存的数据
            this.unsavedNodeData(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public Object rawValue() {
        if (super.unsavedNodeData() != null) {
            return super.unsavedNodeData();
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
        Object o = this.unsavedNodeData();
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

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    public Integer size() {
        return this.value.size();
    }
}
