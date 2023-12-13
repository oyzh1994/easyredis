package cn.oyzh.easyredis.terminal.command.hash;

import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisHmsetTerminalCommand extends RedisKeyTerminalCommand {

    private Map<String, String> hash;

}
