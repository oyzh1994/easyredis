package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.trees.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.parser.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.handler.TabSwitchHandler;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.params.XAddParams;

import java.util.Map;


/**
 * redis添加stream消息
 *
 * @author oyzh
 * @since 2022/07/07
 */
@Slf4j
@StageAttribute(
        title = "添加stream消息",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
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
            JSONObject fields;
            try {
                fields = JSON.parseObject(rowValue);
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.warn("消息内容必须为json键值对");
                return;
            }
            if (fields == null || fields.isEmpty()) {
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
            client.xadd(dbIndex, key, (Map) fields.getInnerMap(), params);
            // 发送事件
            EventUtil.fire(RedisEventTypes.REDIS_STREAM_MESSAGE_ADDED, this.treeItem);
            MessageBox.okToast("新增消息成功！");
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
