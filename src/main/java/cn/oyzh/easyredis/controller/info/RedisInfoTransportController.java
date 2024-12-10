// package cn.oyzh.easyredis.controller.info;
//
// import cn.oyzh.common.thread.ThreadUtil;
// import cn.oyzh.common.util.CollectionUtil;
// import cn.oyzh.common.util.SystemUtil;
// import cn.oyzh.easyredis.RedisConst;
// import cn.oyzh.easyredis.domain.RedisConnect;
// import cn.oyzh.easyredis.domain.RedisFilter;
// import cn.oyzh.easyredis.exception.RedisExceptionParser;
// import cn.oyzh.easyredis.fx.RedisConnectComboBox;
// import cn.oyzh.easyredis.fx.RedisDatabaseComboBox;
// import cn.oyzh.easyredis.redis.RedisClient;
// import cn.oyzh.easyredis.redis.RedisKeyType;
// import cn.oyzh.easyredis.redis.key.RedisKey;
// import cn.oyzh.easyredis.store.RedisFilterJdbcStore;
// import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
// import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
// import cn.oyzh.easyredis.util.RedisConnectUtil;
// import cn.oyzh.easyredis.util.RedisI18nHelper;
// import cn.oyzh.easyredis.util.RedisKeyUtil;
// import cn.oyzh.fx.gui.text.field.ClearableTextField;
// import cn.oyzh.fx.plus.controller.StageController;
// import cn.oyzh.fx.plus.controls.box.FlexHBox;
// import cn.oyzh.fx.plus.controls.button.FXRadioButton;
// import cn.oyzh.fx.plus.controls.button.FlexButton;
// import cn.oyzh.fx.plus.controls.button.FXCheckBox;
// import cn.oyzh.fx.plus.controls.label.FXLabel;
// import cn.oyzh.fx.gui.text.area.MsgTextArea;
// import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
// import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
// import cn.oyzh.fx.plus.information.MessageBox;
// import cn.oyzh.fx.plus.node.NodeGroupUtil;
// import cn.oyzh.fx.plus.util.Counter;
// import cn.oyzh.fx.plus.util.FXUtil;
// import cn.oyzh.fx.plus.window.StageAttribute;
// import cn.oyzh.i18n.I18nHelper;
// import javafx.fxml.FXML;
// import javafx.scene.control.TreeItem;
// import javafx.stage.Modality;
// import javafx.stage.WindowEvent;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Set;
//
//
// /**
//  * redis数据传输业务
//  *
//  * @author oyzh
//  * @since 2023/07/20
//  */
// @StageAttribute(
//         iconUrls = RedisConst.ICON_PATH,
//         modality = Modality.WINDOW_MODAL,
//         value = RedisConst.FXML_BASE_PATH + "info/redisInfoTransport.fxml"
// )
// public class RedisInfoTransportController extends StageController {
//
//     // /**
//     //  * 状态管理器
//     //  */
//     // @FXML
//     // private StateManager stateManager;
//
//     /**
//      * 存在时跳过
//      */
//     @FXML
//     private FXRadioButton skipForExist;
//
//     /**
//      * 存在时更新
//      */
//     @FXML
//     private FXRadioButton updateForExist;
//
//     /**
//      * 存在时覆盖
//      */
//     @FXML
//     private FXRadioButton overrideForExist;
//
//     /**
//      * 保留ttl
//      */
//     @FXML
//     private FXCheckBox retainTTL;
//
//     /**
//      * 适用过滤配置
//      */
//     @FXML
//     private FXCheckBox applyFilter;
//
//     /**
//      * 传输按钮
//      */
//     @FXML
//     private FlexButton transportBtn;
//
//     /**
//      * 结束传输按钮
//      */
//     @FXML
//     private FlexButton stopTransportBtn;
//
//     /**
//      * 传输状态
//      */
//     @FXML
//     private FXLabel transportStatus;
//
//     /**
//      * 传输消息
//      */
//     @FXML
//     private MsgTextArea transportMsg;
//
//     /**
//      * 来源连接
//      */
//     @FXML
//     private RedisConnectComboBox fromConnect;
//
//     /**
//      * 来源信息
//      */
//     private RedisConnect fromInfo;
//
//     /**
//      * 来源数据库
//      */
//     @FXML
//     private RedisDatabaseComboBox fromDB;
//
//     /**
//      * 目标连接
//      */
//     @FXML
//     private RedisConnectComboBox targetConnect;
//
//     /**
//      * 目标数据库
//      */
//     @FXML
//     private RedisDatabaseComboBox targetDB;
//
//     /**
//      * 排除string类型
//      */
//     @FXML
//     private FXCheckBox stringType;
//
//     /**
//      * 排除list类型
//      */
//     @FXML
//     private FXCheckBox listType;
//
//     /**
//      * 排除stream类型
//      */
//     @FXML
//     private FXCheckBox streamType;
//
//     /**
//      * 排除set类型
//      */
//     @FXML
//     private FXCheckBox setType;
//
//     /**
//      * 排除zset类型
//      */
//     @FXML
//     private FXCheckBox zsetType;
//
//     /**
//      * 排除hash类型
//      */
//     @FXML
//     private FXCheckBox hashType;
//
//     /**
//      * 当前传输redis对象
//      */
//     private RedisClient fromClient;
//
//     /**
//      * 传输类型
//      * 1. 任意
//      * 2. 连接
//      * 3. 库
//      */
//     private int transportType;
//
//     /**
//      * 当前目标redis客户端
//      */
//     private RedisClient targetClient;
//
//     /**
//      * 当前目标redis对象
//      */
//     private RedisConnect targetInfo;
//
//     /**
//      * 传输操作任务
//      */
//     private Thread exportTask;
//
//     /**
//      * 过滤内容列表
//      */
//     private List<RedisFilter> filters;
//
//     /**
//      * 计数器
//      */
//     private final Counter counter = new Counter();
//
//     /**
//      * 键模式
//      */
//     @FXML
//     private ClearableTextField pattern;
//
//     /**
//      * 消息组件
//      */
//     @FXML
//     private FlexHBox msgBox;
//
//     /**
//      * 键相关组件1
//      */
//     @FXML
//     private FlexHBox keysBox1;
//
//     /**
//      * 键相关组件2
//      */
//     @FXML
//     private FlexHBox keysBox2;
//
//     /**
//      * 键过滤
//      */
//     @FXML
//     private FXCheckBox filterKeys;
//
//     /**
//      * 当前db键列表
//      */
//     private Set<String> allKeys;
//
//     /**
//      * 受影响的键
//      */
//     @FXML
//     private ReadOnlyTextArea keys;
//
//     /**
//      * 过滤配置储存
//      */
//     private final RedisFilterJdbcStore filterStore = RedisFilterJdbcStore.INSTANCE;
//
//     /**
//      * 执行传输
//      */
//     @FXML
//     private void doTransport() {
//         // 检查连接
//         if (this.fromConnect.getValue() == null) {
//             MessageBox.tipMsg(RedisI18nHelper.transportTip7(), this.fromConnect);
//             return;
//         }
//         if (this.targetConnect.getValue() == null) {
//             MessageBox.tipMsg(RedisI18nHelper.transportTip8(), this.targetConnect);
//             return;
//         }
//         RedisConnect fromInfo = this.fromConnect.getValue();
//         RedisConnect targetInfo = this.targetConnect.getValue();
//         int fIndex = this.fromDB.getDB();
//         int tIndex = this.targetDB.getDB();
//         if (fromInfo == targetInfo && fIndex == tIndex) {
//             MessageBox.tipMsg(RedisI18nHelper.transportTip9(), this.fromConnect);
//             return;
//         }
//         // 开始传输
//         this.transportStart();
//         // 检查传输连接
//         if (this.fromClient == null || !this.fromClient.isConnected()) {
//             try {
//                 this.fromConnect.requestFocus();
//                 MessageBox.warn(I18nHelper.sourceConnect() + "[" + fromInfo.getName() + "]" + I18nHelper.initFail());
//                 return;
//             } finally {
//                 this.transportEnd();
//             }
//         }
//
//         // 检查目标连接
//         if (this.targetClient == null || !this.targetClient.isConnected()) {
//             try {
//                 this.targetConnect.requestFocus();
//                 MessageBox.warn(I18nHelper.targetConnect() + "[" + targetInfo.getName() + "]" + I18nHelper.initFail());
//                 return;
//             } finally {
//                 this.transportEnd();
//             }
//         }
//
//         // 重置参数
//         this.counter.reset();
//         // 开始传输
//         this.transportStart();
//         this.stage.appendTitle("===" + I18nHelper.transportIng() + "===");
//         // 执行传输
//         this.exportTask = ThreadUtil.start(() -> {
//             this.stopTransportBtn.enable();
//             try {
//                 // 适用过滤
//                 if (this.applyFilter.isSelected()) {
//                     this.filters = this.filterStore.loadEnable();
//                 }
//                 // 查询键列表
//                 if (CollectionUtil.isEmpty(this.allKeys)) {
//                     this.allKeys = this.fromClient.allKeys(fIndex, this.pattern.getText());
//                 }
//                 // 执行传输
//                 this.transport(fIndex, tIndex, this.allKeys);
//                 this.updateStatus(I18nHelper.transportFinish());
//                 MessageBox.okToast(I18nHelper.operationSuccess());
//             } catch (Exception e) {
//                 if (e.getClass().isAssignableFrom(InterruptedException.class)) {
//                     this.updateStatus(I18nHelper.transportCancel());
//                     MessageBox.okToast(I18nHelper.operationCancel());
//                 } else {
//                     e.printStackTrace();
//                     this.updateStatus(I18nHelper.transportFail());
//                     MessageBox.warn(I18nHelper.operationFail());
//                 }
//             } finally {
//                 // 结束传输
//                 this.transportEnd();
//             }
//         });
//     }
//
//     /**
//      * 传输开始
//      */
//     private void transportStart() {
//         this.fromDB.disable();
//         this.fromConnect.disable();
//         this.targetDB.disable();
//         this.targetConnect.disable();
//         this.transportMsg.clear();
//         // this.transportBtn.disable();
//         // this.stateManager.disable();
//         NodeGroupUtil.disable(this.stage, "exec");
//     }
//
//     /**
//      * 传输结束
//      */
//     private void transportEnd() {
//         if (this.transportType == 1) {
//             this.fromDB.enable();
//             this.fromConnect.enable();
//         } else if (this.transportType == 2) {
//             this.fromDB.enable();
//             this.fromConnect.disable();
//         } else if (this.transportType == 3) {
//             this.fromDB.disable();
//             this.fromConnect.disable();
//         }
//         this.targetDB.enable();
//         this.targetConnect.enable();
//         // this.transportBtn.enable();
//         this.stopTransportBtn.disable();
//         // this.stateManager.enable();
//         NodeGroupUtil.enable(this.stage, "exec");
//         this.stage.restoreTitle();
//         SystemUtil.gcLater();
//     }
//
//     /**
//      * 关闭客户端
//      */
//     private void closeClient() {
//         if (this.transportType == 1 && this.fromClient != null) {
//             RedisConnectUtil.close(this.fromClient, true);
//             this.fromClient = null;
//         }
//         RedisConnectUtil.close(this.targetClient, true);
//         this.targetClient = null;
//     }
//
//     /**
//      * 结束传输
//      */
//     @FXML
//     private void stopTransport() {
//         ThreadUtil.interrupt(this.exportTask);
//         this.exportTask = null;
//     }
//
//     @Override
//     protected void bindListeners() {
//         // this.keysBox1.managedBindVisible();
//         // this.keysBox2.managedBindVisible();
//
//         // 键模式变化
//         this.pattern.addTextChangeListener((observable, oldValue, newValue) -> {
//             this.keys.clear();
//             this.allKeys = null;
//         });
//
//         // 任意
//         if (this.transportType == 1) {
//             this.fromConnect.selectedItemChanged((observableValue, redisInfo, t1) -> {
//                 if (t1 != this.fromInfo) {
//                     this.fromInfo = t1;
//                     RedisConnectUtil.close(this.fromClient, true);
//                 }
//                 this.transportBtn.setDisable(this.fromInfo == null || this.targetInfo == null);
//                 if (t1 != null) {
//                     this.fromClient = new RedisClient(t1);
//                     this.initDBList(this.fromDB, this.fromClient);
//                 }
//                 this.keys.clear();
//                 this.allKeys = null;
//                 this.initKeysBox();
//             });
//         }
//
//         // 任意或者连接
//         if (this.transportType == 1 || this.transportType == 2) {
//             this.fromDB.selectedIndexChanged((observable, oldValue, newValue) -> {
//                 this.keys.clear();
//                 this.allKeys = null;
//                 this.initKeysBox();
//             });
//         }
//
//         // 目标连接变化
//         this.targetConnect.selectedItemChanged((observableValue, info, t1) -> {
//             if (t1 != this.targetInfo) {
//                 this.targetInfo = t1;
//                 RedisConnectUtil.close(this.targetClient, true);
//             }
//             this.transportBtn.setDisable(this.fromInfo == null || this.targetInfo == null);
//             if (t1 != null) {
//                 this.targetClient = new RedisClient(t1);
//                 this.initDBList(this.targetDB, this.targetClient);
//             }
//         });
//
//         // 初始化键过滤组件
//         this.initKeysBox();
//
//         // 过滤键
//         this.filterKeys.selectedChanged((observable, oldValue, newValue) -> {
//             if (newValue) {
//                 this.keysBox1.display();
//                 this.keysBox2.display();
//                 this.msgBox.setFlexHeight("100% - 600");
//             } else {
//                 this.keys.clear();
//                 this.allKeys = null;
//                 this.pattern.setText("*");
//                 this.keysBox1.disappear();
//                 this.keysBox2.disappear();
//                 this.msgBox.setFlexHeight("100% - 400");
//             }
//             this.keysBox1.parentAutosize();
//         });
//     }
//
//     /**
//      * 初始化键过滤组件
//      */
//     private void initKeysBox() {
//         if (this.fromConnect.getValue() != null && this.fromDB.getValue() != null) {
//             this.keysBox1.enable();
//             this.keysBox2.enable();
//         } else {
//             this.keysBox1.disable();
//             this.keysBox2.disable();
//         }
//     }
//
//     /**
//      * 初始化db列表
//      *
//      * @param comboBox 下拉框组件
//      * @param client   redis客户端
//      */
//     private void initDBList(RedisDatabaseComboBox comboBox, RedisClient client) {
//         try {
//             this.stage.disable();
//             this.stage.appendTitle(I18nHelper.connectInitIng());
//             comboBox.clearItems();
//             // 执行连接
//             if (!client.isConnected()) {
//                 client.start();
//             }
//             // 连接成功
//             if (client.isConnected()) {
//                 comboBox.enable();
//                 comboBox.setDbCount(client.databases());
//                 comboBox.selectFirst();
//             } else {// 连接失败
//                 comboBox.disable();
//                 MessageBox.warn(I18nHelper.connectInitFail());
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             MessageBox.warn(I18nHelper.connectInitFail());
//         } finally {
//             this.stage.enable();
//             this.stage.restoreTitle();
//         }
//     }
//
//     @Override
//     public void onStageShown(WindowEvent event) {
//         TreeItem<?> treeItem = this.stage.getProp("treeItem");
//         // db节点
//         if (treeItem instanceof RedisDatabaseTreeItem dbTreeItem) {
//             this.fromClient = dbTreeItem.client();
//             this.fromInfo = this.fromClient.redisInfo();
//             this.fromConnect.select(this.fromInfo);
//             this.fromConnect.disable();
//             this.fromConnect.setUserData(this.fromInfo);
//             this.fromDB.addDB(dbTreeItem.dbIndex());
//             this.fromDB.selectFirst();
//             this.fromDB.disable();
//             this.transportType = 3;
//         } else if (treeItem instanceof RedisConnectTreeItem connectTreeItem) {// 连接节点
//             this.fromClient = connectTreeItem.client();
//             this.fromInfo = this.fromClient.redisInfo();
//             this.fromConnect.select(this.fromInfo);
//             this.fromConnect.disable();
//             this.fromConnect.setUserData(this.fromInfo);
//             this.initDBList(this.fromDB, this.fromClient);
//             this.transportType = 2;
//         } else {
//             this.transportType = 1;
//         }
//         this.stage.hideOnEscape();
//         super.onStageShown(event);
//     }
//
//     @Override
//     public void onWindowHidden(WindowEvent event) {
//         this.transportEnd();
//         this.stopTransport();
//         this.closeClient();
//         this.fromDB.clearItems();
//         this.targetDB.clearItems();
//     }
//
//     /**
//      * 执行传输
//      *
//      * @param fromDBIndex   来源数据库索引
//      * @param targetDBIndex 目标数据库索引
//      * @param keys          键列表
//      */
//     private void transport(int fromDBIndex, int targetDBIndex, Set<String> keys) {
//         for (String key : keys) {
//             try {
//                 // 取消操作
//                 if (ThreadUtil.isInterrupted(this.exportTask)) {
//                     break;
//                 }
//                 // 被过滤
//                 if (this.applyFilter.isSelected() && RedisKeyUtil.isFiltered(key, this.filters)) {
//                     this.updateStatus(key, 2, null);
//                     continue;
//                 }
//                 int status;
//                 // 获取键
//                 RedisKey redisKey = RedisKeyUtil.getKey(fromDBIndex, key, this.retainTTL.isSelected(), true, this.fromClient);
//                 // 获取键失败
//                 if (redisKey == null) {
//                     status = 0;
//                 } else if (this.isExclude(redisKey)) { // 键被排除
//                     status = 3;
//                 } else {
//                     // 键存在时，处理键
//                     if (this.targetClient.exists(targetDBIndex, key)) {
//                         status = this.handleExist(redisKey, targetDBIndex);
//                     } else {// 键不存在，创建键
//                         this.createNode(redisKey, targetDBIndex);
//                         status = 1;
//                     }
//                 }
//                 // 更新状态
//                 this.updateStatus(key, status, null);
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 this.updateStatus(key, 0, ex);
//             }
//         }
//     }
//
//     /**
//      * 处理键存在时业务
//      *
//      * @param node          redis键
//      * @param targetDBIndex 目标数据库索引
//      * @return 处理结果
//      */
//     private int handleExist(RedisKey node, int targetDBIndex) {
//         // 跳过
//         if (this.skipForExist.isSelected()) {
//             return 4;
//         }
//         String key = node.key();
//         RedisKeyType type = node.type();
//         // 覆盖
//         if (this.overrideForExist.isSelected()) {
//             this.targetClient.del(targetDBIndex, key);
//             this.createNode(node, targetDBIndex);
//             return 6;
//         }
//         // 更新
//         if (this.updateForExist.isSelected()) {
//             RedisKeyType keyType = RedisKeyUtil.keyType(targetDBIndex, key, this.targetClient);
//             if (keyType != type) {
//                 return 7;
//             }
//             this.createNode(node, targetDBIndex);
//             return 5;
//         }
//         return 0;
//     }
//
//     /**
//      * 创建键
//      *
//      * @param node          redis键
//      * @param targetDBIndex 目标数据库索引
//      */
//     private void createNode(RedisKey node, int targetDBIndex) {
//         if (node != null) {
//             RedisKeyUtil.createKey(node, targetDBIndex, this.targetClient);
//             String key = node.key();
//             Long ttl = node.ttl();
//             if (ttl != null && this.retainTTL.isSelected()) {
//                 if (ttl >= 0) {
//                     this.targetClient.expire(targetDBIndex, key, ttl, null);
//                 } else if (ttl == -1) {
//                     this.targetClient.persist(targetDBIndex, key);
//                 }
//             }
//         }
//     }
//
//     /**
//      * 是否被排除
//      *
//      * @param node 键
//      * @return 结果
//      */
//     private boolean isExclude(RedisKey node) {
//         if (!this.listType.isSelected() && node.isListKey()) {
//             return true;
//         }
//         if (!this.setType.isSelected() && node.isSetKey()) {
//             return true;
//         }
//         if (!this.zsetType.isSelected() && node.isZSetKey()) {
//             return true;
//         }
//         if (!this.hashType.isSelected() && node.isHashKey()) {
//             return true;
//         }
//         if (!this.streamType.isSelected() && node.isStreamKey()) {
//             return true;
//         }
//         return !this.stringType.isSelected() && node.isStringKey();
//     }
//
//     /**
//      * 更新状态
//      *
//      * @param key    路径
//      * @param status 状态 0:失败 1:成功 2:过滤 3:排除 4:已存在 5:更新 6:覆盖 7:键类型不一致
//      * @param ex     异常信息
//      */
//     private void updateStatus(String key, int status, Exception ex) {
//         String msg;
//         if (status == 1) {
//             msg = I18nHelper.transportKey() + "：" + key + " " + I18nHelper.success();
//             this.counter.updateSuccess();
//         } else if (status == 2) {
//             msg = I18nHelper.transportKey() + "：" + key + " " + RedisI18nHelper.transportTip1();
//             this.counter.updateIgnore();
//         } else if (status == 3) {
//             msg = I18nHelper.transportKey() + "：" + key + " " + RedisI18nHelper.transportTip2();
//             this.counter.updateIgnore();
//         } else if (status == 4) {
//             msg = I18nHelper.transportKey() + "：" + key + " " + RedisI18nHelper.transportTip3();
//             this.counter.updateIgnore();
//         } else if (status == 5) {
//             msg = I18nHelper.transportKey() + "：" + key + I18nHelper.success() + " ，" + RedisI18nHelper.transportTip4();
//             this.counter.updateSuccess();
//         } else if (status == 6) {
//             msg = I18nHelper.transportKey() + "：" + key + I18nHelper.success() + " ，" + RedisI18nHelper.transportTip5();
//             this.counter.updateSuccess();
//         } else if (status == 7) {
//             msg = I18nHelper.transportKey() + "：" + key + I18nHelper.fail() + " ，" + RedisI18nHelper.transportTip6();
//             this.counter.updateFail();
//         } else {
//             msg = I18nHelper.transportKey() + "：" + key + " " + I18nHelper.fail();
//             if (ex != null) {
//                 msg += "，" + I18nHelper.errorInfo() + "：" + RedisExceptionParser.INSTANCE.apply(ex);
//             }
//             this.counter.updateFail();
//         }
//         this.transportMsg.appendLine(msg);
//         this.updateStatus(null);
//     }
//
//     /**
//      * 更新状态
//      *
//      * @param extraMsg 额外信息
//      */
//     private void updateStatus(String extraMsg) {
//         if (extraMsg != null) {
//             this.counter.setExtraMsg(extraMsg);
//         }
//         FXUtil.runLater(() -> this.transportStatus.setText(this.counter.unknownFormat()));
//     }
//
//     /**
//      * 显示受影响的键
//      */
//     @FXML
//     private void showKeys() {
//         this.keys.clear();
//         this.allKeys = this.fromClient.allKeys(this.fromDB.getDB(), this.pattern.getText());
//         if (CollectionUtil.isNotEmpty(this.allKeys)) {
//             List<String> texts = new ArrayList<>(this.allKeys.size());
//             int index = 0;
//             for (String key : this.allKeys) {
//                 texts.add(++index + ". " + key);
//             }
//             this.keys.appendLines(texts);
//         }
//     }
//
//     @Override
//     public String getViewTitle() {
//         return I18nResourceBundle.i18nString("base.title.info.transport");
//     }
// }
