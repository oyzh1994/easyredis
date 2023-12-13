package cn.oyzh.easyredis.terminal;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.dto.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnState;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.terminal.TerminalTextArea;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * redis终端
 *
 * @author oyzh
 * @since 2023/7/21
 */
//@Slf4j
public class RedisTerminalTextArea extends TerminalTextArea {

    {
        this.historyHandler(RedisTerminalHistoryHandler.INSTANCE);
        this.completeHandler(RedisTerminalCompleteHandler.INSTANCE);
        this.keyHandler(RedisTerminalKeyHandler.INSTANCE);
        this.mouseHandler(RedisTerminalMouseHandler.INSTANCE);
    }

    /**
     * redis客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisClient client;

    /**
     * redis客户端连接状态监听器
     */
    private ChangeListener<RedisConnState> stateChangeListener;

    @Override
    public void flushPrompt() {
        if (!this.client.isConnected()) {
            this.prompt("redis连接@" + this.client.infoName() + "> ");
        } else {
            this.prompt("redis连接@" + this.client.infoName() + "（已连接）> ");
        }
    }

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.disableInput();
        this.appendLine("欢迎使用EasyRedis!");
        this.appendLine("Powered By oyzh(2023-2023).");
        if (this.isTemporary()) {
            this.initByTemporary();
        } else {
            this.initByPermanent();
        }
    }

    /**
     * 是否临时连接
     *
     * @return 结果
     */
    public boolean isTemporary() {
        return this.client.redisInfo().getId() == null;
    }

    @Override
    public void outputPrompt() {
        if (!this.client.isConnecting()) {
            super.outputPrompt();
        }
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 是否已关闭
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.client != null && this.client.isClosed();
    }

    /**
     * 执行连接
     *
     * @param input 输入内容
     */
    public void connect(String input) {
        this.client.reset();
        RedisConnect connect = RedisConnectUtil.parse(input);
        if (connect != null) {
            this.client.redisInfo().setHost(connect.getHost() + ":" + connect.getPort());
            this.client.redisInfo().setPassword(connect.getPassword());
            this.intConnStat();
            this.disable();
            this.client.start(connect.getDb());
            this.enable();
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        this.appendLine("请输入连接地址然后回车，格式-h host [-p port] [-a password] [-n db]");
        this.appendText("-h 127.0.0.1");
        ExecutorUtil.start(() -> {
            FXUtil.runLater(this::requestFocus);
            this.enableInput();
            this.flushCaret();
            this.moveCaretEnd();
        }, 10);
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
        this.appendLine(this.client.redisInfo().getHost() + " 连接开始.");
        ExecutorUtil.start(() -> {
            this.intConnStat();
            this.client.start();
        }, 10);
    }

    /**
     * 初始化连接状态处理
     */
    private void intConnStat() {
        if (this.stateChangeListener == null) {
            this.stateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.client.redisInfo().getHost();
                if (t1 == RedisConnState.CONNECTED) {
                    this.outputLine(host + " 连接成功.");
                    this.outputLine("输入help可查看支持的命令列表.");
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == RedisConnState.CLOSED) {
                    this.disableInput();
                    this.outputLine(host + " 连接关闭.");
                } else if (t1 == RedisConnState.BROKEN) {
                    this.disableInput();
                    this.outputLine(host + " 连接中断.");
                } else if (t1 == RedisConnState.FAILED) {
                    this.disableInput();
                    this.outputLine(host + " 连接失败.");
                    this.flushCaret();
                }
                StaticLog.info("connState={}", t1);
            };
        }
        this.client().addStateListener(this.stateChangeListener);
    }

    @Override
    public void enableInput() {
        if (this.isConnected() || this.isTemporary()) {
            super.enableInput();
        }
    }
}
