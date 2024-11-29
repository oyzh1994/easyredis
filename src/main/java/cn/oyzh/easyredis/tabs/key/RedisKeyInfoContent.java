package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.text.FlexLabel;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis键信息组件
 *
 * @author oyzh
 * @since 2023/08/03
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class RedisKeyInfoContent implements Initializable {

    /**
     * 根节点
     */
    @FXML
    private FXTab infoRoot;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * redis键
     */
    private RedisKey redisKey;

    /**
     * redis树节点
     */
    private RedisKeyTreeItem<?, ?> treeItem;

    /**
     * 编码
     */
    @FXML
    private FlexLabel objectEncoding;

    /**
     * 空闲时间
     */
    @FXML
    private FlexLabel objectIdletime;

    /**
     * 引用数量
     */
    @FXML
    private FlexLabel objectRefcount;

    /**
     * 内存占用
     */
    @FXML
    private FlexLabel memoryUsage;

    /**
     * 复制信息
     */
    @FXML
    private void copy() {
        String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                I18nHelper.database() + ": " + this.treeItem.dbIndex() + System.lineSeparator() +
                I18nHelper.encoding() + ": " + this.redisKey.objectedEncoding() + System.lineSeparator() +
                I18nHelper.idleTime() + ": " + this.redisKey.objectIdletime() + System.lineSeparator() +
                I18nHelper.refcount() + ": " + this.redisKey.objectRefcount() + System.lineSeparator() +
                I18nHelper.memoryUsage() + ": " + this.memoryUsage.getText();
        ClipboardUtil.setStringAndTip(builder, "键信息");
    }

    /**
     * 刷新信息
     */
    @FXML
    private void refresh() {
        this.initObject();
    }

    /**
     * 初始化组件
     *
     * @param treeItem redis树键
     */
    public void init(RedisKeyTreeItem<?, ?> treeItem) {
        // 重置渲染标志位
        this.treeItem = treeItem;
        this.redisKey = treeItem.value();
        this.client = treeItem.client();
    }

    /**
     * 初始化对象
     */
    protected void initObject() {
        try {
            String key = this.treeItem.key();
            int dbIndex = this.treeItem.dbIndex();
            RedisKeyUtil.keyObject(this.redisKey, dbIndex, key, this.client);
            this.objectIdletime.setText(this.redisKey.objectIdletimeString());
            this.objectRefcount.setText(this.redisKey.objectRefcountString());
            this.objectEncoding.setText(this.redisKey.objectedEncodingString());
            Long memoryUsage = this.treeItem.memoryUsage();
            if (memoryUsage == null || memoryUsage < 0) {
                this.memoryUsage.setText("N/A");
            } else if (memoryUsage < 1024) {
                this.memoryUsage.setText(memoryUsage + "bytes");
            } else if (memoryUsage < 1024 * 1024) {
                this.memoryUsage.setText(memoryUsage / 1024.0 + "Kb");
            } else if (memoryUsage < 1024 * 1024 * 1024) {
                this.memoryUsage.setText(memoryUsage / 1024.0 / 1024 + "Mb");
            } else if (memoryUsage < 1024 * 1024 * 1024 * 1024L) {
                this.memoryUsage.setText(memoryUsage / 1024.0 / 1024 / 1024 + "Gb");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 选中状态监听
        this.infoRoot.selectedProperty().addListener((observable, oldValue, newValue) -> this.initObject());
    }
}
