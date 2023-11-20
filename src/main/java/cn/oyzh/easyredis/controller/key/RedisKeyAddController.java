package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.trees.RedisDBTreeItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisEvents;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.ToggleSwitch;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.stage.StageAttribute;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.params.XAddParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * redis键添加业务
 *
 * @author oyzh
 * @since 2023/06/22
 */
@Slf4j
@StageAttribute(
        title = "Redis键添加",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyAdd.fxml"
)
public class RedisKeyAddController extends Controller {

    /**
     * 键
     */
    @FXML
    private ClearableTextField key;

    /**
     * 位图组件
     */
    @FXML
    private FlexVBox bitBox;

    /**
     * stream组件
     */
    @FXML
    private FlexVBox streamBox;

    /**
     * string组件
     */
    @FXML
    private FlexVBox stringBox;

    /**
     * list组件
     */
    @FXML
    private FlexVBox listBox;

    /**
     * set组件
     */
    @FXML
    private FlexVBox setBox;

    /**
     * zSet组件
     */
    @FXML
    private FlexVBox zSetBox;

    /**
     * set组件
     */
    @FXML
    private FlexVBox hashBox;

    /**
     * hylog组件
     */
    @FXML
    private FlexVBox hylogBox;

    /**
     * geo组件
     */
    @FXML
    private FlexVBox geoBox;

    /**
     * 字段名
     */
    @FXML
    private FlexTextArea fieldValue;

    /**
     * bit值
     */
    @FXML
    private ToggleSwitch bitValue;

    /**
     * bit索引
     */
    @FXML
    private NumberTextField bitIndex;

    /**
     * ttl值
     */
    @FXML
    private NumberTextField ttlValue;

    /**
     * 分数值
     */
    @FXML
    private DecimalTextField scoreValue;

    /**
     * 经度值
     */
    @FXML
    private DecimalTextField longitudeValue;

    /**
     * 纬度值
     */
    @FXML
    private DecimalTextField latitudeValue;

    /**
     * 消息id组件
     */
    @FXML
    private FlexVBox root;

    /**
     * 消息id值
     */
    @FXML
    private ClearableTextField streamIDValue;

    /**
     * 键类型
     */
    @FXML
    private FlexComboBox<String> type;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * 树键
     */
    private RedisDBTreeItem treeItem;

    /**
     * 节点互斥组件
     */
    private final NodeGroupManage mutexes = new NodeGroupManage();

    /**
     * 获取值文本组件
     *
     * @return 值文本组件
     */
    private FlexTextArea valueTextArea() {
        FlexTextArea textArea;
        if (this.stringBox.isVisible()) {
            textArea = (FlexTextArea) this.stringBox.lookup("FlexTextArea");
        } else if (this.listBox.isVisible()) {
            textArea = (FlexTextArea) this.listBox.lookup("FlexTextArea");
        } else if (this.hylogBox.isVisible()) {
            textArea = (FlexTextArea) this.hylogBox.lookup("FlexTextArea");
        } else if (this.zSetBox.isVisible()) {
            textArea = (FlexTextArea) this.zSetBox.lookup("FlexTextArea");
        } else if (this.setBox.isVisible()) {
            textArea = (FlexTextArea) this.setBox.lookup("FlexTextArea");
        } else if (this.hashBox.isVisible()) {
            textArea = (FlexTextArea) this.hashBox.lookup("FlexTextArea");
        } else if (this.geoBox.isVisible()) {
            textArea = (FlexTextArea) this.geoBox.lookup("FlexTextArea");
        } else {
            textArea = (FlexTextArea) this.streamBox.lookup("FlexTextArea");
        }
        return textArea;
    }

    /**
     * 获取值内容
     *
     * @return 值内容
     */
    private String valueText() {
        return this.valueTextArea().getText();
    }

