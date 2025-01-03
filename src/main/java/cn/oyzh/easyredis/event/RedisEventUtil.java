package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.dto.RedisPubsubItem;
import cn.oyzh.easyredis.event.client.RedisClientActionEvent;
import cn.oyzh.easyredis.event.connect.RedisAddConnectEvent;
import cn.oyzh.easyredis.event.connect.RedisConnectAddedEvent;
import cn.oyzh.easyredis.event.connect.RedisConnectOpenedEvent;
import cn.oyzh.easyredis.event.connect.RedisConnectUpdatedEvent;
import cn.oyzh.easyredis.event.connection.RedisConnectDeletedEvent;
import cn.oyzh.easyredis.event.connection.RedisConnectionClosedEvent;
import cn.oyzh.easyredis.event.connection.RedisConnectionConnectedEvent;
import cn.oyzh.easyredis.event.connection.RedisServerMonitorEvent;
import cn.oyzh.easyredis.event.group.RedisAddGroupEvent;
import cn.oyzh.easyredis.event.group.RedisGroupAddedEvent;
import cn.oyzh.easyredis.event.group.RedisGroupDeletedEvent;
import cn.oyzh.easyredis.event.group.RedisGroupRenamedEvent;
import cn.oyzh.easyredis.event.key.RedisHashFieldAddedEvent;
import cn.oyzh.easyredis.event.key.RedisHyLogElementsAddedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyAddedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyCopiedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyDeletedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyFilteredEvent;
import cn.oyzh.easyredis.event.key.RedisKeyFlushedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyMovedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyRenamedEvent;
import cn.oyzh.easyredis.event.key.RedisKeyTTLUpdatedEvent;
import cn.oyzh.easyredis.event.key.RedisKeysMovedEvent;
import cn.oyzh.easyredis.event.key.RedisListRowAddedEvent;
import cn.oyzh.easyredis.event.key.RedisPubsubOpenEvent;
import cn.oyzh.easyredis.event.key.RedisSetMemberAddedEvent;
import cn.oyzh.easyredis.event.key.RedisStreamMessageAddedEvent;
import cn.oyzh.easyredis.event.key.RedisZSetCoordinateAddedEvent;
import cn.oyzh.easyredis.event.key.RedisZSetMemberAddedEvent;
import cn.oyzh.easyredis.event.key.RedisZSetReverseViewEvent;
import cn.oyzh.easyredis.event.terminal.RedisTerminalCloseEvent;
import cn.oyzh.easyredis.event.terminal.RedisTerminalOpenEvent;
import cn.oyzh.easyredis.event.tree.RedisTreeItemChangedEvent;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.CommandArguments;

