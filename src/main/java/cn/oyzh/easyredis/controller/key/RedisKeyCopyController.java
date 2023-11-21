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
import lombok.extern.slf4j.Slf4j;


/**
 * redis键复制业务
 *
 * @author oyzh
 * @since 2023/08/09
 */
@Slf4j
@StageAttribute(
        title = "Redis键复制",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyCopy.fxml"
)
public class RedisKeyCopyController extends Controller {

    /**
     * 键
     */
    @FXML
    private ClearableTextField key;

    /**
     * 存在时替换
     */
    @FXML
    private FXCheckBox replace;

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
    private RedisKeyTreeItem<?> treeItem;

    /**
     * 转移键
     */
    @FXML
    private void copyKey() {
        String key = this.treeItem.key();
        int fromDBIndex = this.treeItem.dbIndex();
        int targetDBIndex = this.targetDB.getDB();
        try {
            this.client.throwClusterException();
            this.client.throwSentinelException();
            this.client.throwCommandException("copy");
            if (targetDBIndex == fromDBIndex) {
                MessageBox.warn("目标库和来源库不能是同一个！");
                return;
            }
            // 保留ttl
            long ttl = -3;
            // 移动键
            boolean result = this.client.copy(fromDBIndex, key, key, targetDBIndex, this.replace.isSelected());
            if (!result) {
                MessageBox.warn("复制键失败！");
            } else {
                this.treeItem.treeView().setProp("targetDB", targetDBIndex);
                EventUtil.fire(RedisEventTypes.REDIS_KEY_COPY, this.treeItem);
                MessageBox.okToast("复制键成功！");
            }
            this.closeStage();
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
}
