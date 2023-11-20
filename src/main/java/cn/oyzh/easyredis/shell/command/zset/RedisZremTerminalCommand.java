package cn.oyzh.easyredis.shell.command.zset;

import cn.oyzh.easyredis.shell.command.set.RedisSaddTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisZremTerminalCommand extends RedisSaddTerminalCommand {

}
