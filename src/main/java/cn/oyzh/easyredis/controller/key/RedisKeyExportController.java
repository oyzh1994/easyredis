package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.fx.RedisDBComboBox;
import cn.oyzh.easyredis.parser.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.util.RedisExportUtil;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.FlexTextField;
import cn.oyzh.fx.plus.handler.StateManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXFileChooser;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * redis键导出业务
 *
 * @author oyzh
 * @since 2023/07/07
 */
//@Slf4j
@StageAttribute(
        title = "Redis数据导出",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyExport.fxml"
)
public class RedisKeyExportController extends Controller {

    /**
     * 状态管理器
     */
    @FXML
    private StateManager stateManager;

    /**
     * 服务器
     */
    @FXML
    private FlexTextField serverName;

    /**
     * 数据库
     */
    @FXML
    private RedisDBComboBox db;

    /**
     * 按词典导出
     */
    @FXML
    private FlexCheckBox dictSort;

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
     * 美化选项
     */
    @FXML
    private FlexComboBox<String> pretty;

    // /**
    //  * 导出字符集
    //  */
    // @FXML
    // private CharsetComboBox charset;

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
     * 导出按钮
     */
    @FXML
    private FlexButton exportBtn;

    /**
     * 结束导出按钮
     */
    @FXML
    private FlexButton stopExportBtn;

    /**
     * 导出状态
     */
    @FXML
    private FXLabel exportStatus;

    /**
     * 导出消息
     */
    @FXML
    private MsgTextArea exportMsg;

    /**
     * 键模式
     */
    @FXML
    private ClearableTextField pattern;

    /**
     * 受影响的键
     */
    @FXML
    private ReadOnlyTextArea keys;

    /**
     * redis客户端
     */
    private RedisClient client;

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
     * 导出操作任务
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
     * 过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 当前db键列表
     */
    private Set<String> allKeys;

    /**
     * 全部db键列表
     */
    private Map<Integer, Set<String>> fullKeys;

