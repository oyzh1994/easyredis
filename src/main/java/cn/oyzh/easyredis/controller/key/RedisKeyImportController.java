package cn.oyzh.easyredis.controller.key;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.SystemUtil;
import cn.oyzh.easyredis.dto.RedisNodeExport;
import cn.oyzh.easyredis.exception.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.redis.key.RedisHashValue;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListValue;
import cn.oyzh.easyredis.redis.key.RedisSetValue;
import cn.oyzh.easyredis.redis.key.RedisStreamValue;
import cn.oyzh.easyredis.redis.key.RedisZSetValue;
import cn.oyzh.easyredis.util.RedisExportUtil;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.text.area.MsgTextArea;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.FXRadioButton;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * redis键导入业务
 *
 * @author oyzh
 * @since 2023/07/19
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        value = FXConst.FXML_PATH + "key/redisKeyImport.fxml"
)
@Deprecated
public class RedisKeyImportController extends StageController {

    // /**
    //  * 状态管理器
    //  */
    // @FXML
    // private StateManager stateManager;

    // /**
    //  * redis树键
    //  */
    // private RedisConnectTreeItem treeItem;

    /**
     * redis客户端
     */
    private RedisClient client;

    /**
     * 脚本信息
     */
    @FXML
    private FlexText scriptInfo;

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
    private FXCheckBox retainTTL;

    /**
     * 导入按钮
     */
    @FXML
    private FlexButton importBtn;

    /**
     * 结束导入按钮
     */
    @FXML
    private FlexButton stopImportBtn;

    /**
     * 导入状态
     */
    @FXML
    private FXLabel importStatus;

    /**
     * 导入消息
     */
    @FXML
    private MsgTextArea importMsg;

    /**
     * 导入操作任务
     */
    private Thread execTask;

