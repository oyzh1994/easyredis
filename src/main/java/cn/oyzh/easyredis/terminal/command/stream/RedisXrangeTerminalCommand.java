package cn.oyzh.easyredis.terminal.command.stream;

import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.StreamEntryID;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisXrangeTerminalCommand extends RedisKeyTerminalCommand {

    private StreamEntryID start;

    private StreamEntryID end;

    private Integer count;

    public void startOfString(String id) {
        if (!id.contains("-")) {
            id = id + "-0";
        }
        this.start = new StreamEntryID(id);
    }

    public void endOfString(String id) {
        if (!id.contains("-")) {
            id = id + "-0";
        }
        this.end = new StreamEntryID(id);
    }

}
