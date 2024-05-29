// package cn.oyzh.easyredis.terminal.handler.server;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
// import cn.oyzh.fx.terminal.command.TerminalCommand;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
//
// /**
//  * @author oyzh
//  * @since 2023/7/21
//  */
// @Component
// public class RedisSlowlogResetTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {
//
//     @Override
//     protected boolean checkArgs(String[] words) {
//         return words.length == 2;
//     }
//
//     @Override
//     public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
//         TerminalExecuteResult result = new TerminalExecuteResult();
//         try {
//             String slowlogReset = terminal.client().slowlogReset();
//             result.setResult(RedisTerminalUtil.formatOut(slowlogReset));
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             result.setException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public String commandName() {
//         return "SLOWLOG";
//     }
//
//     @Override
//     public String commandSubName() {
//         return "RESET";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "清空慢查日志";
//     }
// }
