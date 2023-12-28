package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import redis.clients.jedis.params.XAddParams;

import java.util.Map;


/**
 * redis添加stream消息
 *
 * @author oyzh
 * @since 2023/07/07
 */
@StageAttribute(
        title = "添加stream消息",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = RedisConst.FXML_BASE_PATH + "row/redisStreamMessageAdd.fxml"
)
public class RedisStreamMessageAddController extends Controller {

    /**
     * 消息内容
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * 消息id
     */
    @FXML
    private ClearableTextField streamID;

    /**
     * redis键
     */
    private RedisStreamKeyTreeItem treeItem;

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        try {
            // 行数据
            String rowValue = this.rowValue.getText();
            if (StrUtil.isEmpty(rowValue)) {
                MessageBox.tipMsg("消息内容不能为空", this.rowValue);
                return;
            }
            if (!JSONUtil.isTypeJSON(rowValue)) {
                MessageBox.warn("消息内容必须为json键值对");
                return;
            }
            JSONObject fields = JSONUtil.parseObj(rowValue);
            if (fields.isEmpty()) {
                MessageBox.tipMsg("消息内容不能为空", this.rowValue);
                return;
            }
            String streamIDText = this.streamID.getText();
            if (streamIDText == null) {
                MessageBox.tipMsg("消息id不能为空", this.streamID);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            // 流添加参数
            XAddParams params = new XAddParams();
            params.id(streamIDText);
            // 添加流
            client.xadd(dbIndex, key, (Map) fields, params);
            // 发送事件
            // EventUtil.fire(RedisEventTypes.REDIS_STREAM_MESSAGE_ADDED, this.treeItem);
            RedisEventUtil.streamMessageAdded(this.treeItem, key, rowValue);
            // MessageBox.okToast("新增消息成功！");
            this.closeStage();
        } catch (Exception ex) {
            ex.printStackTrace();
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
        this.rowValue.requestFocus();
        super.onStageShown(event);
    }

}