    /**
     * 导入数据
     */
    private RedisNodeExport nodeExport;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 拖拽文件
     *
     * @param event 事件
     */
    private void dragFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        List<File> files = dragboard.getFiles();
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile());
            return;
        }
        File file = files.getFirst();
        // 解析文件
        this.parseFile(file);
    }

    /**
     * 选择脚本文件
     */
    @FXML
    private void chooseFile() {
        FileExtensionFilter filter1 = new FileExtensionFilter("JSON files|TXT files", "*.json", "*.txt");
        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
        // 解析文件
        this.parseFile(file);
    }

    /**
     * 解析文件
     *
     * @param file 文件
     */
    private void parseFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn(I18nHelper.fileNotExists());
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nHelper.notSupportFolder());
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn(I18nHelper.invalidFormat());
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nHelper.contentIsEmpty());
            return;
        }
        try {
            // 解析数据
            this.nodeExport = RedisExportUtil.fromFile(file);
            // 初始化信息
            this.importMsg.clear();
            this.importBtn.enable();
            // 脚本信息
            String info = I18nHelper.fileName() + " " + file.getName() + "，" +
                    I18nHelper.total() + " " + this.nodeExport.counts() + I18nHelper.line() + "，" +
                    I18nHelper.size() + " " + Math.max(1, file.length() / 1024) + "Kb，" +
                    I18nHelper.version() + " " + this.nodeExport.version() + "，" +
                    I18nHelper.platform() + " " + this.nodeExport.platform() + "，" +
                    I18nHelper.charset() + " " + this.nodeExport.charset();
            this.scriptInfo.setText(info);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.nodeExport = null;
            this.importBtn.disable();
            MessageBox.exception(ex, I18nHelper.parseFail());
        }
    }

    /**
     * 执行导入
     */
    @FXML
    private void doImport() {
        // 重置参数
        this.importMsg.clear();
        this.counter.reset();
        this.counter.setSum(this.nodeExport.counts());
        // 开始处理
        // this.importBtn.disable();
        // this.stateManager.disable();
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.importProcessing() + "===");
        // 执行导入
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.stopImportBtn.enable();
                for (Map<String, Object> node : this.nodeExport.getNodes()) {
                    // 取消操作
                    if (ThreadUtil.isInterrupted(this.execTask)) {
                        JulLog.warn("import canceled!");
                        break;
                    }
                    // 获取数据
                    Long ttl = (Long) node.get("ttl");
                    String key = (String) node.get("key");
                    String type = (String) node.get("type");
                    String value = (String) node.get("value");
                    Integer dbIndex = (Integer) node.get("dbIndex");
                    RedisKeyType keyType = RedisKeyType.valueOfType(type);
                    // 状态
                    int status = 1;
                    // 异常
                    Exception exception = null;
                    try {
                        // 设置数据
                        if (this.client.exists(dbIndex, key)) {
                            status = this.handleExist(key, dbIndex, keyType, value, ttl);
                        } else {// 创建键
                            this.createNode(key, dbIndex, keyType, value, ttl);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        status = 0;
                        exception = ex;
                    }
                    this.updateStatus(key, dbIndex, status, exception);
                }
                // 收尾工作
                this.updateStatus(I18nHelper.operationFinish());
                MessageBox.okToast(I18nHelper.operationFinish());
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus(I18nHelper.operationCancel());
                    MessageBox.okToast(I18nHelper.operationCancel());
                } else {
                    e.printStackTrace();
                    this.updateStatus(I18nHelper.operationFail());
                    MessageBox.warn(I18nHelper.operationFail());
                }
            } finally {
                // 结束处理
                // this.importBtn.enable();
                // this.stateManager.enable();
                NodeGroupUtil.enable(this.stage, "exec");
                this.stopImportBtn.disable();
                this.stage.restoreTitle();
                SystemUtil.gcLater();
            }
        });
    }

    /**
     * 处理键存在时业务
     *
     * @param key     键
     * @param dbIndex db索引
     * @param type    类型
     * @param value   值
     * @param ttl     到期时间
     * @return 处理方式
     */
    private int handleExist(String key, int dbIndex, RedisKeyType type, String value, Long ttl) {
        // 跳过
        if (this.skipForExist.isSelected()) {
            return 2;
        }
        // 覆盖
        if (this.overrideForExist.isSelected()) {
            this.client.del(dbIndex, key);
            this.createNode(key, dbIndex, type, value, ttl);
            return 4;
        }
        // 更新
        if (this.updateForExist.isSelected()) {
            RedisKeyType keyType = RedisKeyUtil.keyType(dbIndex, key, this.client);
            if (keyType != type) {
                return 5;
            }
            this.createNode(key, dbIndex, type, value, ttl);
            return 3;
        }
        return 0;
    }

    /**
     * 创建键
     *
     * @param key     键
     * @param dbIndex db索引
     * @param type    类型
     * @param value   值
     * @param ttl     到期时间
     */
    private void createNode(String key, int dbIndex, RedisKeyType type, String value, Long ttl) {
        RedisKey redisKey = RedisKeyUtil.deserializeNode(type, value);
        if (redisKey == null) {
            JulLog.warn("redisKey is null");
            return;
        }
        if (redisKey.isStringKey()) {
            this.client.set(dbIndex, key, (String) redisKey.asStringValue().getValue());
        } else if (redisKey.isListKey()) {
            List<RedisListValue.RedisListRow> rows=  redisKey.asListValue().getValue();
            String[] arr;
            if (CollectionUtil.isEmpty(rows)) {
                arr = new String[]{""};
            } else {
                List<String> strings = rows.parallelStream().map(RedisListValue.RedisListRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(strings, String.class);
            }
            this.client.lpush(dbIndex, key, arr);
        } else if (redisKey.isSetKey()) {
            List<RedisSetValue.RedisSetRow> rows=  redisKey.asSetValue().getValue();
            String[] arr;
            if (CollectionUtil.isEmpty(rows)) {
                arr = new String[]{""};
            } else {
                List<String> strings = rows.parallelStream().map(RedisSetValue.RedisSetRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(strings, String.class);
            }
            this.client.sadd(dbIndex, key, arr);
        } else if (redisKey.isZSetKey()) {
            List<RedisZSetValue.RedisZSetRow> rows=  redisKey.asZSetValue().getValue();
            Map<String, Double> scoreMembers;
            if (CollectionUtil.isEmpty(rows)) {
                scoreMembers = new HashMap<>();
            } else {
                scoreMembers = new HashMap<>();
                for (RedisZSetValue.RedisZSetRow row : rows) {
                    scoreMembers.put(row.getValue(), row.getScore());
                }
            }
            this.client.zadd(dbIndex, key, scoreMembers);
        } else if (redisKey.isHashKey()) {
            List<RedisHashValue.RedisHashRow> rows=  redisKey.asHashValue().getValue();
            Map<String, String> hash;
            if (CollectionUtil.isEmpty(rows)) {
                hash = new HashMap<>();
            } else {
                hash = new HashMap<>();
                for (RedisHashValue.RedisHashRow row : rows) {
                    hash.put(row.getField(), row.getValue());
                }
            }
            this.client.hmset(dbIndex, key, hash);
        } else if (redisKey.isStreamKey()) {
            List<RedisStreamValue.RedisStreamRow> rows=  redisKey.asStreamValue().getValue();
            if (CollectionUtil.isNotEmpty(rows)) {
                for (RedisStreamValue.RedisStreamRow row : rows) {
                    this.client.xadd(dbIndex, key, row.getStreamId(), row.getFields());
                }
            }
        }
        // 处理ttl
        if (ttl != null && this.retainTTL.isSelected()) {
            // 持久化
            if (ttl == -1) {
                this.client.persist(dbIndex, key);
            } else {// 设置ttl
                this.client.expire(dbIndex, key, ttl, null);
            }
        }
    }

    /**
     * 结束导入
     */
    @FXML
    private void stopImport() {
        ThreadUtil.interrupt(this.execTask);
        this.execTask = null;
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        // this.treeItem = this.getWindowProp("treeItem");
        // this.client = treeItem.client();
        this.client = this.getWindowProp("client");
        this.scriptInfo.managedProperty().bind(this.scriptInfo.visibleProperty());
        this.scriptInfo.addTextChangeListener((observableValue, s, t1) -> this.scriptInfo.setVisible(StringUtil.isNotBlank(t1)));
        this.stage.hideOnEscape();
        // 文件拖拽相关
        this.stage.scene().setOnDragOver(event1 -> {
            this.stage.disable();
            this.stage.appendTitle("===" + I18nHelper.dragTip1() + "===");
            event1.acceptTransferModes(TransferMode.ANY);
            event1.consume();
        });
        this.stage.scene().setOnDragExited(event1 -> {
            this.stage.enable();
            this.stage.restoreTitle();
            event1.consume();
        });
        this.stage.scene().setOnDragDropped(event1 -> {
            this.dragFile(event1);
            event1.setDropCompleted(true);
            event1.consume();
        });
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        this.stopImport();
    }

    /**
     * 更新状态
     *
     * @param key     键
     * @param dbIndex db索引
     * @param status  状态 0:失败 1:成功 2:跳过 3:更新 4:覆盖 5:键类型不一致
     * @param ex      异常
     */
    private void updateStatus(String key, int dbIndex, int status, Exception ex) {
        String msg;
        if (status == 1) {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + I18nHelper.success();
            this.counter.updateSuccess();
        } else if (status == 2) {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + RedisI18nHelper.importTip1();
            this.counter.updateIgnore();
        } else if (status == 3) {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + I18nHelper.success() + "，" + RedisI18nHelper.importTip2();
            this.counter.updateSuccess();
        } else if (status == 4) {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + I18nHelper.success() + "，" + RedisI18nHelper.importTip3();
            this.counter.updateSuccess();
        } else if (status == 5) {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + I18nHelper.fail() + "，" + RedisI18nHelper.importTip4();
            this.counter.updateFail();
        } else {
            msg = I18nHelper.importKey() + "：" + key + " [db" + dbIndex + "] " + I18nHelper.fail();
            if (ex != null) {
                msg += "，" + I18nHelper.errorInfo() + "：" + RedisExceptionParser.INSTANCE.apply(ex);
            }
            this.counter.updateSuccess();
        }
        this.importMsg.appendLine(msg);
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
        FXUtil.runLater(() -> this.importStatus.setText(this.counter.knownFormat()));
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.import");
    }
}
