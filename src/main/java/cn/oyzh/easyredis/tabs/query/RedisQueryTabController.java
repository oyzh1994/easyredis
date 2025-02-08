package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisDatabaseComboBox;
import cn.oyzh.easyredis.query.RedisQueryParam;
import cn.oyzh.easyredis.query.RedisQueryResult;
import cn.oyzh.easyredis.query.RedisQueryTextArea;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisQueryStore;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2025/02/06
 */
public class RedisQueryTabController extends DynamicTabController {

    /**
     * 查询对象
     */
    @Getter
    private RedisQuery query;

    /**
     * 未保存标志位
     */
    @Getter
    private boolean unsaved;

    /**
     * zk客户端
     */
    private RedisClient redisClient;

    /**
     * 当前内容
     */
    @FXML
    private RedisQueryTextArea content;

    /**
     * 数据库
     */
    @FXML
    private RedisDatabaseComboBox database;

    /**
     * 结果面板
     */
    @FXML
    private FlexTabPane resultTabPane;

    /**
     * 查询存储
     */
    private final RedisQueryStore queryStore = RedisQueryStore.INSTANCE;

    public RedisConnect redisConnect() {
        return this.redisClient.redisConnect();
    }

    public void init(RedisClient client, RedisQuery query) {
        this.redisClient = client;
        this.content.setClient(client);
        if (query == null) {
            query = new RedisQuery();
            query.setIid(client.iid());
            query.setName(I18nHelper.unnamedQuery());
            this.unsaved = true;
        } else {
            this.content.setText(query.getContent());
            this.content.setPromptText(null);
        }
        // 初始化数据库
        this.database.setDbCount(client.databases());
        this.database.setInitIndex(query.getDbIndex());
        // 监听数据库变化
        this.database.selectedIndexChanged((observable, oldValue, newValue) -> {
            this.unsaved = true;
            this.flushTab();
            this.content.setDbIndex(newValue.intValue());
        });
        // 监听内容变化
        this.content.addTextChangeListener((observable, oldValue, newValue) -> {
            this.unsaved = true;
            this.flushTab();
        });
        this.query = query;
    }

    /**
     * 保存
     */
    @FXML
    private void save() {
        try {
            this.query.setContent(this.content.getText());
            this.query.setDbIndex(this.database.getSelectedIndex());
            if (this.query.getUid() == null) {
                String name = MessageBox.prompt(I18nHelper.pleaseInputName());
                if (StringUtil.isNotBlank(name)) {
                    this.query.setName(name);
                    this.queryStore.insert(this.query);
                    RedisEventUtil.queryAdded(this.query);
                }
            } else {
                this.queryStore.update(this.query);
            }
            this.unsaved = false;
            this.flushTab();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 运行
     */
    @FXML
    private void run() {
        try {
            RedisQueryParam param = new RedisQueryParam();
            param.setContent(this.content.getText());
            param.setDbIndex(this.database.getSelectedIndex());
            RedisQueryResult result = this.redisClient.query(param);
            this.content.flexHeight("30% - 60");
            this.resultTabPane.setVisible(true);
            this.resultTabPane.clearChild();
            this.resultTabPane.addTab(new RedisQueryMsgTab(param, result));
            if (result.hasData()) {
                this.resultTabPane.addTab(new RedisQueryDataTab(result.getResult()));
                this.resultTabPane.select(1);
            }
            this.content.parentAutosize();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 内容键入事件
     *
     * @param event 事件
     */
    @FXML
    private void onContentKeyPressed(KeyEvent event) {
        if (KeyboardUtil.isCtrlS(event)) {
            this.save();
        } else if (KeyboardUtil.isCtrlR(event)) {
            this.run();
        }
    }

    @Override
    public void onCloseRequest(DynamicTab tab, Event event) {
        if (this.unsaved && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            super.onCloseRequest(tab, event);
        }
    }
}