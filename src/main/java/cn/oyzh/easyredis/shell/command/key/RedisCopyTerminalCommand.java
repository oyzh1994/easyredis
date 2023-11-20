package cn.oyzh.easyredis.shell.command.key;

import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisCopyTerminalCommand extends RedisKeyTerminalCommand {

    private String dstKey;

    private Integer db;

    private boolean replace;

}
