// package cn.oyzh.easyredis.terminal.handler.key;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
// import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
// import cn.oyzh.fx.terminal.command.TerminalCommand;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
// import redis.clients.jedis.Protocol;
//
// /**
//  * @author oyzh
//  * @since 2023/7/31
//  */
// @Component
// public class RedisObjectEncodingTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {
//
//     @Override
//     public String commandName() {
//         return "OBJECT";
//     }
//
//     @Override
//     protected Protocol.Command getCommandType() {
//         return Protocol.Command.OBJECT;
//     }
//
//     @Override
//     public String commandSubName() {
//         return "ENCODING";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "获取键编码信息";
//     }
//
// }
