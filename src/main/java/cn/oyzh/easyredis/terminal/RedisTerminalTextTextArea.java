package cn.oyzh.easyredis.terminal;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ExecutorUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.dto.RedisConnectInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnState;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.TerminalTextArea;
import cn.oyzh.i18n.I18nHelper;
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
public class RedisTerminalTextTextArea extends TerminalTextArea {

    {
        this.keyHandler(RedisTerminalKeyHandler.INSTANCE);
        this.helpHandler(RedisTerminalHelpHandler.INSTANCE);
        this.mouseHandler(RedisTerminalMouseHandler.INSTANCE);
        this.historyHandler(RedisTerminalHistoryHandler.INSTANCE);
        this.completeHandler(RedisTerminalCompleteHandler.INSTANCE);
    }

    @Override
    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        RedisSetting setting= RedisSettingStore.SETTING;
        this.setFontSize(setting.getTerminalFontSize());
        this.setFontFamily(setting.getTerminalFontFamily());
        this.setFontWeight2(setting.getTerminalFontWeight());
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
    private RedisConnectInfo connect;

    /**
     * redis客户端连接状态监听器
     */
    private ChangeListener<RedisConnState> stateChangeListener;

    /**
     * db索引
     */
    @Getter
    @Accessors(fluent = true, chain = false)
    private Integer dbIndex;

    @Override
    public void flushPrompt() {
        String str;
        if (this.isTemporary()) {
            str = "redis " + I18nHelper.connection();
        } else {
            str = this.client.connectName();
        }
        if (this.redisConnect().getHost() != null) {
            str += "@" + this.redisConnect().getHost();
        }
        if (this.isConnecting()) {
            str += "(" + I18nHelper.connectIng() + this.getDbName() + ")> ";
        } else if (this.isConnected()) {
            if (this.client.isReadonly()) {
                str += "(" + I18nHelper.connected() + "/" + I18nHelper.readonlyMode() + this.getDbName() + ")> ";
            } else {
                str += "(" + I18nHelper.connected() + this.getDbName() + ")> ";
            }
        } else {
            str += this.getDbName() + "> ";
        }
        this.prompt(str);
    }

    private String getDbName() {
        return this.dbIndex == null ? "" : "@db" + this.dbIndex;
    }

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(@NonNull RedisClient client, Integer dbIndex) {
        this.client = client;
        this.dbIndex = dbIndex;
        this.disableInput();
        this.outputLine(I18nResourceBundle.i18nString("redis.home.welcome"));
        this.appendLine("Powered By oyzh(2023-2025).");
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
        return this.client.iid() == null;
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
            RedisConnectUtil.copyConnect(this.connect, this.redisConnect());
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
        this.initStatListener();
        ExecutorUtil.start(() -> {
            try {
                this.disable();
                this.client.startDatabase(db);
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
    private void initStatListener() {
        if (this.stateChangeListener == null) {
            this.stateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.client.redisConnect().getHost();
                if (t1 == RedisConnState.CONNECTED) {
                    this.outputLine(I18nHelper.terminalTip2());
                    this.outputLine(I18nHelper.terminalTip1());
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == RedisConnState.CLOSED) {
                    this.outputLine(host + " " + I18nHelper.connectionClosed() + " .");
                    this.enableInput();
                } else if (t1 == RedisConnState.CONNECTING) {
                    this.outputLine(host + " " + I18nHelper.connectionConnecting() + " .");
                    this.disableInput();
                } else if (t1 == RedisConnState.BROKEN) {
                    this.outputLine(host + " " + I18nHelper.connectionBroken() + " .");
                    this.enableInput();
                } else if (t1 == RedisConnState.FAILED) {
                    this.outputLine(host + " " + I18nHelper.connectFail() + " .");
                    if (this.connect != null) {
                        this.appendByPrompt(this.connect.getInput());
                    }
                    this.flushAndMoveCaretEnd();
                    this.enableInput();
                }
                JulLog.info("connState={}", t1);
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

    public RedisConnect redisConnect() {
        return this.client().redisConnect();
    }
}
