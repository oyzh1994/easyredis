package cn.oyzh.easyredis.terminal;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.dto.RedisConnect;
import cn.oyzh.easyredis.exception.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnState;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.TerminalTextTextArea;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * redis终端文本域
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class RedisTerminalTextTextArea extends TerminalTextTextArea {

    {
        this.keyHandler(RedisTerminalKeyHandler.INSTANCE);
        this.helpHandler(RedisTerminalHelpHandler.INSTANCE);
        this.mouseHandler(RedisTerminalMouseHandler.INSTANCE);
        this.historyHandler(RedisTerminalHistoryHandler.INSTANCE);
        this.completeHandler(RedisTerminalCompleteHandler.INSTANCE);
        super.initContentPrompts();
    }

    /**
     * redis客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisClient client;

    /**
     * redis连接
     */
    private RedisConnect connect;

    /**
     * redis客户端连接状态监听器
     */
    private ChangeListener<RedisConnState> stateChangeListener;

    @Override
    public void flushPrompt() {
        String str;
        if (this.isTemporary()) {
            str = "redis " + I18nHelper.connection();
        } else {
            str = this.client.infoName();
        }
        if (this.info().getHost() != null) {
            str += "@" + this.info().getHost();
        }
        if (this.isConnecting()) {
            str += "（" + I18nHelper.connectIng() + "）> ";
        } else if (this.isConnected()) {
            if (this.client.isReadonly()) {
                str += "（" + I18nHelper.connected() + "/" + I18nHelper.readonlyMode() + "）> ";
            } else {
                str += "（" + I18nHelper.connected() + "）> ";
            }
        } else {
            str += " > ";
        }
        this.prompt(str);
    }

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client) {
        this.client = client;
        this.disableInput();
        this.outputLine(I18nResourceBundle.i18nString("redis.home.welcome"));
        // this.appendLine("欢迎使用EasyRedis!");
        this.appendLine("Powered By oyzh(2023-2023).");
        this.flushPrompt();
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
        this.connect = RedisConnectUtil.parse(input);
        if (this.connect != null) {
            this.disable();
            RedisConnectUtil.copyConnect(this.connect, this.info());
            this.start(this.connect.getDb());
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        // this.outputLine("请输入信息然后回车");
        this.outputLine("connect [-timeout timeout] -h host [-p port] [-u user] [-a password] [-n db] [-r]");
        this.outputLine("-timeout " + I18nResourceBundle.i18nString("base.unit", "base.ms"));
        this.outputLine("-h " + I18nHelper.host());
        this.outputLine("-p " + I18nHelper.port());
        this.outputLine("-u " + I18nHelper.userName());
        this.outputLine("-a " + I18nHelper.password());
        this.outputLine("-n " + I18nHelper.database());
        this.outputLine("-r " + I18nHelper.readonlyMode());
        this.appendByPrompt("connect -timeout 3000 -h 127.0.0.1 -p 6379 -n 0");
        this.enableInput();
        this.flushAndMoveCaretEnd();
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
        this.start(0);
    }

    /**
     * 开始连接
     */
    private void start(int db) {
        ExecutorUtil.start(() -> {
            try {
                this.intStatListener();
                this.client.start(db);
            } catch (Exception ex) {
                this.onError(RedisExceptionParser.INSTANCE.apply(ex));
            } finally {
                this.enable();
            }
        }, 10);
    }

    /**
     * 刷新光标并移动到尾部
     */
    private void flushAndMoveCaretEnd() {
        ExecutorUtil.start(() -> {
            this.flushCaret();
            this.moveCaretEnd();
        }, 50);
    }

    /**
     * 初始化连接状态处理
     */
    private void intStatListener() {
        if (this.stateChangeListener == null) {
            this.stateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.client.redisInfo().getHost();
                if (t1 == RedisConnState.CONNECTED) {
                    // this.outputLine(host + " 连接成功.");
                    // this.outputLine("输入\"help\"或者按下tab键可查看命令列表.");
                    // this.outputLine("输入\"命令 -?\"可查看此命令详情.");
                    this.outputLine(I18nHelper.terminalTip2());
                    this.outputLine(I18nHelper.terminalTip1());
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == RedisConnState.CLOSED) {
                    this.outputLine(host + " " + I18nHelper.connectionClosed() + " .");
                    // this.outputLine(host + " 连接关闭.");
                    this.enableInput();
                } else if (t1 == RedisConnState.CONNECTING) {
                    this.outputLine(host + " " + I18nHelper.connectionConnecting() + " .");
                    // this.outputLine(host + " 开始连接.");
                    this.disableInput();
                } else if (t1 == RedisConnState.BROKEN) {
                    this.outputLine(host + " " + I18nHelper.connectionBroken() + " .");
                    // this.outputLine(host + " 连接中断.");
                    this.enableInput();
                } else if (t1 == RedisConnState.FAILED) {
                    this.outputLine(host + " " + I18nHelper.connectFail() + " .");
                    // this.outputLine(host + " 连接失败.");
                    if (this.connect != null) {
                        this.appendByPrompt(this.connect.getInput());
                    }
                    this.flushAndMoveCaretEnd();
                    this.enableInput();
                }
                StaticLog.info("connState={}", t1);
            };
            this.client().addStateListener(this.stateChangeListener);
        }
    }

    @Override
    public void enableInput() {
        if (this.isConnected() || this.isTemporary()) {
            super.enableInput();
        }
    }

    public RedisInfo info() {
        return this.client().redisInfo();
    }
}
