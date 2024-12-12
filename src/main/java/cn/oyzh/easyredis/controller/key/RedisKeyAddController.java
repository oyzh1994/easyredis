package cn.oyzh.easyredis.controller.key;

import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisKeyTypeComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
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
@StageAttribute(
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyAdd.fxml"
)
public class RedisKeyAddController extends StageController {

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
     * coordinate组件
     */
    @FXML
    private FlexVBox coordinateBox;

    /**
     * 字段名
     */
    @FXML
    private FlexTextArea fieldValue;

    /**
     * bit值
     */
    @FXML
    private FXToggleSwitch bitValue;

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
    private RedisKeyTypeComboBox type;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * 树键
     */
    private RedisDatabaseTreeItem dbItem;

    /**
     * 节点互斥组件
     */
    private final NodeMutexes mutexes = new NodeMutexes();

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
        } else if (this.coordinateBox.isVisible()) {
            textArea = (FlexTextArea) this.coordinateBox.lookup("FlexTextArea");
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
    private void addKey() {
        // 获取键值
        int type = this.type.getSelectedIndex();
        String key = this.key.getTextTrim();
        int dbIndex = this.dbItem.dbIndex();
        try {
            long ttl = this.ttlValue.getValue();
            if (ttl == 0) {
                MessageBox.warn(RedisI18nHelper.addTip2());
                return;
            }
            if (key.isEmpty()) {
                MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.key);
                return;
            }
            if (this.client.exists(dbIndex, key)) {
                MessageBox.warn("key: [" + key + "] " + I18nHelper.alreadyExists());
                return;
            }
            boolean result = false;
            String keyType = "";
            if (type == 0) {
                result = this.addStringNode(dbIndex, key);
                keyType = "STRING";
            } else if (type == 1) {
                result = this.addSetNode(dbIndex, key);
                keyType = "SET";
            } else if (type == 2) {
                result = this.addZSetNode(dbIndex, key);
                keyType = "ZSET";
            } else if (type == 3) {
                result = this.addListNode(dbIndex, key);
                keyType = "LIST";
            } else if (type == 4) {
                result = this.addHashNode(dbIndex, key);
                keyType = "HASH";
            } else if (type == 5) {
                result = this.addStreamNode(dbIndex, key);
                keyType = "STREAM";
            } else if (type == 6) {
                result = this.addHyLogNode(dbIndex, key);
                keyType = "HYPERLOGLOG/STRING";
            } else if (type == 7) {
                result = this.addGEONode(dbIndex, key);
                keyType = "GEO/ZSET";
            } else if (type == 8) {
                result = this.addBitNode(dbIndex, key);
                keyType = "BITMAP/STRING";
            }
            if (!result) {
                return;
            }
            // 设置ttl
            if (ttl != -1) {
                this.client.expire(dbIndex, key, ttl, null);
            }
            RedisEventUtil.keyAdded(this.dbItem.info(), keyType, key, this.dbItem.dbIndex());
            MessageBox.okToast(I18nHelper.operationSuccess());
            this.closeWindow();
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
     * @return 结果
     */
    private boolean addStringNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        return this.client.set(dbIndex, key, nodeValue) != null;
    }

    /**
     * 添加list键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addListNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        return this.client.lpush(dbIndex, key, nodeValue) > 0;
    }

    /**
     * 添加set键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addSetNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        return this.client.sadd(dbIndex, key, nodeValue) > 0;
    }

    /**
     * 添加zset键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addZSetNode(int dbIndex, String key) {
        Number score = this.scoreValue.getValue();
        if (score == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.scoreValue);
            return false;
        }
        String nodeValue = this.valueText();
        return this.client.zadd(dbIndex, key, score.doubleValue(), nodeValue) > 0;
    }

    /**
     * 添加hash键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addHashNode(int dbIndex, String key) {
        String field = this.fieldValue.getText();
        if (field == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.fieldValue);
            return false;
        }
        String nodeValue = this.valueText();
        return this.client.hset(dbIndex, key, field, nodeValue) > 0;
    }

    /**
     * 添加hyperLogLog键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addHyLogNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        List<String> elements = nodeValue.lines().collect(Collectors.toList());
        elements = CollectionUtil.removeBlank(elements);
        if (elements.isEmpty()) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.valueTextArea());
            return false;
        }
        return this.client.pfadd(dbIndex, key, ArrayUtil.toArray(elements, String.class)) > 0;
    }

    /**
     * 添加geo键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addGEONode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        if (nodeValue.isEmpty()) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.valueTextArea());
            return false;
        }
        Number latitudeValue = this.latitudeValue.getValue();
        if (latitudeValue == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.latitudeValue);
            return false;
        }
        Number longitudeValue = this.longitudeValue.getValue();
        if (longitudeValue == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.longitudeValue);
            return false;
        }
        return this.client.geoadd(dbIndex, key, longitudeValue.doubleValue(), latitudeValue.doubleValue(), nodeValue) > 0;
    }

    /**
     * 添加stream键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addStreamNode(int dbIndex, String key) {
        String nodeValue = this.valueText();
        // 行数据
        if (nodeValue.isEmpty()) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.valueTextArea());
            return false;
        }
        if (!JSONUtil.isJson(nodeValue)) {
            MessageBox.warn(RedisI18nHelper.addTip1());
            return false;
        }
        String streamID = this.streamIDValue.getText();
        if (streamID == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.streamIDValue);
            return false;
        }
        JSONObject object = JSONUtil.parseObject(nodeValue);
        if (object.isEmpty()) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.valueTextArea());
            return false;
        }
        // 流添加参数
        XAddParams params = new XAddParams();
        params.id(streamID);
        // 添加流
        return this.client.xadd(dbIndex, key, (Map) object, params) != null;
    }

    /**
     * 添加bit键
     *
     * @param dbIndex 数据库索引
     * @param key     键名称
     * @return 结果
     */
    private boolean addBitNode(int dbIndex, String key) {
        Number bitIndex = this.bitIndex.getValue();
        // 设置bit值
        return this.client.setbit(dbIndex, key, bitIndex.intValue(), this.bitValue.isSelected());
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
                String jsonStr = JSONUtil.toJson(text);
                this.valueTextArea().setText(jsonStr);
                this.valueTextArea().setUserData("text");
            } else if (text.contains("{") || text.contains("[") || "text".equals(this.valueTextArea().getUserData())) {
                String jsonStr = JSONUtil.toPretty(text);
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
                this.mutexes.visible(this.setBox);
            } else if (newValue.intValue() == 2) {
                this.mutexes.visible(this.zSetBox);
            } else if (newValue.intValue() == 3) {
                this.mutexes.visible(this.listBox);
            } else if (newValue.intValue() == 4) {
                this.mutexes.visible(this.hashBox);
            } else if (newValue.intValue() == 5) {
                this.mutexes.visible(this.streamBox);
            } else if (newValue.intValue() == 6) {
                this.mutexes.visible(this.hylogBox);
            } else if (newValue.intValue() == 7) {
                this.mutexes.visible(this.coordinateBox);
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
        this.mutexes.addNodes(this.bitBox, this.hashBox, this.listBox, this.coordinateBox, this.setBox, this.zSetBox, this.streamBox, this.stringBox, this.hylogBox);
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.dbItem = this.getWindowProp("dbItem");
        RedisKeyType type = this.getWindowProp("type");
        this.type.select(type);
        this.client = this.dbItem.client();
        this.key.requestFocus();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("redis.title.key.add");
    }
}
