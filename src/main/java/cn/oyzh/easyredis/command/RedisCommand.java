package cn.oyzh.easyredis.command;

import lombok.Data;

/**
 * @author oyzh
 * @since 2024/5/29
 */
@Data
public class RedisCommand {

    private String desc;

    private String args;

    private String command;

    private String available;
}
