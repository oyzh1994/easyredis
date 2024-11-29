package cn.oyzh.easyredis.terminal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.util.TerminalUtil;

import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/31
 */
public abstract class RedisNKeysTerminalCommandHandler<C extends TerminalCommand> extends RedisTerminalCommandHandler<C> {

    @Override
    public boolean completion(String line, RedisTerminalTextTextArea terminal) {
        String[] words = TerminalUtil.split(line);
        if (words.length >= 1) {
            String key = line.substring(this.commandFullName().length());
            String pattern = StrUtil.isBlank(key) ? "*" : key + "*";
            Set<String> keys = terminal.client().keys(null, pattern, this.getKeyType());
            if (CollUtil.isEmpty(keys)) {
                return false;
            }
            if (keys.size() == 1) {
                terminal.coverInput(words[0] + " " + CollUtil.getFirst(keys));
            } else {
                String textFormat = TextUtil.beautifyFormat(keys, 3, 0);
                terminal.outputByPrompt(textFormat);
                terminal.outputPrompt();
                terminal.output(line);
            }
            return true;
        }
        return false;
    }

    protected RedisKeyType getKeyType() {
        return null;
    }
}
