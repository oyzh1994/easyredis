package cn.oyzh.easyredis.shell.command.bit;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import cn.oyzh.fx.terminal.exception.TerminalException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.params.BitPosParams;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisBitposCommand extends RedisKeyTerminalCommand {

    private boolean value;

    private BitPosParams params;

    private BitPosParams _params() {
        if (this.params == null) {
            this.params = BitPosParams.bitPosParams();
        }
        return this.params;
    }

    public void valueOfString(String value) {
        if (StrUtil.equalsAnyIgnoreCase(value, "true", "1")) {
            this.value = true;
        } else if (StrUtil.equalsAnyIgnoreCase(value, "false", "0")) {
            this.value = false;
        } else {
            throw new TerminalException("bit " + value + " is invalid");
        }
    }

    public void startOfString(String value) {
        try {
            this._params().start(Long.parseLong(value));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void endOfString(String value) {
        try {
            this._params().end(Long.parseLong(value));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
