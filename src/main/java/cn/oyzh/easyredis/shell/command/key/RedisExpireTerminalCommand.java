package cn.oyzh.easyredis.shell.command.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import cn.oyzh.fx.terminal.exception.TerminalException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.args.ExpiryOption;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisExpireTerminalCommand extends RedisKeyTerminalCommand {

    private long time;

    private ExpiryOption option;

    public void optionOfString(String option) {
        if (StrUtil.equalsIgnoreCase(option, "nx")) {
            this.option = ExpiryOption.NX;
        } else if (StrUtil.equalsIgnoreCase(option, "xx")) {
            this.option = ExpiryOption.XX;
        } else if (StrUtil.equalsIgnoreCase(option, "gt")) {
            this.option = ExpiryOption.GT;
        } else if (StrUtil.equalsIgnoreCase(option, "lt")) {
            this.option = ExpiryOption.LT;
        } else {
            throw new TerminalException("unknown option \"" + option + "\"");
        }
    }

}
