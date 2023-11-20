package cn.oyzh.easyredis.shell;


import cn.oyzh.fx.terminal.key.BaseTerminalKeyHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class RedisTerminalKeyHandler extends BaseTerminalKeyHandler<RedisTerminalTextArea> {

    /**
     * 当前实例
     */
    public static final RedisTerminalKeyHandler INSTANCE = new RedisTerminalKeyHandler();

    @Override
    public boolean onEnterKeyPressed(RedisTerminalTextArea terminal) throws Exception {
        if (terminal.isTemporary() && !terminal.isConnected()) {
            String input = terminal.getInput();
            terminal.connect(input);
            terminal.saveHistory(input);
        } else if (!terminal.isConnecting()) {
            super.onEnterKeyPressed(terminal);
        }
        return false;
    }
}
