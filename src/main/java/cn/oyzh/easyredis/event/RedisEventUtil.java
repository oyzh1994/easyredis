package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.EventUtil;
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

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void clientClosed(RedisClient client) {
        RedisClientClosedEvent event = new RedisClientClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(RedisClient client) {
        RedisConnectionClosedEvent msg = new RedisConnectionClosedEvent();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(RedisClient client) {
        RedisConnectionConnectedMsg msg = new RedisConnectionConnectedMsg();
        msg.data(client);
        EventUtil.post(msg);
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
    public static void terminalOpen(RedisInfo info) {
        RedisTerminalOpenEvent msg = new RedisTerminalOpenEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 终端关闭事件
     *
     * @param info redis信息
     */
    public static void terminalClose(RedisInfo info) {
        RedisTerminalCloseEvent msg = new RedisTerminalCloseEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * list行添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void listRowAdded(RedisListKeyTreeItem item, String key, String member) {
        RedisListRowAddedEvent msg = new RedisListRowAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.member(member);
        EventUtil.post(msg);
    }

    /**
     * set成员添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void setMemberAdded(RedisSetKeyTreeItem item, String key, String member) {
        RedisSetMemberAddedEvent msg = new RedisSetMemberAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.member(member);
        EventUtil.post(msg);
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
        RedisZSetMemberAddedEvent msg = new RedisZSetMemberAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.score(score);
        msg.member(member);
        EventUtil.post(msg);
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
        RedisZSetCoordinateAddedEvent msg = new RedisZSetCoordinateAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.member(member);
        msg.latitude(latitude);
        msg.longitude(longitude);
        EventUtil.post(msg);
    }

    /**
     * stream消息添加事件
     *
     * @param item    redis树节点
     * @param key     键名称
     * @param message 内容
     */
    public static void streamMessageAdded(RedisStreamKeyTreeItem item, String key, String message) {
        RedisStreamMessageAddedEvent msg = new RedisStreamMessageAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.message(message);
        EventUtil.post(msg);
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
        RedisHashFieldAddedEvent msg = new RedisHashFieldAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.field(field);
        msg.value(value);
        EventUtil.post(msg);
    }

    /**
     * hylog元素添加事件
     *
     * @param item     redis树节点
     * @param key      键名称
     * @param elements 统计元素
     */
    public static void hyLogElementsAdded(RedisStringKeyTreeItem item, String key, String[] elements) {
        RedisHyLogElementsAddedEvent msg = new RedisHyLogElementsAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.elements(elements);
        EventUtil.post(msg);
    }

    /**
     * 搜索开始事件
     */
    public static void searchStart(RedisSearchParam searchParam) {
        RedisSearchStartEvent msg = new RedisSearchStartEvent();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(RedisSearchParam searchParam) {
        RedisSearchFinishEvent msg = new RedisSearchFinishEvent();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        EventUtil.post(new TreeChildFilterEvent());
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        EventUtil.post(new TreeChildChangedEvent());
    }

    /**
     * 键添加事件
     *
     * @param item redis树节点
     * @param type 键类型
     * @param key  键名称
     */
    public static void keyAdded(RedisDBTreeItem item, String type, String key) {
        RedisKeyAddedEvent msg = new RedisKeyAddedEvent();
        msg.data(item);
        msg.key(key);
        msg.type(type);
        EventUtil.post(msg);
    }

    /**
     * 键删除事件
     *
     * @param item redis树节点
     * @param key  键名称
     */
    public static void keyDeleted(RedisDBTreeItem item, String key) {
        RedisKeyDeletedEvent msg = new RedisKeyDeletedEvent();
        msg.data(item);
        msg.key(key);
        EventUtil.post(msg);
    }

    /**
     * 键刷新事件
     *
     * @param item redis树节点
     */
    public static void keyFlushed(RedisDBTreeItem item) {
        RedisKeyFlushedEvent msg = new RedisKeyFlushedEvent();
        msg.data(item);
        EventUtil.post(msg);
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        RedisFilterMainEvent msg = new RedisFilterMainEvent();
        EventUtil.post(msg);
    }

    /**
     * 连接已新增事件
     *
     * @param info redis信息
     */
    public static void infoAdded(RedisInfo info) {
        RedisInfoAddedEvent msg = new RedisInfoAddedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 连接已修改事件
     *
     * @param info Redis信息
     */
    public static void infoUpdated(RedisInfo info) {
        RedisInfoUpdatedEvent msg = new RedisInfoUpdatedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 连接已删除事件
     *
     * @param info Redis信息
     */
    public static void infoDeleted(RedisInfo info) {
        RedisInfoDeletedEvent msg = new RedisInfoDeletedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 键ttl更新事件
     *
     * @param item redis树节点
     * @param ttl  ttl值
     */
    public static void keyTTLUpdated(RedisKeyTreeItem<?, ?> item, Long ttl) {
        RedisKeyTTLUpdatedEvent msg = new RedisKeyTTLUpdatedEvent();
        msg.data(item);
        msg.ttl(ttl);
        EventUtil.post(msg);
    }

    /**
     * 键更名事件
     *
     * @param item   redis树节点
     * @param oldKey 旧名称
     */
    public static void keyRenamed(RedisKeyTreeItem<?, ?> item, String oldKey) {
        RedisKeyRenamedEvent msg = new RedisKeyRenamedEvent();
        msg.data(item);
        msg.oldKey(oldKey);
        EventUtil.post(msg);
    }

    /**
     * 键复制事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyCopied(TreeItem<?> item, int targetDB) {
        RedisKeyCopiedEvent msg = new RedisKeyCopiedEvent();
        msg.data(item);
        msg.targetDB(targetDB);
        EventUtil.post(msg);
    }

    /**
     * 键移动事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyMoved(TreeItem<?> item, int targetDB) {
        RedisKeyMovedMsg msg = new RedisKeyMovedMsg();
        msg.data(item);
        msg.targetDB(targetDB);
        EventUtil.post(msg);
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

    public static void filterAdded() {
        RedisFilterAddedEvent event = new RedisFilterAddedEvent();
        EventUtil.post(event);
    }

    public static void keyFilter() {
        RedisKeyFilterEvent event = new RedisKeyFilterEvent();
        EventUtil.post(event);
    }

    /**
     * 过滤历史选中事件
     * @param kw
     */
    public static void filterHistorySelected(String kw) {
        RedisFilterHistorySelectedEvent event = new RedisFilterHistorySelectedEvent();
        event.data(kw);
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
}
