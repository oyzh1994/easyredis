package cn.oyzh.easyredis.terminal.command.bit;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.terminal.exception.TerminalException;
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
public class RedisSetbitCommand extends RedisGetbitCommand {

    private boolean value;

    public void valueOfString(String value) {
        if (StrUtil.equalsAnyIgnoreCase(value, "true", "1")) {
            this.value = true;
        } else if (StrUtil.equalsAnyIgnoreCase(value, "false", "0")) {
            this.value = false;
        } else {
            throw new TerminalException("bit " + value + " is invalid");
        }
    }
}
