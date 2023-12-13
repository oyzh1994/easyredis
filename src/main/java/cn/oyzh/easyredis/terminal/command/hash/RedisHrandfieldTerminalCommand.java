package cn.oyzh.easyredis.terminal.command.hash;

import cn.hutool.core.util.StrUtil;
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
public class RedisHrandfieldTerminalCommand extends RedisKeyTerminalCommand {

    private Integer count;

    private Boolean withValues;

    public void withValuesOfString(String str) {
        this.withValues = StrUtil.equalsIgnoreCase(str, "WITHVALUES");
    }
}
