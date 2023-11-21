package cn.oyzh.easyredis.controller.info;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.fx.RedisConnectComboBox;
import cn.oyzh.easyredis.fx.RedisDBComboBox;
import cn.oyzh.easyredis.parser.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.button.FXRadioButton;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupDisable;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * redis数据传输业务
 *
 * @author oyzh
 * @since 2023/07/20
 */
@Slf4j
@StageAttribute(
        title = "Redis数据传输",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "info/redisInfoTransport.fxml"
)
public class InfoTransportController extends Controller {

    /**
     * 存在时跳过
     */
    @FXML
    private FXRadioButton skipForExist;

    /**
     * 存在时更新
     */
    @FXML
    private FXRadioButton updateForExist;

    /**
     * 存在时覆盖
     */
    @FXML
    private FXRadioButton overrideForExist;

    /**
     * 保留ttl
     */
    @FXML
    private FlexCheckBox retainTTL;

    /**
     * 适用过滤配置
     */
    @FXML
    private FlexCheckBox applyFilter;

    /**
     * 传输按钮
     */
    @FXML
    private FlexButton transportBtn;

    /**
     * 结束传输按钮
     */
    @FXML
    private FlexButton stopTransportBtn;

    /**
     * 传输状态
     */
    @FXML
    private FXLabel transportStatus;

    /**
     * 传输消息
     */
    @FXML
    private MsgTextArea transportMsg;

    /**
     * 来源连接
     */
    @FXML
    private RedisConnectComboBox fromConnect;

    /**
     * 来源信息
     */
    private RedisInfo fromInfo;

    /**
     * 来源数据库
     */
    @FXML
    private RedisDBComboBox fromDB;

    /**
     * 目标连接
     */
    @FXML
    private RedisConnectComboBox targetConnect;

    /**
     * 目标数据库
     */
    @FXML
    private RedisDBComboBox targetDB;

    /**
     * 排除string类型
     */
    @FXML
    private FlexCheckBox stringType;

    /**
     * 排除list类型
     */
    @FXML
    private FlexCheckBox listType;

    /**
     * 排除stream类型
     */
    @FXML
    private FlexCheckBox streamType;

    /**
     * 排除set类型
     */
    @FXML
    private FlexCheckBox setType;

    /**
     * 排除zset类型
     */
    @FXML
    private FlexCheckBox zsetType;

    /**
     * 排除hash类型
     */
    @FXML
    private FlexCheckBox hashType;

    /**
     * 排除hyperLogLog类型
     */
    @FXML
    private FlexCheckBox hyperLogLogType;

    /**
     * 当前传输redis对象
     */
    private RedisClient fromClient;

    /**
     * 传输类型
     * 1. 任意
     * 2. 连接
     * 3. 库
     */
    private int transportType;

    /**
     * 当前目标redis客户端
     */
    private RedisClient targetClient;

    /**
     * 当前目标redis对象
     */
    private RedisInfo targetInfo;

    /**
     * 传输操作任务
     */
    private Thread exportTask;

    /**
     * 过滤内容列表
     */
    private List<RedisFilter> filters;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 键模式
     */
    @FXML
    private ClearableTextField pattern;

    /**
     * 消息组件
     */
    @FXML
    private FlexHBox msgBox;

    /**
     * 键相关组件1
     */
    @FXML
    private FlexHBox keysBox1;

    /**
     * 键相关组件2
     */
    @FXML
    private FlexHBox keysBox2;

    /**
     * 键过滤
     */
    @FXML
    private FlexCheckBox filterKeys;

    /**
     * 当前db键列表
     */
    private Set<String> allKeys;

    /**
     * 受影响的键
     */
    @FXML
    private ReadOnlyTextArea keys;

    /**
     * 过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 节点分组禁用组件
     */
    private final NodeGroupDisable groupDisabled = new NodeGroupDisable();

