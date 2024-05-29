// package cn.oyzh.easyredis.terminal.handler.pubsub;
//
// import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
// import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
// import cn.oyzh.easyredis.terminal.command.pubsub.RedisPubsubChannelsTerminalCommand;
// import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
// import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
// import org.springframework.stereotype.Component;
// import redis.clients.jedis.Protocol;
//
// import java.util.List;
//
// /**
//  * @author oyzh
//  * @since 2023/7/27
//  */
// @Component
// public class RedisPubsubChannelsTerminalCommandHandler extends BaseTerminalCommandHandler<RedisPubsubChannelsTerminalCommand, RedisTerminalTextArea> {
//
//     @Override
//     protected boolean checkArgs(String[] words) {
//         return words.length == 2 || words.length == 3;
//     }
//
//     @Override
//     protected RedisPubsubChannelsTerminalCommand parseCommand(String line, String[] words) {
//         RedisPubsubChannelsTerminalCommand command = new RedisPubsubChannelsTerminalCommand();
//         if (words.length == 3) {
//             command.pattern(words[1]);
//         }
//         return command;
//     }
//
//     @Override
//     public TerminalExecuteResult execute(RedisPubsubChannelsTerminalCommand command, RedisTerminalTextArea terminal) {
//         TerminalExecuteResult result = new TerminalExecuteResult();
//         try {
//             List<String> pubsubChannels = terminal.client().pubsubChannels(command.pattern());
//             result.setResult(RedisTerminalUtil.formatOut(pubsubChannels));
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             result.setException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public String commandName() {
//         return "PUBSUB";
//     }
//
//     @Override
//     public String commandSubName() {
//         return "CHANNELS";
//     }
//
//     @Override
//     public String commandArg() {
//         return "pattern";
//     }
//
//     @Override
//     public String commandDesc() {
//         return "获取发布列表";
//     }
//
//     @Override
//     protected Protocol.Command getCommandType() {
//         return Protocol.Command.PUBSUB;
//     }
// }
