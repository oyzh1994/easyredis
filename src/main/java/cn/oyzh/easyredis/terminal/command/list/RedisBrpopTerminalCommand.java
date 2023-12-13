package cn.oyzh.easyredis.terminal.command.list;

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
public class RedisBrpopTerminalCommand extends RedisBlpopTerminalCommand {


}
