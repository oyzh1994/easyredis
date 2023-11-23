package cn.oyzh.easyredis.event;

import lombok.experimental.UtilityClass;

/**
 * redis事件分组
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class RedisEventGroups {

    /**
     * 树操作
     */
    public static final String TREE_ACTION = "TREE_ACTION";

    /**
     * 信息操作
     */
    public static final String INFO_ACTION = "INFO_ACTION";

    /**
     * 搜索操作
     */
    public static final String SEARCH_ACTION = "SEARCH_ACTION";

    /**
     * 终端操作
     */
    public static final String TERMINAL_ACTION = "TERMINAL_ACTION";

    /**
     * 连接操作
     */
    public static final String CONNECTION_ACTION = "CONNECTION_ACTION";

    /**
     * 过滤操作
     */
    public static final String FILTER_ACTION = "FILTER_ACTION";

    /**
     * 键操作
     */
    public static final String KEY_ACTION = "KEY_ACTION";

}
