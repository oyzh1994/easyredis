package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis键信息组件
 *
 * @author oyzh
 * @since 2023/08/03
 */
public class RedisKeyInfoController extends DynamicTabController {

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
    private RedisKeyTreeItem treeItem;

    /**
     * 编码
     */
    @FXML
    private FXLabel objectEncoding;

    /**
     * 空闲时间
     */
    @FXML
    private FXLabel objectIdletime;

    /**
     * 引用数量
     */
    @FXML
    private FXLabel objectRefcount;

    /**
     * 复制信息
     */
    @FXML
    private void copy() {
        String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                I18nHelper.database() + " : " + this.treeItem.dbIndex() + System.lineSeparator() +
                I18nHelper.encoding() + " : " + this.redisKey.objectedEncoding() + System.lineSeparator() +
                I18nHelper.idleTime() + " : " + this.redisKey.objectIdletime() + System.lineSeparator() +
                I18nHelper.refcount() + " : " + this.redisKey.objectRefcount() + System.lineSeparator();
        ClipboardUtil.setStringAndTip(builder);
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
    public void init(RedisKeyTreeItem treeItem) {
        // 重置渲染标志位
        this.treeItem = treeItem;
        this.redisKey = treeItem.value();
        this.client = treeItem.client();
        // 选中时则更新
        if (this.infoRoot.isSelected()) {
            this.initObject();
        }
    }

    /**
     * 初始化对象
     */
    protected void initObject() {
        if (this.treeItem != null) {
            try {
                String key = this.treeItem.key();
                int dbIndex = this.treeItem.dbIndex();
                RedisKeyUtil.keyObject(this.redisKey, dbIndex, key, this.client);
                this.objectIdletime.text(I18nHelper.idleTime() + " : " + this.redisKey.objectIdletimeString());
                this.objectRefcount.text(I18nHelper.refcount() + " : " + this.redisKey.objectRefcountString());
                this.objectEncoding.text(I18nHelper.encoding() + " : " + this.redisKey.objectedEncodingString());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        this.infoRoot.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.initObject();
            }
        });
    }
}
