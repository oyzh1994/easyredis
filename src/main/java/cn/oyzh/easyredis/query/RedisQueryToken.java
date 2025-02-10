package cn.oyzh.easyredis.query;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;
import lombok.ToString;
import redis.clients.jedis.Protocol;

import java.util.List;

/**
 * @author oyzh
 * @since 2025/01/21
 */
@Data
@ToString
public class RedisQueryToken {

    /**
     * 结束位置
     */
    private int endIndex;

    /**
     * 开始位置
     */
    private int startIndex;

    /**
     * 输入
     */
    private String input;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 null
     * 2 空格
     */
    private Character token;

    public boolean isEmpty() {
        return StringUtil.isEmpty(this.content);
    }

    public boolean isNotEmpty() {
        return StringUtil.isNotEmpty(this.content);
    }

    public boolean isPossibilityKeyword() {
        return this.token == null || this.token == ' ';
    }

    public boolean isPossibilityParam() {
        return this.token != null && this.token == ' ';
    }

    public boolean isPossibilityKey() {
        if (this.token != null && this.token == ' ') {
            List<Protocol.Command> commands = RedisQueryUtil.keyCommands();
            for (Protocol.Command command : commands) {
                if (StringUtil.startWithIgnoreCase(this.input, command.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
