package cn.oyzh.easyredis.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.Const;
import lombok.Data;
import redis.clients.jedis.resps.Slowlog;

/**
 * 慢查日志项目
 *
 * @author oyzh
 * @since 2023/8/1
 */
@Data
public class RedisSlowlogItem {

    /**
     * id
     */
    private long id;

    /**
     * 编号
     */
    private int index;

    /**
     * 指令
     */
    private String command;

    /**
     * 发生时间
     */
    private String timeStamp;

    /**
     * 客户端地址
     */
    private String clientHost;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 耗时
     */
    private long executionTime;

    /**
     * 从慢查日志生成
     *
     * @param slowlog 慢查日志
     * @return 慢查日志键
     */
    public static RedisSlowlogItem from(Slowlog slowlog) {
        RedisSlowlogItem item = new RedisSlowlogItem();
        item.setId(slowlog.getId());
        item.setClientName(slowlog.getClientName());
        item.setExecutionTime(slowlog.getExecutionTime());
        item.setClientHost(slowlog.getClientIpPort().toString());
        item.setCommand(StrUtil.join(" ", slowlog.getArgs()));
        item.setTimeStamp(Const.DATE_FORMAT.format(slowlog.getTimeStamp() * 1000));
        return item;
    }
}
