package cn.oyzh.easyredis.terminal.command.zset;

import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisZincrbyTerminalCommand extends RedisKeyTerminalCommand {

    private double increment;

    private String member;
}