    /**
     * 执行传输
     */
    @FXML
    private void doTransport() {
        // 检查连接
        if (this.fromConnect.getValue() == null) {
           MessageBox.tipMsg("请选择一个传输连接", this.fromConnect);
            return;
        }
        if (this.targetConnect.getValue() == null) {
           MessageBox.tipMsg("请选择一个目标连接", this.fromConnect);
            return;
        }
        RedisInfo fromInfo = this.fromConnect.getValue();
        RedisInfo targetInfo = this.targetConnect.getValue();
        int fIndex = this.fromDB.getDB();
        int tIndex = this.targetDB.getDB();
        if (fromInfo == targetInfo && fIndex == tIndex) {
           MessageBox.tipMsg("传输目标不能是自己", this.fromConnect);
            return;
        }

        // 开始传输
        this.transportStart();
        // 检查传输连接
        if (this.fromClient == null || !this.fromClient.isConnected()) {
            try {
                this.fromConnect.requestFocus();
                MessageBox.warn("传输连接[" + fromInfo.getName() + "]初始化失败！");
                return;
            } finally {
                this.transportEnd();
            }
        }

        // 检查目标连接
        if (this.targetClient == null || !this.targetClient.isConnected()) {
            try {
                this.targetConnect.requestFocus();
                MessageBox.warn("目标连接[" + targetInfo.getName() + "]初始化失败！");
                return;
            } finally {
                this.transportEnd();
            }
        }

        // 重置参数
        this.counter.reset();
        // 开始传输
        this.transportStart();
        this.stage.appendTitle("===传输执行中===");
        // 执行传输
        this.exportTask = ThreadUtil.start(() -> {
            this.stopTransportBtn.enable();
            try {
                // 适用过滤
                if (this.applyFilter.isSelected()) {
                    this.filters = this.filterStore.loadEnable();
                }
                // 查询键列表
                if (CollUtil.isEmpty(this.allKeys)) {
                    this.allKeys = this.fromClient.allKeys(fIndex, this.pattern.getText());
                }
                // 执行传输
                this.transport(fIndex, tIndex, this.allKeys);
                this.updateStatus("数据传输收尾中....");
                this.transportMsg.waitTextExpend();
                this.updateStatus("数据传输结束");
                MessageBox.okToast("传输数据结束！");
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据传输取消");
                    MessageBox.okToast("传输数据取消！");
                } else {
                    e.printStackTrace();
                    this.updateStatus("数据传输失败");
                    MessageBox.warn("传输数据失败！");
                }
            } finally {
                // 结束传输
                this.transportEnd();
            }
        });
    }

    /**
     * 传输开始
     */
    private void transportStart() {
        this.fromDB.disable();
        this.fromConnect.disable();
        this.transportMsg.clear();
        this.groupDisabled.disable();
    }

    /**
     * 传输结束
     */
    private void transportEnd() {
        if (this.transportType == 1) {
            this.fromDB.enable();
            this.fromConnect.enable();
        } else if (this.transportType == 2) {
            this.fromDB.enable();
            this.fromConnect.disable();
        } else if (this.transportType == 3) {
            this.fromDB.disable();
            this.fromConnect.disable();
        }
        this.groupDisabled.enable();
        this.stopTransportBtn.disable();
        this.stage.restoreTitle();
        SystemUtil.gcLater();
    }

    /**
     * 关闭客户端
     */
    private void closeClient() {
        if (this.transportType == 1 && this.fromClient != null) {
            RedisConnectUtil.close(this.fromClient, true);
            this.fromClient = null;
        }
        RedisConnectUtil.close(this.targetClient, true);
        this.targetClient = null;
    }

    /**
     * 结束传输
     */
    @FXML
    private void stopTransport() {
        ThreadUtil.interrupt(this.exportTask);
        this.exportTask = null;
    }

    @Override
    protected void bindListeners() {
        this.keysBox1.managedBindVisible();
        this.keysBox2.managedBindVisible();

        // 键模式变化
        this.pattern.addTextChangeListener((observable, oldValue, newValue) -> {
            this.keys.clear();
            this.allKeys = null;
        });

        // 任意
        if (this.transportType == 1) {
            this.fromConnect.selectedItemChanged((observableValue, redisInfo, t1) -> {
                if (t1 != this.fromInfo) {
                    this.fromInfo = t1;
                    RedisConnectUtil.close(this.fromClient, true);
                }
                this.transportBtn.setDisable(this.fromInfo == null || this.targetInfo == null);
                if (t1 != null) {
                    this.fromClient = new RedisClient(t1);
                    this.initDBList(this.fromDB, this.fromClient);
                }
                this.keys.clear();
                this.allKeys = null;
                this.initKeysBox();
            });
        }

        // 任意或者连接
        if (this.transportType == 1 || this.transportType == 2) {
            this.fromDB.selectedIndexChanged((observable, oldValue, newValue) -> {
                this.keys.clear();
                this.allKeys = null;
                this.initKeysBox();
            });
        }

        // 目标连接变化
        this.targetConnect.selectedItemChanged((observableValue, info, t1) -> {
            if (t1 != this.targetInfo) {
                this.targetInfo = t1;
                RedisConnectUtil.close(this.targetClient, true);
            }
            this.transportBtn.setDisable(this.fromInfo == null || this.targetInfo == null);
            if (t1 != null) {
                this.targetClient = new RedisClient(t1);
                this.initDBList(this.targetDB, this.targetClient);
            }
        });

        // 初始化键过滤组件
        this.initKeysBox();

        // 过滤键
        this.filterKeys.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.keysBox1.display();
                this.keysBox2.display();
                this.msgBox.setFlexHeight("100% - 600");
            } else {
                this.keys.clear();
                this.allKeys = null;
                this.pattern.setText("*");
                this.keysBox1.disappear();
                this.keysBox2.disappear();
                this.msgBox.setFlexHeight("100% - 400");
            }
            this.keysBox1.parentAutosize();
        });
    }

    /**
     * 初始化键过滤组件
     */
    private void initKeysBox() {
        if (this.fromConnect.getValue() != null && this.fromDB.getValue() != null) {
            this.keysBox1.enable();
            this.keysBox2.enable();
        } else {
            this.keysBox1.disable();
            this.keysBox2.disable();
        }
    }

    /**
     * 初始化db列表
     *
     * @param comboBox 下拉框组件
     * @param client   redis客户端
     */
    private void initDBList(RedisDBComboBox comboBox, RedisClient client) {
        try {
            this.stage.disable();
            this.stage.appendTitle("连接初始化中...");
            comboBox.clearItems();
            // 执行连接
            if (!client.isConnected()) {
                client.start();
            }
            // 连接成功
            if (client.isConnected()) {
                comboBox.enable();
                comboBox.setDbCount(client.databases());
                comboBox.selectFirst();
            } else {// 连接失败
                comboBox.disable();
                MessageBox.warn("连接初始化失败！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.warn("连接初始化失败！");
        } finally {
            this.stage.enable();
            this.stage.restoreTitle();
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        // 分组禁用
        this.groupDisabled.addNodes(this.setType, this.listType, this.zsetType, this.streamType, this.stringType,
                this.hashType, this.hyperLogLogType, this.applyFilter, this.targetConnect, this.targetDB,
                this.retainTTL, this.skipForExist, this.updateForExist, this.overrideForExist, this.transportBtn,
                this.filterKeys, this.keysBox1, this.keysBox2);
        TreeItem<?> treeItem = this.stage.getProp("treeItem");
        // db节点
        if (treeItem instanceof RedisDBTreeItem dbTreeItem) {
            this.fromClient = dbTreeItem.client();
            this.fromInfo = this.fromClient.redisInfo();
            this.fromConnect.select(this.fromInfo);
            this.fromConnect.disable();
            this.fromConnect.setUserData(this.fromInfo);
            this.fromDB.addDB(dbTreeItem.dbIndex());
            this.fromDB.selectFirst();
            this.fromDB.disable();
            this.transportType = 3;
        } else if (treeItem instanceof RedisConnectTreeItem connectTreeItem) {// 连接节点
            this.fromClient = connectTreeItem.client();
            this.fromInfo = this.fromClient.redisInfo();
            this.fromConnect.select(this.fromInfo);
            this.fromConnect.disable();
            this.fromConnect.setUserData(this.fromInfo);
            this.initDBList(this.fromDB, this.fromClient);
            this.transportType = 2;
        } else {
            this.transportType = 1;
        }
        this.stage.hideOnEscape();
        super.onStageShown(event);
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        this.transportEnd();
        this.stopTransport();
        this.closeClient();
        this.fromDB.clearItems();
        this.targetDB.clearItems();
    }

    /**
     * 执行传输
     *
     * @param fromDBIndex   来源数据库索引
     * @param targetDBIndex 目标数据库索引
     * @param keys          键列表
     */
    private void transport(int fromDBIndex, int targetDBIndex, Set<String> keys) {
        for (String key : keys) {
            try {
                // 取消操作
                if (ThreadUtil.isInterrupted(this.exportTask)) {
                    break;
                }
                // 被过滤
                if (this.applyFilter.isSelected() && RedisKeyUtil.isFiltered(key, this.filters)) {
                    this.updateStatus(key, 2, null);
                    continue;
                }
                int status;
                RedisKey node = RedisKeyUtil.getNode(fromDBIndex, key, this.retainTTL.isSelected(), this.fromClient);
                // 获取键失败
                if (node == null) {
                    status = 0;
                } else if (this.isExclude(node)) { // 键被排除
                    status = 3;
                } else {
                    // 获取键值
                    RedisKeyUtil.getNodeValue(node, fromDBIndex, key, this.fromClient);
                    // 键存在时，处理键
                    if (this.targetClient.exists(targetDBIndex, key)) {
                        status = this.handleExist(node, targetDBIndex);
                    } else {// 键不存在，创建键
                        this.createNode(node, targetDBIndex);
                        status = 1;
                    }
                }
                // 更新状态
                this.updateStatus(key, status, null);
            } catch (Exception ex) {
                ex.printStackTrace();
                this.updateStatus(key, 0, ex);
            }
        }
    }

    /**
     * 处理键存在时业务
     *
     * @param node          redis键
     * @param targetDBIndex 目标数据库索引
     * @return 处理结果
     */
    private int handleExist(RedisKey node, int targetDBIndex) {
        // 跳过
        if (this.skipForExist.isSelected()) {
            return 4;
        }
        String key = node.key();
        String type = node.type().toString();
        // 覆盖
        if (this.overrideForExist.isSelected()) {
            this.targetClient.del(targetDBIndex, key);
            this.createNode(node, targetDBIndex);
            return 6;
        }
        // 更新
        if (this.updateForExist.isSelected()) {
            String keyType = RedisKeyUtil.getKeyType(targetDBIndex, key, this.targetClient);
            if (!StrUtil.equalsIgnoreCase(keyType, type)) {
                return 7;
            }
            this.createNode(node, targetDBIndex);
            return 5;
        }
        return 0;
    }

    /**
     * 创建键
     *
     * @param node          redis键
     * @param targetDBIndex 目标数据库索引
     */
    private void createNode(RedisKey node, int targetDBIndex) {
        if (node != null) {
            RedisKeyUtil.createNode(node, targetDBIndex, this.targetClient);
            String key = node.key();
            Long ttl = node.ttl();
            if (ttl != null && this.retainTTL.isSelected()) {
                this.targetClient.expire(targetDBIndex, key, ttl, null);
            }
        }
    }

    /**
     * 是否被排除
     *
     * @param node 键
     * @return 结果
     */
    private boolean isExclude(RedisKey node) {
        if (this.listType.isSelected() && node.isListKey()) {
            return true;
        }
        if (this.setType.isSelected() && node.isSetKey()) {
            return true;
        }
        if (this.zsetType.isSelected() && node.isZSetKey()) {
            return true;
        }
        if (this.hashType.isSelected() && node.isHashKey()) {
            return true;
        }
        if (this.hyperLogLogType.isSelected() && node.isHyperLogLogKey()) {
            return true;
        }
        if (this.streamType.isSelected() && node.isStreamKey()) {
            return true;
        }
        return this.stringType.isSelected() && node.isStringKey();
    }

    /**
     * 更新状态
     *
     * @param key    路径
     * @param status 状态 0:失败 1:成功 2:过滤 3:排除 4:已存在 5:更新 6:覆盖 7:键类型不一致
     * @param ex     异常信息
     */
    private void updateStatus(String key, int status, Exception ex) {
        String msg;
        if (status == 1) {
            msg = "传输键：[" + key + "] 成功";
            this.counter.updateSuccess();
        } else if (status == 2) {
            msg = "传输键：[" + key + "] 跳过，此键被过滤";
            this.counter.updateIgnore();
        } else if (status == 3) {
            msg = "传输键：[" + key + "] 跳过，此键被排除";
            this.counter.updateIgnore();
        } else if (status == 4) {
            msg = "传输键：[" + key + "] 跳过，此键已存在";
            this.counter.updateIgnore();
        } else if (status == 5) {
            msg = "传输键：[" + key + "] 成功，此键已更新";
            this.counter.updateSuccess();
        } else if (status == 6) {
            msg = "传输键：[" + key + "] 成功，此键已覆盖";
            this.counter.updateSuccess();
        } else if (status == 7) {
            msg = "传输键：[" + key + "] 失败，此键已存在，且类型不一致";
            this.counter.updateFail();
        } else {
            msg = "传输键：[" + key + "] 失败";
            if (ex != null) {
                msg += "，错误信息：" + RedisExceptionParser.INSTANCE.apply(ex);
            }
            this.counter.updateFail();
        }
        this.transportMsg.appendLine(msg);
        this.updateStatus(null);
    }

    /**
     * 更新状态
     *
     * @param extraMsg 额外信息
     */
    private void updateStatus(String extraMsg) {
        if (extraMsg != null) {
            this.counter.setExtraMsg(extraMsg);
        }
        FXUtil.runLater(() -> this.transportStatus.setText(this.counter.unknownFormat()));
    }

    /**
     * 显示受影响的键
     */
    @FXML
    private void showKeys() {
        this.keys.clear();
        this.allKeys = this.fromClient.allKeys(this.fromDB.getDB(), this.pattern.getText());
        if (CollUtil.isNotEmpty(this.allKeys)) {
            List<String> texts = new ArrayList<>(this.allKeys.size());
            int index = 0;
            for (String key : this.allKeys) {
                texts.add(++index + ". " + key);
            }
            this.keys.appendLines(texts);
        }
    }
}
