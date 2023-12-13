package cn.oyzh.easyredis.terminal.handler.geo;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.geo.RedisGeoaddTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.GeoCoordinate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisGeoaddTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGeoaddTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 4 && ((words.length - 2) % 3 == 0 || (words.length - 3) % 3 == 0 || (words.length - 4) % 3 == 0);
    }

    @Override
    protected RedisGeoaddTerminalCommand parseCommand(String line, String[] words) {
        RedisGeoaddTerminalCommand command = new RedisGeoaddTerminalCommand();
        command.key(words[1]);
        command.paramsOfString(words[2]);
        command.paramsOfString(words[3]);
        int index = 2;
        if (StrUtil.equalsAnyIgnoreCase(words[2], "nx", "xx", "ch")) {
            index++;
        }
        if (StrUtil.equalsAnyIgnoreCase(words[3], "nx", "xx", "ch")) {
            index++;
        }
        Map<String, GeoCoordinate> members = new HashMap<>();
        for (int i = index; i < words.length; i += 3) {
            double longitude = Double.parseDouble(words[i]);
            double latitude = Double.parseDouble(words[i + 1]);
            String member = words[i + 2];
            members.put(member, new GeoCoordinate(longitude, latitude));
        }
        command.memberCoordinate(members);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGeoaddTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().geoadd(null, command.key(), command.params(), command.memberCoordinate());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GEOADD";
    }

    @Override
    public String commandArg() {
        return "key [NX | XX] [CH] longitude latitude member [longitude latitude member ...]";
    }

    @Override
    public String commandDesc() {
        return "添加geo坐标";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