    /**
     * 添加redis键
     */
    @FXML
    private void addNode() {
        // 获取键值
        int type = this.type.getSelectedIndex();
        String key = this.key.getTextTrim();
        int dbIndex = this.treeItem.dbIndex();
        try {
            long ttl = this.ttlValue.getValue();
            if (ttl == 0) {
                MessageBox.warn("TTL设置为马上过期，新增键已取消！");
                return;
            }
            if (key.isEmpty()) {
               MessageBox.tipMsg("键名称不能为空！", this.key);
                return;
            }
            if (this.client.exists(dbIndex, key)) {
                MessageBox.warn("key:" + key + "已经存在！");
                return;
            }
            if (type == 0) {
                this.addStringNode(dbIndex, key);
            } else if (type == 1) {
                this.addListNode(dbIndex, key);
            } else if (type == 2) {
                this.addSetNode(dbIndex, key);
            } else if (type == 3) {
                this.addZSetNode(dbIndex, key);
            } else if (type == 4) {
                this.addHashNode(dbIndex, key);
            } else if (type == 5) {
                this.addHyperLogLogNode(dbIndex, key);
            } else if (type == 6) {
                this.addGEONode(dbIndex, key);
            } else if (type == 7) {
                this.addStreamNode(dbIndex, key);
            } else if (type == 8) {
                this.addBitNode(dbIndex, key);
            }
            // 设置ttl
            if (ttl != -1) {
                this.client.expire(dbIndex, key, ttl, null);
            }
            EventUtil.fire(RedisEvents.REDIS_KEY_ADDED, this.treeItem);
            MessageBox.okToast("新增键成功！");
            this.closeStage();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加string键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addStringNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        this.client.set(dbIndex, key, nodeValue);
    }

    /**
     * 添加list键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addListNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        this.client.lpush(dbIndex, key, nodeValue);
    }

    /**
     * 添加set键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addSetNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        this.client.sadd(dbIndex, key, nodeValue);
    }

    /**
     * 添加zset键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addZSetNode(int dbIndex, String key) {
        Number score = this.scoreValue.getValue();
        if (score == null) {
           MessageBox.tipMsg("请填写分数！", this.scoreValue);
            return;
        }
        String nodeValue = this.valueText();
        this.client.zadd(dbIndex, key, score.doubleValue(), nodeValue);
    }

    /**
     * 添加hash键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addHashNode(int dbIndex, String key) {
        String field = this.fieldValue.getText();
        if (field == null) {
           MessageBox.tipMsg("请填写字段名称！", this.fieldValue);
            return;
        }
        String nodeValue = this.valueText();
        this.client.hset(dbIndex, key, field, nodeValue);
    }

    /**
     * 添加hyperLogLog键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addHyperLogLogNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        List<String> elements = nodeValue.lines().collect(Collectors.toList());
        elements = CollUtil.removeBlank(elements);
        if (elements.isEmpty()) {
           MessageBox.tipMsg("元素内容不能为空", this.valueTextArea());
            return;
        }
        this.client.pfadd(dbIndex, key, ArrayUtil.toArray(elements, String.class));
    }

    /**
     * 添加geo键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addGEONode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        if (nodeValue.isEmpty()) {
           MessageBox.tipMsg("元素内容不能为空", this.valueTextArea());
            return;
        }
        Number latitudeValue = this.latitudeValue.getValue();
        if (latitudeValue == null) {
           MessageBox.tipMsg("请输入纬度！", this.latitudeValue);
            return;
        }
        Number longitudeValue = this.longitudeValue.getValue();
        if (longitudeValue == null) {
           MessageBox.tipMsg("请输入经度！", this.longitudeValue);
            return;
        }
        this.client.geoadd(dbIndex, key, longitudeValue.doubleValue(), latitudeValue.doubleValue(), nodeValue);
    }

    /**
     * 添加stream键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addStreamNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        if (nodeValue.isEmpty()) {
           MessageBox.tipMsg("消息内容不能为空", this.valueTextArea());
            return;
        }
        String streamID = this.streamIDValue.getText();
        if (streamID == null) {
           MessageBox.tipMsg("消息id不能为空", this.streamIDValue);
            return;
        }
        JSONObject fields;
        try {
            fields = JSON.parseObject(nodeValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.warn("内容必须为json键值对");
            return;
        }
        if (fields == null || fields.isEmpty()) {
           MessageBox.tipMsg("消息内容不能为空", this.valueTextArea());
            return;
        }
        // 流添加参数
        XAddParams params = new XAddParams();
        params.id(streamID);
        // 添加流
        this.client.xadd(dbIndex, key, (Map) fields.getInnerMap(), params);
    }

    /**
     * 添加bit键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     */
    private void addBitNode(int dbIndex, String key) {
        Number bitIndex = this.bitIndex.getValue();
        // 设置bit值
        this.client.setbit(dbIndex, key, bitIndex.intValue(), this.bitValue.isSelected());
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.valueTextArea().paste();
        this.valueTextArea().requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.valueTextArea().clear();
        this.valueTextArea().requestFocus();
    }

    /**
     * 解析为json
     */
    @FXML
    private void parseToJson() {
        String text = this.valueTextArea().getTextTrim();
        try {
            if ("json".equals(this.valueTextArea().getUserData())) {
                JSONObject json = JSON.parseObject(text, Feature.OrderedField);
                String jsonStr = JSONObject.toJSONString(json);
                this.valueTextArea().setText(jsonStr);
                this.valueTextArea().setUserData("text");
            } else if (text.contains("{") || text.contains("[") || "text".equals(this.valueTextArea().getUserData())) {
                JSONObject json = JSON.parseObject(text, Feature.OrderedField);
                String jsonStr = JSONObject.toJSONString(json, true);
                this.valueTextArea().setText(jsonStr);
                this.valueTextArea().setUserData("json");
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void bindListeners() {
        // 权限变化处理
        this.type.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                this.mutexes.visible(this.stringBox);
            } else if (newValue.intValue() == 1) {
                this.mutexes.visible(this.listBox);
            } else if (newValue.intValue() == 2) {
                this.mutexes.visible(this.setBox);
            } else if (newValue.intValue() == 3) {
                this.mutexes.visible(this.zSetBox);
            } else if (newValue.intValue() == 4) {
                this.mutexes.visible(this.hashBox);
            } else if (newValue.intValue() == 5) {
                this.mutexes.visible(this.hylogBox);
            } else if (newValue.intValue() == 6) {
                this.mutexes.visible(this.geoBox);
            } else if (newValue.intValue() == 7) {
                this.mutexes.visible(this.streamBox);
            } else if (newValue.intValue() == 8) {
                this.mutexes.visible(this.bitBox);
            }
            this.root.parentAutosize();
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.mutexes.manageBindVisible();
        this.mutexes.addNodes(this.bitBox, this.hashBox, this.listBox, this.geoBox, this.setBox, this.zSetBox, this.streamBox, this.stringBox, this.hylogBox);
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.treeItem = this.getStageProp("treeItem");
        this.client = this.treeItem.client();
        this.key.requestFocus();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
    }
}
