package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * redis添加set成员
 *
 * @author oyzh
 * @since 2022/06/27
 */
@Slf4j
@StageAttribute(
        title = "添加zset成员",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "row/redisZSetMemberAdd.fxml"
)
public class RedisZSetMemberAddController extends Controller {

    /**
     * 行数据
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * 分数
     */
    @FXML
    private DecimalTextField score;

    /**
     * redis键
     */
    private RedisZSetKeyTreeItem treeItem;

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
            Number scoreValue = this.score.getValue();
            if (scoreValue == null) {
               MessageBox.tipMsg("分数不能为空", this.score);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            if (client.zrank(dbIndex, key, rowValue) != null) {
                MessageBox.warn("此成员已存在！");
                return;
            }
            // 添加元素
            client.zadd(dbIndex, key, scoreValue.doubleValue(), rowValue);
            // 发送事件
            // EventUtil.fire(RedisEventTypes.REDIS_ZSET_MEMBER_ADDED, this.treeItem);
            RedisEventUtil.zSetMemberAdded(this.treeItem);
            MessageBox.okToast("新增成员成功！");
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
