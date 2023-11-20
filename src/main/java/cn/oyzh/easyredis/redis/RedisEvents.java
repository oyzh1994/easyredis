package cn.oyzh.easyredis.redis;

import lombok.experimental.UtilityClass;

/**
 * redis事件
 *
 * @author oyzh
 * @since 2023/06/16
 */
@UtilityClass
public class RedisEvents {

    /**
     * 应用退出事件
     */
    public static final String APP_EXIT = "APP_EXIT";

    /**
     * 连接变更事件
     */
    public static final String CONNECTION_CHANGED = "CONNECTION_CHANGED";

    /**
     * redis信息新增
     */
    public static final String REDIS_INFO_ADD = "REDIS_INFO_ADD";

    /**
     * redis信息修改
     */
    public static final String REDIS_INFO_UPDATED = "REDIS_INFO_UPDATED";

    /**
     * redis键过滤
     */
    public static final String REDIS_KEY_FILTER = "REDIS_KEY_FILTER";

    /**
     * redisTTL更新
     */
    public static final String REDIS_TTL_UPDATED = "REDIS_TTL_UPDATED";

    /**
     * redis键添加
     */
    public static final String REDIS_KEY_ADDED = "REDIS_KEY_ADDED";

    /**
     * redis键移动
     */
    public static final String REDIS_KEY_MOVED = "REDIS_KEY_MOVED";

    /**
     * redis键更名
     */
    public static final String REDIS_KEY_RENAMED = "REDIS_KEY_RENAMED";

    /**
     * redis键删除
     */
    public static final String REDIS_KEY_DELETED = "REDIS_KEY_DELETED";

    /**
     * redis键刷新
     */
    public static final String REDIS_KEY_FLUSH = "REDIS_KEY_FLUSH";

    /**
     * redis键复制
     */
    public static final String REDIS_KEY_COPY = "REDIS_KEY_COPY";

    /**
     * redis新增list行
     */
    public static final String REDIS_LIST_ROW_ADDED = "REDIS_LIST_ROW_ADDED";

    /**
     * redis新增set成员
     */
    public static final String REDIS_SET_MEMBER_ADDED = "REDIS_SET_MEMBER_ADDED";

    /**
     * redis变更zset的显示方式
     */
    public static final String REDIS_CHANGE_ZSET_SHOW_TYPE = "REDIS_CHANGE_ZSET_SHOW_TYPE";

    /**
     * redis新增zset成员
     */
    public static final String REDIS_ZSET_MEMBER_ADDED = "REDIS_ZSET_MEMBER_ADDED";

    /**
     * redis新增地理坐标
     */
    public static final String REDIS_GEO_COORDINATE_ADDED = "REDIS_GEO_COORDINATE_ADDED";

    /**
     * redis新增stream消息
     */
    public static final String REDIS_STREAM_MESSAGE_ADDED = "REDIS_STREAM_MESSAGE_ADDED";

    /**
     * redis新增hash字段
     */
    public static final String REDIS_HASH_FIELD_ADDED = "REDIS_HASH_FIELD_ADDED";

    /**
     * redis新增hyperLogLog元素
     */
    public static final String REDIS_HYPER_LOG_LOG_ELEMENT_ADDED = "REDIS_HYPER_LOG_LOG_ELEMENT_ADDED";

    /**
     * redis过滤配置新增
     */
    public static final String REDIS_FILTER_ADDED = "REDIS_FILTER_ADDED";

    /**
     * 展开左侧
     */
    public static final String LEFT_EXTEND = "LEFT_EXTEND";

    /**
     * 收缩左侧
     */
    public static final String LEFT_COLLAPSE = "LEFT_COLLAPSE";

    /**
     * redis导入开始事件
     */
    public static final String REDIS_IMPORT_START = "REDIS_IMPORT_START";

    /**
     * redis导入结束事件
     */
    public static final String REDIS_IMPORT_FINISH = "REDIS_IMPORT_FINISH";

    /**
     * redis搜索开始事件
     */
    public static final String REDIS_SEARCH_START = "REDIS_SEARCH_START";

    /**
     * redis搜索结束事件
     */
    public static final String REDIS_SEARCH_FINISH = "REDIS_SEARCH_FINISH";

    /**
     * redis搜索历史选择事件
     */
    public static final String REDIS_SEARCH_HISTORY_SELECTED = "REDIS_SEARCH_HISTORY_SELECTED";

    /**
     * redis替换历史选择事件
     */
    public static final String REDIS_REPLACE_HISTORY_SELECTED = "REDIS_REPLACE_HISTORY_SELECTED";

    /**
     * redis过滤历史选择事件
     */
    public static final String REDIS_FILTER_HISTORY_SELECTED = "REDIS_REPLACE_HISTORY_SELECTED";

    /**
     * 打开终端事件
     */
    public static final String REDIS_OPEN_TERMINAL = "REDIS_OPEN_TERMINAL";

    /**
     * 打开订阅事件
     */
    public static final String REDIS_OPEN_PUBSUB = "REDIS_OPEN_PUBSUB";

    /**
     * 服务信息事件
     */
    public static final String REDIS_SERVER_INFO = "REDIS_SERVER_INFO";

    /**
     * redis客户端关闭事件
     */
    public static final String REDIS_CLINE_CLOSED = "REDIS_CLINE_CLOSED";

    /**
     * 添加连接事件
     */
    public static final String REDIS_ADD_CONNECT = "REDIS_ADD_CONNECT";

    /**
     * 添加分组事件
     */
    public static final String REDIS_ADD_GROUP = "REDIS_ADD_GROUP";
}
