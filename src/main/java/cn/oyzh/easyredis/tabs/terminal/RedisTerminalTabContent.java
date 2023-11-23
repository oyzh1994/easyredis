package cn.oyzh.easyredis.tabs.terminal;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * redis命令行tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
@Lazy
@Component
@Scope("prototype")
public class RedisTerminalTabContent extends DynamicTabController {

    /**
     * redis客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisClient client;

    /**
     * redis命令行文本域
     */
    @FXML
    private RedisTerminalTextArea terminal;

    /**
     * 设置redis客户端
     *
     * @param client redis客户端
     */
    public void client(@NonNull RedisClient client) {
        this.client = client;
        this.terminal.init(client);
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    protected RedisInfo info() {
        return this.client.redisInfo();
    }

}
