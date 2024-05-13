package cn.oyzh.easyredis.controller.key;

import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisDBComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.SubmitButton;
import cn.oyzh.fx.plus.controls.textfield.DisabledTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis键移动业务
 *
 * @author oyzh
 * @since 2023/07/08
 */
@StageAttribute(
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyMove.fxml"
)
public class RedisKeyMoveController extends Controller {

    /**
     * 键
     */
    @FXML
    private DisabledTextField key;

    /**
     * 保留ttl
     */
    @FXML
    private FXCheckBox retainTTL;

    /**
     * 目标数据库
     */
    @FXML
    private RedisDBComboBox targetDB;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * 提交按钮
     */
    @FXML
    private SubmitButton submit;

    /**
     * 树节点
     */
    private RedisKeyTreeItem<?, ?> treeItem;

    /**
     * 转移键
     */
    @FXML
    private void moveKey() {
        String key = this.treeItem.key();
        int fromDBIndex = this.treeItem.dbIndex();
        int targetDBIndex = this.targetDB.getDB();
        try {
            this.client.throwClusterException();
            this.client.throwSentinelException();
            if (targetDBIndex == fromDBIndex) {
                MessageBox.warn("目标库和来源库不能是同一个！");
                return;
            }
            if (this.client.exists(targetDBIndex, key)) {
                MessageBox.warn("目标库已存在此键！");
                return;
            }
            // 保留ttl
            long ttl = -3;
            if (this.retainTTL.isSelected()) {
                ttl = this.client.ttl(fromDBIndex, key);
            }
            // 移动键
            long count = this.client.move(key, fromDBIndex, targetDBIndex);
            if (count <= 0) {
                MessageBox.warn(I18nHelper.operationFail());
            } else {
                // 设置新键的ttl
                if (ttl > 0) {
                    this.client.expire(targetDBIndex, key, ttl, null);
                }
                RedisEventUtil.keyMoved(this.treeItem, targetDBIndex);
                MessageBox.okToast(I18nHelper.operationSuccess());
                this.closeStage();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // 权限变化处理
        this.targetDB.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == this.treeItem.dbIndex()) {
                this.submit.disable();
            } else {
                this.submit.enable();
            }
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.treeItem = this.getStageProp("treeItem");
        this.client = this.treeItem.client();
        this.key.setText(this.treeItem.key() + "（db" + this.treeItem.dbIndex() + "）");
        this.targetDB.setDbCount(this.client.databases());
        this.targetDB.selectFirst();
        this.targetDB.requestFocus();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("redis.title.key.move");
    }
}
