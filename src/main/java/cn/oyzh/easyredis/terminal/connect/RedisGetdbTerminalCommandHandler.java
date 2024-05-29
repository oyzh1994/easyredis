// package cn.oyzh.easyredis.terminal.handler.connect;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
// import cn.oyzh.fx.terminal.command.TerminalCommand;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
// import redis.clients.jedis.Protocol;
//
// /**
//  * @author oyzh
//  * @since 2023/7/21
//  */
// @Component
// public class RedisGetdbTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {
//
//     @Override
//     public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
//         TerminalExecuteResult result = new TerminalExecuteResult();
//         try {
//             int db = terminal.client().getDB();
//             result.setResult(RedisTerminalUtil.formatOut(db));
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             result.setException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public String commandName() {
//         return "GETDB";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "获取数据库索引";
//     }
//
//
// }
