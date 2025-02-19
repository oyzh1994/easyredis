package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.terminal.RedisTerminalTextAreaPane;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import javafx.event.Event;
import javafx.fxml.FXML;
import lombok.NonNull;

/**
 * redis命令行tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class RedisTerminalTabController extends DynamicTabController {

    /**
     * redis命令行文本域
     */
    @FXML
    private RedisTerminalTextAreaPane terminal;

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client, Integer dbIndex) {
        this.terminal.init(client, dbIndex);
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    protected RedisConnect redisConnect() {
        return this.terminal.redisConnect();
    }

    public Integer dbIndex() {
        return this.terminal.dbIndex();
    }

    public RedisClient client() {
        return this.terminal.client();
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        if (this.terminal.isTemporary()) {
            RedisConnectUtil.close(this.client(), true, true);
        }
        super.onTabClose(tab, event);
    }
}
