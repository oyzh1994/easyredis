package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.dto.RedisSearchParam;
import cn.oyzh.easyredis.event.msg.RedisHashFieldAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisHyLogElementsAddedMsg;
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
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.hylog.RedisHyLogKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventBuilder;
import cn.oyzh.fx.plus.event.EventUtil;
import lombok.experimental.UtilityClass;

/**
 * zk事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class RedisEventUtil {

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
     * @param item redis树节点
     */
    public static void listRowAdded(RedisListKeyTreeItem item) {
        RedisListRowAddedMsg msg = new RedisListRowAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * set成员添加事件
     *
     * @param item redis树节点
     */
    public static void setMemberAdded(RedisSetKeyTreeItem item) {
        RedisSetMemberAddedMsg msg = new RedisSetMemberAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * zset成员添加事件
     *
     * @param item redis树节点
     */
    public static void zSetMemberAdded(RedisZSetKeyTreeItem item) {
        RedisZSetMemberAddedMsg msg = new RedisZSetMemberAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * zset地理坐标添加事件
     *
     * @param item redis树节点
     */
    public static void zSetCoordinateAdded(RedisZSetKeyTreeItem item) {
        RedisZSetCoordinateAddedMsg msg = new RedisZSetCoordinateAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * stream消息添加事件
     *
     * @param item redis树节点
     */
    public static void streamMessageAdded(RedisStreamKeyTreeItem item) {
        RedisStreamMessageAddedMsg msg = new RedisStreamMessageAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * hash字段添加事件
     *
     * @param item redis树节点
     */
    public static void hashFieldAddedMsg(RedisHashKeyTreeItem item) {
        RedisHashFieldAddedMsg msg = new RedisHashFieldAddedMsg();
        msg.item(item);
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }

    /**
     * hylog元素添加事件
     *
     * @param item redis树节点
     */
    public static void hyLogElementsAddedMsg(RedisHyLogKeyTreeItem item) {
        RedisHyLogElementsAddedMsg msg = new RedisHyLogElementsAddedMsg();
        msg.item(item);
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
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        TreeChildChangedMsg msg = new TreeChildChangedMsg();
        EventUtil.fire(EventBuilder.newBuilder(msg).build());
    }
}
