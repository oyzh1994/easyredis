package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FXToggleGroup;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis添加list行
 *
 * @author oyzh
 * @since 2023/06/25
 */
//@Slf4j
@StageAttribute(
        title = "添加list行",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "row/redisListRowAdd.fxml"
)
public class RedisListRowAddController extends Controller {

    /**
     * 行数据
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * 插入模式
     */
    @FXML
    private FXToggleGroup insertMode;

    /**
     * redis键
     */
    private RedisListKeyTreeItem treeItem;

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        try {
            // 行数据
            String rowValue = this.rowValue.getText();
            if (StrUtil.isEmpty(rowValue)) {
                MessageBox.tipMsg("行数据不能为空", this.rowValue);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            // 添加行
            if (this.insertMode.selectedUserData().equals("0")) {
                client.lpushx(dbIndex, key, rowValue);
            } else if (this.insertMode.selectedUserData().equals("1")) {
                client.rpushx(dbIndex, key, rowValue);
            }
            // 发送事件
            // EventUtil.fire(RedisEventTypes.REDIS_LIST_ROW_ADDED, this.treeItem);
            RedisEventUtil.listRowAdded(this.treeItem, key, rowValue);
            // MessageBox.okToast("新增行成功！");
            this.closeStage();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.rowValue.paste();
        this.rowValue.requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.rowValue.clear();
        this.rowValue.requestFocus();
    }

    /**
     * 解析为json
     */
    @FXML
    private void parseToJson() {
        String text = this.rowValue.getTextTrim();
        try {
            if ("json".equals(this.rowValue.getUserData())) {
                String jsonStr = JSONUtil.toJsonStr(this.rowValue);
                this.rowValue.setText(jsonStr);
                this.rowValue.setUserData("text");
            } else if (text.contains("{") || text.contains("[") || "text".equals(this.rowValue.getUserData())) {
                String jsonStr = JSONUtil.toJsonPrettyStr(this.rowValue);
                this.rowValue.setText(jsonStr);
                this.rowValue.setUserData("json");
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.treeItem = this.getStageProp("treeItem");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        super.onStageShown(event);
    }

}
