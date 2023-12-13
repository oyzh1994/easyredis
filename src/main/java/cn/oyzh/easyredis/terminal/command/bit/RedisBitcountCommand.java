package cn.oyzh.easyredis.terminal.command.bit;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.args.BitCountOption;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisBitcountCommand extends RedisKeyTerminalCommand {

    private Long start;

    private Long end;

    private BitCountOption option;

    public void optionOfString(String str) {
        if (StrUtil.equalsIgnoreCase("bit", str)) {
            option = BitCountOption.BIT;
        } else if (StrUtil.equalsIgnoreCase("byte", str)) {
            option = BitCountOption.BYTE;
        }
    }
}
