package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.easyredis.redis.RedisRow;
import cn.oyzh.easyredis.redis.RedisRowKey;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * redis 行类型键节点
 *
 * @author oyzh
 * @since 2023/06/30
 */
//@Slf4j
public abstract class RedisRowKeyTreeItem<K extends RedisRowKey, R extends RedisRow> extends RedisKeyTreeItem<K> {

    /**
     * 当前行
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    protected R currentRow;

    public RedisRowKeyTreeItem(@NonNull K value, @NonNull RedisKeysTreeView treeView) {
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
     * 重新载入行
     *
     * @return 结果
     */
    public boolean reloadRow() {
        return false;
    }

    /**
     * 检查是否存在
     *
     * @return 结果
     */
    public boolean checkExists() {
        return false;
    }

    /**
     * 键值
     *
     * @return 行列表
     */
    public List<R> nodeValue() {
        try {
            this.refreshNodeValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return this.value.value();
    }
}
