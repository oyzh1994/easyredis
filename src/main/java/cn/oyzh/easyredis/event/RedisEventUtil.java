package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.msg.RedisConnectionClosedMsg;
import cn.oyzh.easyredis.event.msg.RedisConnectionConnectedMsg;
import cn.oyzh.easyredis.event.msg.RedisFilterMainMsg;
import cn.oyzh.easyredis.event.msg.RedisHashFieldAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisHyLogElementsAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisInfoAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisInfoDeletedMsg;
import cn.oyzh.easyredis.event.msg.RedisInfoUpdatedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyCopiedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyDeletedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyFlushedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyMovedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyRenamedMsg;
import cn.oyzh.easyredis.event.msg.RedisKeyTTLUpdatedMsg;
import cn.oyzh.easyredis.event.msg.RedisListRowAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisSearchFinishMsg;
import cn.oyzh.easyredis.event.msg.RedisSearchStartMsg;
import cn.oyzh.easyredis.event.msg.RedisSetMemberAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisStreamMessageAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisTerminalCloseMsg;
import cn.oyzh.easyredis.event.msg.RedisTerminalOpenMsg;
import cn.oyzh.easyredis.event.msg.RedisZSetCoordinateAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisZSetMemberAddedMsg;
import cn.oyzh.easyredis.event.msg.TreeChildChangedMsg;
import cn.oyzh.easyredis.event.msg.TreeChildFilterMsg;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.hylog.RedisHyLogKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.EventBuilder;
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
    public static void connectionClosed(RedisClient client) {
        RedisConnectionClosedMsg msg = new RedisConnectionClosedMsg();
        msg.client(client);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(RedisClient client) {
        RedisConnectionConnectedMsg msg = new RedisConnectionConnectedMsg();
        msg.client(client);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
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
        RedisTerminalOpenMsg msg = new RedisTerminalOpenMsg();
        msg.info(info);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 终端关闭事件
     *
     * @param info redis信息
     */
    public static void terminalClose(RedisInfo info) {
        RedisTerminalCloseMsg msg = new RedisTerminalCloseMsg();
        msg.info(info);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * list行添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void listRowAdded(RedisListKeyTreeItem item, String key, String member) {
        RedisListRowAddedMsg msg = new RedisListRowAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.member(member);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * set成员添加事件
     *
     * @param item   redis树节点
     * @param key    键名称
     * @param member 成员
     */
    public static void setMemberAdded(RedisSetKeyTreeItem item, String key, String member) {
        RedisSetMemberAddedMsg msg = new RedisSetMemberAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.member(member);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
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
        RedisZSetMemberAddedMsg msg = new RedisZSetMemberAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.score(score);
        msg.member(member);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
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
        RedisZSetCoordinateAddedMsg msg = new RedisZSetCoordinateAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.member(member);
        msg.latitude(latitude);
        msg.longitude(longitude);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * stream消息添加事件
     *
     * @param item    redis树节点
     * @param key     键名称
     * @param message 内容
     */
    public static void streamMessageAdded(RedisStreamKeyTreeItem item, String key, String message) {
        RedisStreamMessageAddedMsg msg = new RedisStreamMessageAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.message(message);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
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
        RedisHashFieldAddedMsg msg = new RedisHashFieldAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.field(field);
        msg.value(value);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * hylog元素添加事件
     *
     * @param item     redis树节点
     * @param key      键名称
     * @param elements 统计元素
     */
    public static void hyLogElementsAdded(RedisHyLogKeyTreeItem item, String key, String[] elements) {
        RedisHyLogElementsAddedMsg msg = new RedisHyLogElementsAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.elements(elements);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 搜索开始事件
     */
    public static void searchStart(RedisSearchParam searchParam) {
        RedisSearchStartMsg msg = new RedisSearchStartMsg();
        msg.searchParam(searchParam);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(RedisSearchParam searchParam) {
        RedisSearchFinishMsg msg = new RedisSearchFinishMsg();
        msg.searchParam(searchParam);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        TreeChildFilterMsg msg = new TreeChildFilterMsg();
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        TreeChildChangedMsg msg = new TreeChildChangedMsg();
        EventUtil.fireDelay(EventBuilder.newBuilder(msg).build(), 100);
    }

    /**
     * 键添加事件
     *
     * @param item redis树节点
     * @param type 键类型
     * @param key  键名称
     */
    public static void keyAdded(RedisDBTreeItem item, String type, String key) {
        RedisKeyAddedMsg msg = new RedisKeyAddedMsg();
        msg.item(item);
        msg.key(key);
        msg.type(type);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键删除事件
     *
     * @param item redis树节点
     * @param key  键名称
     */
    public static void keyDeleted(RedisDBTreeItem item, String key) {
        RedisKeyDeletedMsg msg = new RedisKeyDeletedMsg();
        msg.item(item);
        msg.key(key);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键刷新事件
     *
     * @param item redis树节点
     */
    public static void keyFlushed(RedisDBTreeItem item) {
        RedisKeyFlushedMsg msg = new RedisKeyFlushedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        RedisFilterMainMsg msg = new RedisFilterMainMsg();
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 连接已新增事件
     *
     * @param info redis信息
     */
    public static void infoAdded(RedisInfo info) {
        RedisInfoAddedMsg msg = new RedisInfoAddedMsg();
        msg.info(info);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 连接已修改事件
     *
     * @param info Redis信息
     */
    public static void infoUpdated(RedisInfo info) {
        RedisInfoUpdatedMsg msg = new RedisInfoUpdatedMsg();
        msg.info(info);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 连接已删除事件
     *
     * @param info Redis信息
     */
    public static void infoDeleted(RedisInfo info) {
        RedisInfoDeletedMsg msg = new RedisInfoDeletedMsg();
        msg.info(info);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键ttl更新事件
     *
     * @param item redis树节点
     * @param ttl  ttl值
     */
    public static void keyTTLUpdated(RedisKeyTreeItem<?, ?> item, Long ttl) {
        RedisKeyTTLUpdatedMsg msg = new RedisKeyTTLUpdatedMsg();
        msg.item(item);
        msg.ttl(ttl);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键更名事件
     *
     * @param item   redis树节点
     * @param oldKey 旧名称
     */
    public static void keyRenamed(RedisKeyTreeItem<?, ?> item, String oldKey) {
        RedisKeyRenamedMsg msg = new RedisKeyRenamedMsg();
        msg.item(item);
        msg.oldKey(oldKey);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键复制事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyCopied(TreeItem<?> item, int targetDB) {
        RedisKeyCopiedMsg msg = new RedisKeyCopiedMsg();
        msg.item(item);
        msg.targetDB(targetDB);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * 键移动事件
     *
     * @param item     redis树节点
     * @param targetDB 目标库
     */
    public static void keyMoved(TreeItem<?> item, int targetDB) {
        RedisKeyMovedMsg msg = new RedisKeyMovedMsg();
        msg.item(item);
        msg.targetDB(targetDB);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }
}
