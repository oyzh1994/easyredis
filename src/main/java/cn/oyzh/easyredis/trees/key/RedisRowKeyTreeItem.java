package cn.oyzh.easyredis.trees.key;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * redis 行类型键节点
 *
 * @author oyzh
 * @since 2023/06/30
 */
//@Slf4j
public abstract class RedisRowKeyTreeItem<R extends RedisKeyRow> extends RedisKeyTreeItem {

    /**
     * 当前行
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    protected R currentRow;

    public RedisRowKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeyTreeView treeView) {
        super(value, treeView);
    }

    /**
     * 删除行
     *
     * @return 结果
     */
    public boolean deleteRow() {
        return false;
    }

    /**
     * 重载行
     *
     * @return 结果
     */
    public boolean reloadRow() {
        return false;
    }

    /**
     * 检查行是否存在
     *
     * @return 结果
     */
    public boolean checkRowExists() {
        return false;
    }

    /**
     * 获取行列表
     *
     * @return 行列表
     */
    public List<R> rows() {
        try {
            this.refreshKeyValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        if (this.value.isSetKey()) {
            return (List<R>) this.value.asSetValue().getValue();
        }
        if (this.value.isZSetKey()) {
            return (List<R>) this.value.asZSetValue().getValue();
        }
        if (this.value.isListKey()) {
            return (List<R>) this.value.asListValue().getValue();
        }
        if (this.value.isHashKey()) {
            return (List<R>) this.value.asHashValue().getValue();
        }
        if (this.value.isStreamKey()) {
            return (List<R>) this.value.asStreamValue().getValue();
        }
        return Collections.emptyList();
    }

    public boolean isSelectRow() {
        return this.currentRow != null;
    }

    @Override
    public Object rawData() {
        return this.currentRow == null ? null : this.currentRow.getValue();
    }

    /**
     * 数据是否太大
     *
     * @return 结果
     */
    public boolean isDataTooBig() {
        return false;
    }
}
