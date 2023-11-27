package cn.oyzh.easyredis.controller.key;

import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.fx.RedisDBComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.SubmitButton;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventUtil;
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
//@Slf4j
@StageAttribute(
        title = "Redis键移动",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyMove.fxml"
)
public class RedisKeyMoveController extends Controller {

    /**
     * 键
     */
    @FXML
    private ClearableTextField key;

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
                MessageBox.warn("移动键失败！");
            } else {
                // 设置新键的ttl
                if (ttl > 0) {
                    this.client.expire(targetDBIndex, key, ttl, null);
                }
                this.treeItem.getTreeView().setProp("targetDB", targetDBIndex);
                EventUtil.fire(RedisEventTypes.REDIS_KEY_MOVED, this.treeItem);
                MessageBox.okToast("移动键成功！");
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
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
    }
}