    /**
     * 执行导出
     */
    @FXML
    private void doExport() {
        // 重置参数
        this.counter.reset();
        this.exportMsg.clear();
        boolean dictSort = this.dictSort.isSelected();
        // 开始处理
        this.exportMsg.clear();
        this.exportBtn.disable();
        this.stateManager.disable();
        if (this.db.hasProp("canDisable")) {
            this.db.disable();
        }
        this.stage.appendTitle("===导出执行中===");
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.filters = this.filterStore.loadEnable();
        }
        // 执行导出
        this.exportTask = ThreadUtil.start(() -> {
            try {
                this.stopExportBtn.enable();
                // 获取键
                List<RedisKey> allNodes = new ArrayList<>();
                // 导出所有
                if (this.db.getDB() == -1) {
                    if (CollUtil.isEmpty(this.fullKeys)) {
                        this.fullKeys = this.client.fullKeys(this.pattern.getText());
                    }
                    for (Map.Entry<Integer, Set<String>> entry : this.fullKeys.entrySet()) {
                        this.doExport(entry.getKey(), entry.getValue(), allNodes);
                    }
                } else {// 导出当前库
                    if (CollUtil.isEmpty(this.allKeys)) {
                        this.allKeys = this.client.allKeys(this.db.getDB(), this.pattern.getText());
                    }
                    this.doExport(this.db.getDB(), this.allKeys, allNodes);
                }
                // 取消操作
                if (ThreadUtil.isInterrupted(this.exportTask)) {
                    StaticLog.warn("export cancel!");
                    return;
                }
                // 键按词典顺序排序
                if (dictSort) {
                    allNodes.sort(RedisKey::compareTo);
                }
                boolean prettyFormat = this.pretty.getSelectedIndex() == 0;
                // 导出内容
                String exportData = RedisExportUtil.nodesToJSON(allNodes, null, prettyFormat);
                // String exportData = RedisExportUtil.nodesToJSON(allNodes, this.charset.getCharset(), prettyFormat);
                // 文件格式
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
                // 处理名称
                String fileName;
                if (StrUtil.equals("所有数据库", this.db.getValue())) {
                    fileName = "Redis连接-" + this.client.infoName() + "-导出数据.json";
                } else {
                    fileName = "Redis连接-" + this.client.infoName() + "-db" + this.db.getValue() + "-导出数据.json";
                }
                // 收尾工作
                this.updateStatus("处理文件中...");
                this.exportMsg.waitTextExpend();
                File file = FXFileChooser.save("Redis数据导出", fileName, new FileChooser.ExtensionFilter[]{extensionFilter});
                // 保存文件
                if (file != null) {
                    FileUtil.writeUtf8String(exportData, file);
                    this.updateStatus("文件保存成功");
                    MessageBox.okToast("导出数据成功！");
                } else {
                    this.updateStatus("文件保存取消");
                }
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据导出取消");
                    MessageBox.okToast("导出数据取消！");
                } else {
                    e.printStackTrace();
                    this.updateStatus("数据导出失败");
                    MessageBox.warn("导出数据失败！");
                }
            } finally {
                // 结束处理
                this.exportBtn.enable();
                this.stateManager.enable();
                this.stopExportBtn.disable();
                if (this.db.hasProp("canDisable")) {
                    this.db.enable();
                }
                this.stage.restoreTitle();
                SystemUtil.gcLater();
            }
        });
    }

    /**
     * 结束导出
     */
    @FXML
    private void stopExport() {
        ThreadUtil.interrupt(this.exportTask);
        this.exportTask = null;
    }

    @Override
    protected void bindListeners() {
        this.pretty.managedBindVisible();
        this.keysBox1.managedBindVisible();
        this.keysBox2.managedBindVisible();

        // db索引变化
        this.db.selectedIndexChanged((observable, oldValue, newValue) -> {
            this.keys.clear();
            this.allKeys = null;
            this.fullKeys = null;
        });

        // 键模式输入变化
        this.pattern.addTextChangeListener((observable, oldValue, newValue) -> {
            this.keys.clear();
            this.allKeys = null;
            this.fullKeys = null;
        });

        // 键过滤选中变化
        this.filterKeys.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.keysBox1.display();
                this.keysBox2.display();
                this.msgBox.setFlexHeight("100% - 600");
            } else {
                this.keys.clear();
                this.allKeys = null;
                this.fullKeys = null;
                this.pattern.setText("*");
                this.keysBox1.disappear();
                this.keysBox2.disappear();
                this.msgBox.setFlexHeight("100% - 400");
            }
            this.keysBox1.parentAutosize();
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        TreeItem<?> treeItem = this.getStageProp("treeItem");
        if (treeItem instanceof RedisConnectTreeItem connectTreeItem) {
            this.client = connectTreeItem.client();
            this.db.addItem("所有数据库");
            this.db.setDbCount(this.client.databases());
            this.db.setProp("canDisable", true);
            this.serverName.setText(connectTreeItem.value().getName());
        } else if (treeItem instanceof RedisDBTreeItem dbTreeItem) {
            this.client = dbTreeItem.client();
            this.db.addDB(dbTreeItem.dbIndex());
            this.db.disable();
            this.db.removeProp("canDisable");
            this.serverName.setText(dbTreeItem.info().getName());
        }
        this.db.selectFirst();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        this.stopExport();
    }

    /**
     * 执行导出
     *
     * @param dbIndex  数据库索引
     * @param keys     键列表
     * @param allNodes 键节点列表
     */
    private void doExport(int dbIndex, Set<String> keys, List<RedisKey> allNodes) {
        for (String key : keys) {
            try {
                // 被过滤
                if (this.applyFilter.isSelected() && RedisKeyUtil.isFiltered(key, this.filters)) {
                    this.updateStatus(dbIndex, key, 2, null);
                    continue;
                }
                RedisKey node = RedisKeyUtil.getNode(dbIndex, key, this.retainTTL.isSelected(), this.client);
                // 失败
                if (node == null) {
                    this.updateStatus(dbIndex, key, 0, null);
                } else if (this.isExclude(node)) { // 被排除
                    this.updateStatus(dbIndex, key, 3, null);
                } else {// 添加到集合
                    RedisKeyUtil.getNodeValue(node, dbIndex, key, this.client);
                    allNodes.add(node);
                    this.updateStatus(dbIndex, key, 1, null);
                }
                // 取消操作
                if (ThreadUtil.isInterrupted(this.exportTask)) {
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                this.updateStatus(dbIndex, key, 0, ex);
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
        if (!this.listType.isSelected() && node.isListKey()) {
            return true;
        }
        if (!this.setType.isSelected() && node.isSetKey()) {
            return true;
        }
        if (!this.zsetType.isSelected() && node.isZSetKey()) {
            return true;
        }
        if (!this.hashType.isSelected() && node.isHashKey()) {
            return true;
        }
        if (!this.hyperLogLogType.isSelected() && node.isHyLogKey()) {
            return true;
        }
        if (!this.streamType.isSelected() && node.isStreamKey()) {
            return true;
        }
        return !this.stringType.isSelected() && node.isStringKey();
    }

    /**
     * 更新状态
     *
     * @param key    路径
     * @param status 状态 0:失败 1:成功 2:过滤 3:排除
     * @param ex     异常信息
     */
    private void updateStatus(int dbIndex, String key, int status, Exception ex) {
        key = URLDecoder.decode(key, StandardCharsets.UTF_8);
        String msg;
        if (status == 1) {
            msg = "导出键：" + key + " [db" + dbIndex + "] 成功";
            this.counter.updateSuccess();
        } else if (status == 2) {
            msg = "导出键：" + key + " [db" + dbIndex + "] 已忽略，此键适用过滤配置";
            this.counter.updateIgnore();
        } else if (status == 3) {
            msg = "导出键：" + key + " [db" + dbIndex + "] 已忽略，此键类型被排除";
            this.counter.updateIgnore();
        } else {
            msg = "导出键：" + key + " [db" + dbIndex + "] 失败";
            if (ex != null) {
                msg += "，错误信息：" + RedisExceptionParser.INSTANCE.apply(ex);
            }
            this.counter.updateFail();
        }
        this.exportMsg.appendLine(msg);
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
        FXUtil.runLater(() -> this.exportStatus.setText(this.counter.unknownFormat()));
    }

    /**
     * 显示受影响的键
     */
    @FXML
    private void showKeys() {
        this.keys.clear();
        if (this.db.getDB() == -1) {
            this.fullKeys = this.client.fullKeys(this.pattern.getText());
            if (CollUtil.isNotEmpty(this.fullKeys)) {
                List<String> texts = new ArrayList<>(this.fullKeys.size());
                int index = 0;
                for (Map.Entry<Integer, Set<String>> entry : this.fullKeys.entrySet()) {
                    texts.add("db" + entry.getKey() + "  ========================>");
                    for (String key : entry.getValue()) {
                        ++index;
                        texts.add("(" + index + " " + key);
                    }
                    texts.add("\n");
                }
                this.keys.appendLines(texts);
            }
        } else {
            this.allKeys = this.client.allKeys(this.db.getDB(), this.pattern.getText());
            if (CollUtil.isNotEmpty(this.allKeys)) {
                List<String> texts = new ArrayList<>(this.allKeys.size());
                int index = 0;
                for (String key : this.allKeys) {
                    ++index;
                    texts.add("(" + index + " " + key);
                }
                this.keys.appendLines(texts);
            }
        }
    }
}
