package cn.oyzh.easyredis.shell.command.geo;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import cn.oyzh.fx.terminal.exception.TerminalException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.args.GeoUnit;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisGeodistTerminalCommand extends RedisKeyTerminalCommand {

    private String member1;

    private String member2;

    private GeoUnit unit;

    public void unitOfString(String unit) {
        if (StrUtil.equalsIgnoreCase(unit, "km")) {
            this.unit = GeoUnit.KM;
        } else if (StrUtil.equalsIgnoreCase(unit, "m")) {
            this.unit = GeoUnit.M;
        } else if (StrUtil.equalsIgnoreCase(unit, "mi")) {
            this.unit = GeoUnit.MI;
        } else if (StrUtil.equalsIgnoreCase(unit, "ft")) {
            this.unit = GeoUnit.FT;
        } else {
            throw new TerminalException("unknown geounit " + unit);
        }
    }
}
