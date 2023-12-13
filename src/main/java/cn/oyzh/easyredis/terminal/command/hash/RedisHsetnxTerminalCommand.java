package cn.oyzh.easyredis.terminal.command.hash;

import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisHsetnxTerminalCommand extends RedisKeyTerminalCommand {

    private String field;

    private String value;

}