/**
 * redis事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class RedisEventUtil {

    // /**
    //  * 连接关闭事件
    //  *
    //  * @param client redis客户端
    //  */
    // public static void clientClosed(RedisClient client) {
    //     RedisClientClosedEvent event = new RedisClientClosedEvent();
    //     event.data(client);
    //     EventUtil.post(event);
    // }

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(RedisClient client) {
        RedisConnectionClosedEvent event = new RedisConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(RedisClient client) {
        RedisConnectionConnectedEvent event = new RedisConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param redisConnect redis信息
     */
    public static void terminalClose(RedisConnect redisConnect, Integer dbIndex) {
        RedisTerminalCloseEvent event = new RedisTerminalCloseEvent();
        event.data(redisConnect);
        event.dbIndex(dbIndex);
        EventUtil.post(event);
    }

    /**
     * list行添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void listRowAdded(RedisListKeyTreeItem item, String key, String member) {
        RedisListRowAddedEvent event = new RedisListRowAddedEvent();
        event.data(item);
        event.key(key);
        event.member(member);
        EventUtil.post(event);
    }

    /**
     * set成员添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void setMemberAdded(RedisSetKeyTreeItem item, String key, String member) {
        RedisSetMemberAddedEvent event = new RedisSetMemberAddedEvent();
        event.data(item);
        event.key(key);
        event.member(member);
        EventUtil.post(event);
    }

    /**
     * zset成员添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     * @param score  成员
     */
    public static void zSetMemberAdded(RedisZSetKeyTreeItem item, String key, String member, Double score) {
        RedisZSetMemberAddedEvent event = new RedisZSetMemberAddedEvent();
        event.data(item);
        event.key(key);
        event.score(score);
        event.member(member);
        EventUtil.post(event);
    }

    /**
     * zset地理坐标添加事件
     *
     * @param item      redis树节点
     * @param key       键名称
     * @param member    成员
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static void zSetCoordinateAdded(RedisZSetKeyTreeItem item, String key, String member, double longitude, double latitude) {
        RedisZSetCoordinateAddedEvent event = new RedisZSetCoordinateAddedEvent();
        event.data(item);
        event.key(key);
        event.member(member);
        event.latitude(latitude);
        event.longitude(longitude);
        EventUtil.post(event);
    }

    /**
     * stream消息添加事件
     *
     * @param item    redis树节点
     * @param key     键名称
     * @param message 内容
     */
    public static void streamMessageAdded(RedisStreamKeyTreeItem item, String key, String message) {
        RedisStreamMessageAddedEvent event = new RedisStreamMessageAddedEvent();
        event.data(item);
        event.key(key);
        event.message(message);
        EventUtil.post(event);
    }

    /**
     * hash字段添加事件
     *
     * @param item  redis树节点
     * @param key   键名称
     * @param field 字段名称
     * @param value 字段值
     */
    public static void hashFieldAdded(RedisHashKeyTreeItem item, String key, String field, String value) {
        RedisHashFieldAddedEvent event = new RedisHashFieldAddedEvent();
        event.data(item);
        event.key(key);
        event.field(field);
        event.value(value);
        EventUtil.post(event);
    }

    /**
     * hylog元素添加事件
     *
     * @param item     redis树节点
     * @param key      键名称
     * @param elements 统计元素
     */
    public static void hyLogElementsAdded(RedisStringKeyTreeItem item, String key, String[] elements) {
        RedisHyLogElementsAddedEvent event = new RedisHyLogElementsAddedEvent();
        event.data(item);
        event.key(key);
        event.elements(elements);
        EventUtil.post(event);
    }

    // /**
    //  * 树节点过滤事件
    //  */
    // public static void treeChildFilter() {
    //     EventUtil.post(new TreeChildFilterEvent());
    // }

//    /**
//     * 树节点变化事件
//     */
//    public static void treeChildChanged() {
//        EventUtil.post(new TreeChildChangedEvent());
//    }

    /**
     * 键添加事件
     *
     * @param connect redis连接
     * @param type    键类型
     * @param key     键名称
     */
    public static void keyAdded(RedisConnect connect, String type, String key, int dbIndex) {
        RedisKeyAddedEvent event = new RedisKeyAddedEvent();
        event.data(connect);
        event.key(key);
        event.type(type);
        event.dbIndex(dbIndex);
        EventUtil.post(event);
    }

    /**
     * 键删除事件
     *
     * @param connect redis连接
     * @param key     键名称
     * @param dbIndex 库
     */
    public static void keyDeleted(RedisConnect connect, String key, int dbIndex) {
        RedisKeyDeletedEvent event = new RedisKeyDeletedEvent();
        event.data(connect);
        event.key(key);
        event.dbIndex(dbIndex);
        EventUtil.post(event);
    }

    /**
     * 键刷新事件
     *
     * @param item redis树节点
     */
    public static void keyFlushed(RedisDatabaseTreeItem item) {
        RedisKeyFlushedEvent event = new RedisKeyFlushedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 键过滤事件
     *
     * @param item redis树节点
     */
    public static void keyFiltered(RedisDatabaseTreeItem item) {
        RedisKeyFilteredEvent event = new RedisKeyFilteredEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 连接已新增事件
     *
     * @param redisConnect redis信息
     */
    public static void connectAdded(RedisConnect redisConnect) {
        RedisConnectAddedEvent event = new RedisConnectAddedEvent();
        event.data(redisConnect);
        EventUtil.post(event);
    }

    /**
     * 连接已修改事件
     *
     * @param redisConnect Redis信息
     */
    public static void connectUpdated(RedisConnect redisConnect) {
        RedisConnectUpdatedEvent event = new RedisConnectUpdatedEvent();
        event.data(redisConnect);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     */
    public static void terminalOpen() {
        terminalOpen(null, null);
    }

    /**
     * 终端打开事件
     *
     * @param redisConnect redis信息
     */
    public static void terminalOpen(RedisConnect redisConnect, Integer dbIndex) {
        RedisTerminalOpenEvent event = new RedisTerminalOpenEvent();
        event.data(redisConnect);
        event.dbIndex(dbIndex);
        EventUtil.post(event);
    }

    // /**
    //  * 过滤主页事件
    //  */
    // public static void filterMain() {
    //     RedisFilterMainEvent event = new RedisFilterMainEvent();
    //     EventUtil.post(event);
    // }

    // /**
    //  * 搜索开始事件
    //  */
    // public static void searchStart(RedisSearchParam searchParam) {
    //     RedisSearchStartEvent event = new RedisSearchStartEvent();
    //     event.data(searchParam);
    //     EventUtil.post(event);
    // }
    //
    // /**
    //  * 搜索结束事件
    //  */
    // public static void searchFinish(RedisSearchParam searchParam) {
    //     RedisSearchFinishEvent event = new RedisSearchFinishEvent();
    //     event.data(searchParam);
    //     EventUtil.post(event);
    // }

    // /**
    //  * 搜索触发事件
    //  */
    // public static void searchFire() {
    //     EventUtil.post(new RedisSearchFireEvent());
    // }

    /**
     * 连接已删除事件
     *
     * @param redisConnect Redis信息
     */
    public static void connectDeleted(RedisConnect redisConnect) {
        RedisConnectDeletedEvent event = new RedisConnectDeletedEvent();
        event.data(redisConnect);
        EventUtil.post(event);
    }

    /**
     * 键ttl更新事件
     *
     * @param connect redis树节点
     * @param ttl     ttl值
     */
    public static void keyTTLUpdated(RedisConnect connect, Long ttl, String key, int dbIndex) {
        RedisKeyTTLUpdatedEvent event = new RedisKeyTTLUpdatedEvent();
        event.data(connect);
        event.ttl(ttl);
        event.key(key);
        event.dbIndex(dbIndex);
        EventUtil.post(event);
    }

    /**
     * 键更名事件
     *
     * @param item   redis树节点
     * @param oldKey 旧名称
     */
    public static void keyRenamed(RedisKeyTreeItem item, String oldKey) {
        RedisKeyRenamedEvent event = new RedisKeyRenamedEvent();
        event.data(item);
        event.oldKey(oldKey);
        EventUtil.post(event);
    }

    /**
     * 键复制事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyCopied(TreeItem<?> item, int targetDB) {
        RedisKeyCopiedEvent event = new RedisKeyCopiedEvent();
        event.data(item);
        event.targetDB(targetDB);
        EventUtil.post(event);
    }

    /**
     * 键移动事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyMoved(RedisKeyTreeItem item, int targetDB) {
        RedisKeyMovedEvent event = new RedisKeyMovedEvent();
        event.data(item);
        event.targetDB(targetDB);
        EventUtil.post(event);
    }

    /**
     * 多个键移动事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keysMoved(RedisDatabaseTreeItem item, int targetDB) {
        RedisKeysMovedEvent event = new RedisKeysMovedEvent();
        event.data(item);
        event.targetDB(targetDB);
        EventUtil.post(event);
    }

    /**
     * 服务监控事件
     *
     * @param client redis客户端
     */
    public static void serverMonitor(RedisClient client) {
        RedisServerMonitorEvent event = new RedisServerMonitorEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 订阅打开事件
     *
     * @param item redis节点
     */
    public static void pubsubOpen(RedisPubsubItem item) {
        RedisPubsubOpenEvent event = new RedisPubsubOpenEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 添加分组
     */
    public static void addGroup() {
        EventUtil.post(new RedisAddGroupEvent());
    }

    /**
     * 添加连接
     */
    public static void addConnect() {
        EventUtil.post(new RedisAddConnectEvent());
    }

    // /**
    //  * 展开左侧
    //  */
    // public static void leftExtend() {
    //     EventUtil.post(new RedisLeftExtendEvent());
    // }
    //
    // /**
    //  * 收缩左侧
    //  */
    // public static void leftCollapse() {
    //     EventUtil.post(new RedisLeftCollapseEvent());
    // }

    // /**
    //  * 过滤添加事件
    //  */
    // public static void filterAdded() {
    //     RedisFilterAddedEvent event = new RedisFilterAddedEvent();
    //     EventUtil.post(event);
    // }

    /**
     * 更新日志事件
     */
    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    // /**
    //  * 树节点选中事件
    //  */
    // public static void treeChildSelected(RedisKeyTreeItem item) {
    //     TreeChildSelectedEvent event = new TreeChildSelectedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }

    /**
     * zset反转视图事件
     *
     * @param item 节点
     */
    public static void zSetReverseView(RedisZSetKeyTreeItem item) {
        RedisZSetReverseViewEvent event = new RedisZSetReverseViewEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 连接丢失事件
     *
     * @param client redis客户端
     */
    public static void connectionOpened(RedisDatabaseTreeItem client) {
        RedisConnectOpenedEvent event = new RedisConnectOpenedEvent();
        event.data(client);
        EventUtil.postSync(event);
    }

    public static void treeItemChanged(TreeItem<?> treeItem) {
        RedisTreeItemChangedEvent event = new RedisTreeItemChangedEvent();
        event.data(treeItem);
        EventUtil.postSync(event);
    }

    /**
     * 布局1
     */
    public static void layout1() {
        EventUtil.post(new Layout1Event());
    }

    /**
     * 布局2
     */
    public static void layout2() {
        EventUtil.post(new Layout2Event());
    }

    /**
     * 分组已添加
     */
    public static void groupAdded(String group) {
        RedisGroupAddedEvent event = new RedisGroupAddedEvent();
        event.data(group);
        EventUtil.post(event);
    }

    /**
     * 分组已删除
     */
    public static void groupDeleted(String group) {
        RedisGroupDeletedEvent event = new RedisGroupDeletedEvent();
        event.data(group);
        EventUtil.post(event);
    }

    /**
     * 分组已更名
     */
    public static void groupRenamed(String group, String oldName) {
        RedisGroupRenamedEvent event = new RedisGroupRenamedEvent();
        event.data(group);
        event.oldName(oldName);
        EventUtil.post(event);
    }

    /**
     * 客户端操作
     */
    public static void clientAction(String connectName, CommandArguments arguments) {
        RedisClientActionEvent event = new RedisClientActionEvent();
        event.data(connectName);
        event.arguments(arguments);
        EventUtil.postAsync(event);
    }
}
