// package cn.oyzh.easyredis.terminal.handler.server;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.command.server.RedisSlowlogGetTerminalCommand;
// import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
// import redis.clients.jedis.resps.Slowlog;
//
// import java.util.List;
//
// /**
//  * @author oyzh
//  * @since 2023/7/21
//  */
// @Component
// public class RedisSlowlogGetTerminalCommandHandler extends RedisTerminalCommandHandler<RedisSlowlogGetTerminalCommand> {
//
//     @Override
//     protected boolean checkArgs(String[] words) {
//         return words.length == 2 || words.length == 3;
//     }
//
//     @Override
//     protected RedisSlowlogGetTerminalCommand parseCommand(String line, String[] words) {
//         RedisSlowlogGetTerminalCommand command = new RedisSlowlogGetTerminalCommand();
//         if (words.length == 3) {
//             command.entries(Long.valueOf(words[2]));
//         }
//         return command;
//     }
//
//     @Override
//     public TerminalExecuteResult execute(RedisSlowlogGetTerminalCommand command, RedisTerminalTextArea terminal) {
//         TerminalExecuteResult result = new TerminalExecuteResult();
//         try {
//             List<Slowlog> slowlogList;
//             if (command.entries() == null) {
//                 slowlogList = terminal.client().slowlogGet();
//             } else {
//                 slowlogList = terminal.client().slowlogGet(command.entries());
//             }
//             result.setResult(RedisTerminalUtil.formatOut(slowlogList));
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
//         return "GET";
//     }
//
//     @Override
//     public String commandArg() {
//         return "[entries]";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "获取慢查日志";
//     }
// }
