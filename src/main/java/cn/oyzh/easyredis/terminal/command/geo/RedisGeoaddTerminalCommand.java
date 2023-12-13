package cn.oyzh.easyredis.terminal.command.geo;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.params.GeoAddParams;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisGeoaddTerminalCommand extends RedisKeyTerminalCommand {

    private GeoAddParams params = new GeoAddParams();

    private Map<String, GeoCoordinate> memberCoordinate;

    public void paramsOfString(String str) {
        if (StrUtil.equalsIgnoreCase(str, "nx")) {
            this.params.nx();
        } else if (StrUtil.equalsIgnoreCase(str, "xx")) {
            this.params.xx();
        } else if (StrUtil.equalsIgnoreCase(str, "ch")) {
            this.params.ch();
        }
    }
}
