package cn.oyzh.easyredis.redis;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接管理
 *
 * @author oyzh
 * @since 2023/5/12
 */
public interface RedisConnectManager {

    /**
     * 添加连接
     *
     * @param redisInfo 连接信息
     */
    void addConnect(@NonNull RedisConnect redisInfo);

    /**
     * 删除多个连接
     *
     * @param redisInfos 连接列表
     */
    default void addConnects(List<RedisConnect> redisInfos) {
        if (CollectionUtil.isNotEmpty(redisInfos)) {
            for (RedisConnect redisInfo : redisInfos) {
                this.addConnect(redisInfo);
            }
        }
    }

    /**
     * 添加连接键
     *
     * @param item 连接键
     */
    void addConnectItem(@NonNull RedisConnectTreeItem item);

    /**
     * 添加多个连接键
     *
     * @param items 连接键列表
     */
    void addConnectItems(@NonNull List<RedisConnectTreeItem> items);

    /**
     * 删除连接键
     *
     * @param item 连接键
     * @return 结果
     */
    boolean delConnectItem(@NonNull RedisConnectTreeItem item);

    /**
     * 获取连接键
     *
     * @return 连接键
     */
    List<RedisConnectTreeItem> getConnectItems();

    /**
     * 获取已连接的连接节点
     *
     * @return 已连接的连接节点
     */
    default List<RedisConnectTreeItem> getConnectedItems() {
        return this.getConnectItems().parallelStream().filter(RedisConnectTreeItem::isConnected).collect(Collectors.toList());
    }

}
