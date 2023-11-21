package cn.oyzh.easyredis.tabs.pubsub;

import cn.oyzh.easyredis.info.RedisPubsubItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.area.ReadOnlyTextArea;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * redis发布订阅内容组件
 *
 * @author oyzh
 * @since 2023/08/02
 */
@Lazy
@Component
@Scope("prototype")
public class RedisPubsubTabContentController {

    /**
     * 订阅组件
     */
    private JedisPubSub pubSub;

    /**
     * 文本域
     */
    @FXML
    private ReadOnlyTextArea textArea;

    /**
     * 初始化
     *
     * @param item redis发布订阅键
     */
    public void init(RedisPubsubItem item) {
        this.textArea.appendText("消息订阅已开始，关闭页签自动停止订阅，通道: " + item.getChannel());
        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                textArea.appendLine("收到消息: " + message);
            }
        };
        ThreadUtil.startVirtual(() -> item.getClient().subscribe(this.pubSub, item.getChannel()));
    }

    /**
     * 取消订阅
     */
    public void unsubscribe() {
        try {
            this.pubSub.unsubscribe();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
