package cn.oyzh.easyredis.terminal.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterDelSlotsRangeTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandArg() {
        return "start-slot end-slot [start-slot end-slot ...]";
    }

    @Override
    public String commandDesc() {
        return "Sets hash slot ranges as unbound for a node.";
    }

    @Override
    public String commandSubName() {
        return "DELSLOTSRANGE";
    }
}
