package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.keys.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.keys.zset.RedisZSetKeyTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;
import lombok.experimental.UtilityClass;

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
     * @param info redis信息
     */
    public static void terminalClose(RedisConnect info) {
        RedisTerminalCloseEvent event = new RedisTerminalCloseEvent();
        event.data(info);
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

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        EventUtil.post(new TreeChildFilterEvent());
    }

//    /**
//     * 树节点变化事件
//     */
//    public static void treeChildChanged() {
//        EventUtil.post(new TreeChildChangedEvent());
//    }

    /**
     * 键添加事件
     *
     * @param item redis树节点
     * @param type 键类型
     * @param key  键名称
     */
    public static void keyAdded(RedisDatabaseTreeItem item, String type, String key) {
        RedisKeyAddedEvent event = new RedisKeyAddedEvent();
        event.data(item);
        event.key(key);
        event.type(type);
        EventUtil.post(event);
    }

    /**
     * 键删除事件
     *
     * @param item redis树节点
     * @param key  键名称
     */
    public static void keyDeleted(RedisDatabaseTreeItem item, String key) {
        RedisKeyDeletedEvent event = new RedisKeyDeletedEvent();
        event.data(item);
        event.key(key);
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
     * 连接已新增事件
     *
     * @param info redis信息
     */
    public static void infoAdded(RedisConnect info) {
        RedisInfoAddedEvent event = new RedisInfoAddedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 连接已修改事件
     *
     * @param info Redis信息
     */
    public static void infoUpdated(RedisConnect info) {
        RedisInfoUpdatedEvent event = new RedisInfoUpdatedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     */
    public static void terminalOpen() {
        terminalOpen(null);
    }

    /**
     * 终端打开事件
     *
     * @param info redis信息
     */
    public static void terminalOpen(RedisConnect info) {
        RedisTerminalOpenEvent event = new RedisTerminalOpenEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        RedisFilterMainEvent event = new RedisFilterMainEvent();
        EventUtil.post(event);
    }

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
     * @param info Redis信息
     */
    public static void infoDeleted(RedisConnect info) {
        RedisInfoDeletedEvent event = new RedisInfoDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 键ttl更新事件
     *
     * @param item redis树节点
     * @param ttl  ttl值
     */
    public static void keyTTLUpdated(RedisKeyTreeItem<?, ?> item, Long ttl) {
        RedisKeyTTLUpdatedEvent event = new RedisKeyTTLUpdatedEvent();
        event.data(item);
        event.ttl(ttl);
        EventUtil.post(event);
    }

    /**
     * 键更名事件
     *
     * @param item   redis树节点
     * @param oldKey 旧名称
     */
    public static void keyRenamed(RedisKeyTreeItem<?, ?> item, String oldKey) {
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
    public static void keyMoved(TreeItem<?> item, int targetDB) {
        RedisKeyMovedEvent event = new RedisKeyMovedEvent();
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

    /**
     * 展开左侧
     */
    public static void leftExtend() {
        EventUtil.post(new RedisLeftExtendEvent());
    }

    /**
     * 收缩左侧
     */
    public static void leftCollapse() {
        EventUtil.post(new RedisLeftCollapseEvent());
    }

    /**
     * 过滤添加事件
     */
    public static void filterAdded() {
        RedisFilterAddedEvent event = new RedisFilterAddedEvent();
        EventUtil.post(event);
    }

    /**
     * 更新日志事件
     */
    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    /**
     * 树节点选中事件
     */
    public static void treeChildSelected(RedisKeyTreeItem<?, ?> item) {
        TreeChildSelectedEvent event = new TreeChildSelectedEvent();
        event.data(item);
        EventUtil.post(event);
    }

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
     * @param client zk客户端
     */
    public static void connectionOpened(RedisConnectTreeItem client) {
        RedisConnectOpenedEvent event = new RedisConnectOpenedEvent();
        event.data(client);
        EventUtil.postSync(event);
    }
}
