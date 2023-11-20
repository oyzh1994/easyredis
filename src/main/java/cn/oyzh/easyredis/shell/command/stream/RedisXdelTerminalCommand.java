package cn.oyzh.easyredis.shell.command.stream;

import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
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
public class RedisXdelTerminalCommand extends RedisKeyTerminalCommand {

    private StreamEntryID[] ids;

    public void idsOfString(String[] ids) {
        this.ids = new StreamEntryID[ids.length];
        for (int i = 0; i < ids.length; i++) {
            this.ids[i] = new StreamEntryID(ids[i]);
        }
    }

}
