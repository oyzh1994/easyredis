package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis添加set成员
 *
 * @author oyzh
 * @since 2022/06/27
 */
//@Slf4j
@StageAttribute(
        title = "添加hash字段",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "row/redisHashFieldAdd.fxml"
)
public class RedisHashFieldAddController extends Controller {

    /**
     * 字段
     */
    @FXML
    private FlexTextArea fieldValue;

    /**
     * 行数据
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * redis键
     */
    private RedisHashKeyTreeItem treeItem;

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        try {
            String fieldValue = this.fieldValue.getText();
            if (fieldValue == null) {
                MessageBox.tipMsg("字段不能为空", this.fieldValue);
                return;
            }
            // 行数据
            String rowValue = this.rowValue.getText();
            if (StrUtil.isEmpty(rowValue)) {
                MessageBox.tipMsg("数据不能为空", this.rowValue);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            if (client.hexists(dbIndex, key, fieldValue)) {
                MessageBox.warn("此字段已存在！");
                return;
            }
            // 添加元素
            client.hset(dbIndex, key, fieldValue, rowValue);
            // 发送事件
            // EventUtil.fire(RedisEventTypes.REDIS_HASH_FIELD_ADDED, this.treeItem);
            RedisEventUtil.hashFieldAddedMsg(this.treeItem);
            MessageBox.okToast("新增字段成功！");
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
                JSONObject json = JSON.parseObject(text, Feature.OrderedField);
                String jsonStr = JSONObject.toJSONString(json);
                this.rowValue.setText(jsonStr);
                this.rowValue.setUserData("text");
            } else if (text.contains("{") || text.contains("[") || "text".equals(this.rowValue.getUserData())) {
                JSONObject json = JSON.parseObject(text, Feature.OrderedField);
                String jsonStr = JSONObject.toJSONString(json, true);
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
