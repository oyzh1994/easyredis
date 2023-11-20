package cn.oyzh.easyredis.shell.command.stream;

import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.params.XAddParams;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisXaddTerminalCommand extends RedisKeyTerminalCommand {

    private XAddParams addParams;

    private Map<String, String> hash;

    public void id(String id) {
        this.addParams = XAddParams.xAddParams();
        this.addParams.id(id);
    }

}
