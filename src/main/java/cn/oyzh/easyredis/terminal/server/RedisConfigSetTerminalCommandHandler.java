// package cn.oyzh.easyredis.terminal.handler.server;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.command.server.RedisConfigSetTerminalCommand;
// import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
// import cn.oyzh.fx.common.util.ArrUtil;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
//
// /**
//  * @author oyzh
//  * @since 2023/7/28
//  */
// @Component
// public class RedisConfigSetTerminalCommandHandler extends RedisTerminalCommandHandler<RedisConfigSetTerminalCommand> {
//
//     @Override
//     protected boolean checkArgs(String[] words) {
//         return words.length > 3 && words.length % 2 == 0;
//     }
//
//     @Override
//     protected RedisConfigSetTerminalCommand parseCommand(String line, String[] words) {
//         RedisConfigSetTerminalCommand command = new RedisConfigSetTerminalCommand();
//         command.parameterValues(ArrUtil.sub(words, 2));
//         return command;
//     }
//
//     @Override
//     public TerminalExecuteResult execute(RedisConfigSetTerminalCommand command, RedisTerminalTextArea terminal) {
//         TerminalExecuteResult result = new TerminalExecuteResult();
//         try {
//             String msg = terminal.client().configSet(command.parameterValues());
//             result.setResult(RedisTerminalUtil.formatOut(msg));
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             result.setException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public String commandName() {
//         return "CONFIG";
//     }
//
//     @Override
//     public String commandSubName() {
//         return "SET";
//     }
//
//     @Override
//     public String commandArg() {
//         return "parameter value [parameter value...]";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "更改配置";
//     }
// }
