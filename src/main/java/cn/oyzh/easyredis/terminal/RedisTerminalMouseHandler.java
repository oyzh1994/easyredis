package cn.oyzh.easyredis.terminal;


import cn.oyzh.fx.terminal.mouse.TerminalMouseHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class RedisTerminalMouseHandler implements TerminalMouseHandler<RedisTerminalTextTextArea> {

    /**
     * 当前实例
     */
    public static final RedisTerminalMouseHandler INSTANCE = new RedisTerminalMouseHandler();

}
