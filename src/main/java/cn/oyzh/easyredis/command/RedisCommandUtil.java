package cn.oyzh.easyredis.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.json.JSONUtil;
import lombok.experimental.UtilityClass;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * redis版本工具类
 *
 * @author oyzh
 * @since 2023/07/31
 */
@UtilityClass
public class RedisCommandUtil {

    /**
     * 版本缓存
     */
    private final static List<RedisCommand> COMMANDS = new ArrayList<>();

    static {
        try {
            URL url = RedisCommand.class.getResource("/redis_commands.json");
            String json = FileUtil.readString(url, CharsetUtil.CHARSET_UTF_8);
            if (StrUtil.isNotBlank(json)) {
                COMMANDS.addAll(JSONUtil.toBeanList(json, RedisCommand.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<RedisCommand> getCommands() {
        return COMMANDS;
    }

    /**
     * 检查指令是否支持
     *
     * @param command 指令
     */
    public static RedisCommand getCommand(String command) {
        if (command != null) {
            for (RedisCommand redisCommand : COMMANDS) {
                if (StrUtil.equalsIgnoreCase(redisCommand.getCommand(), command)) {
                    return redisCommand;
                }
            }
        }
        return null;
    }

    /**
     * 检查指令是否支持
     *
     * @param command 指令
     */
    public static String getCommandDesc(String command) {
        RedisCommand redisCommand = getCommand(command);
        return redisCommand == null || redisCommand.getDesc() == null ? "" : redisCommand.getDesc();
    }

    /**
     * 检查指令是否支持
     *
     * @param command 指令
     */
    public static String getCommandArgs(String command) {
        RedisCommand redisCommand = getCommand(command);
        return redisCommand == null || redisCommand.getArgs() == null ? "" : redisCommand.getArgs();
    }

    /**
     * 检查指令是否支持
     *
     * @param command 指令
     */
    public static String getCommandAvailable(String command) {
        RedisCommand redisCommand = getCommand(command);
        return redisCommand == null || redisCommand.getAvailable() == null ? "" : redisCommand.getAvailable();
    }

}
