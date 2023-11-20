package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.dto.RedisNodeExport;
import cn.oyzh.easyredis.trees.RedisConnectTreeItem;
import cn.oyzh.easyredis.parser.RedisExceptionParser;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisEvents;
import cn.oyzh.easyredis.redis.RedisHashKey;
import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.redis.RedisHyperLogLogKey;
import cn.oyzh.easyredis.redis.RedisKey;
import cn.oyzh.easyredis.redis.RedisListKey;
import cn.oyzh.easyredis.redis.RedisListRow;
import cn.oyzh.easyredis.redis.RedisSetKey;
import cn.oyzh.easyredis.redis.RedisSetRow;
import cn.oyzh.easyredis.redis.RedisStreamKey;
import cn.oyzh.easyredis.redis.RedisStreamRow;
import cn.oyzh.easyredis.redis.RedisStringKey;
import cn.oyzh.easyredis.redis.RedisZSetKey;
import cn.oyzh.easyredis.redis.RedisZSetRow;
import cn.oyzh.easyredis.util.RedisExportUtil;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FXRadioButton;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupDisable;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXFileChooser;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@StageAttribute(
        title = "Redis数据导入",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyImport.fxml"
)
public class RedisKeyImportController extends Controller {

    /**
     * redis树键
     */
    private RedisConnectTreeItem treeItem;

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
     * 选择文件
     */
    @FXML
    private FlexButton chooseFile;

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

    // /**
    //  * 导入字符集
    //  */
    // @FXML
    // private CharsetComboBox charset;

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
    private Thread importTask;

    /**
     * 导入数据
     */
    private RedisNodeExport nodeExport;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 节点分组禁用组件
     */
    private final NodeGroupDisable groupDisabled = new NodeGroupDisable();

    /**
     * 拖拽文件
     *
     * @param event 事件
     */
    private void dragFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        List<File> files = dragboard.getFiles();
        if (CollUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn("仅支持单个文件！");
            return;
        }
        File file = files.get(0);
        // 解析文件
        this.parseFile(file);
    }

    /**
     * 选择脚本文件
     */
    @FXML
    private void chooseFile() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files|TXT files", "*.json", "*.txt");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FXFileChooser.choose("选择redis脚本", new FileChooser.ExtensionFilter[]{filter1, filter2});
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
            MessageBox.warn("文件不存在！");
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn("不支持文件夹！");
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "txt", "json")) {
            MessageBox.warn("仅支持txt或json文件！");
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn("文件内容为空！");
            return;
        }
        try {
            // 解析数据
            this.nodeExport = RedisExportUtil.fromFile(file);
            // 初始化信息
            this.importMsg.clear();
            this.importBtn.enable();
            // 脚本信息
            String info = "文件名：" + file.getName() + "，" +
                    "共：" + this.nodeExport.counts() + "行，" +
                    "大小：" + Math.max(1, file.length() / 1024) + "Kb，" +
                    "源版本：" + this.nodeExport.version() + "，" +
                    "源平台：" + this.nodeExport.platform() + "，" +
                    "字符集：" + this.nodeExport.charset();
            this.scriptInfo.setText(info);
            // this.charset.select(this.nodeExport.getCharset());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.nodeExport = null;
            this.importBtn.disable();
            MessageBox.warn("解析脚本失败！");
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
        this.groupDisabled.disable();
        this.stage.appendTitle("===导入执行中===");
        // 执行导入
        this.importTask = ThreadUtil.start(() -> {
            try {
                this.stopImportBtn.enable();
                EventUtil.fire(RedisEvents.REDIS_IMPORT_START);
                for (Map<String, Object> node : this.nodeExport.getNodes()) {
                    // 取消操作
                    if (ThreadUtil.isInterrupted(this.importTask)) {
                        log.warn("import cancel!");
                        break;
                    }
                    // 获取数据
                    Long ttl = (Long) node.get("ttl");
                    String key = (String) node.get("key");
                    String type = (String) node.get("type");
                    String value = (String) node.get("value");
                    Integer dbIndex = (Integer) node.get("dbIndex");
                    // 状态
                    int status = 1;
                    // 异常
                    Exception exception = null;
                    try {
                        // 设置数据
                        if (this.client.exists(dbIndex, key)) {
                            status = this.handleExist(key, dbIndex, type, value, ttl);
                        } else {// 创建键
                            this.createNode(key, dbIndex, type, value, ttl);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        status = 0;
                        exception = ex;
                    }
                    this.updateStatus(key, dbIndex, status, exception);
                }
                // 收尾工作
                this.updateStatus("数据导入收尾中...");
                this.importMsg.waitTextExpend();
                this.updateStatus("数据导入结束");
                MessageBox.okToast("导入数据结束！");
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据导入取消");
                    MessageBox.okToast("导入数据取消！");
                } else {
                    e.printStackTrace();
                    this.updateStatus("数据导入失败");
                    MessageBox.warn("导入数据失败！");
                }
            } finally {
                // 结束处理
                this.groupDisabled.enable();
                this.stopImportBtn.disable();
                this.stage.restoreTitle();
                EventUtil.fire(RedisEvents.REDIS_IMPORT_FINISH, this.treeItem);
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
    private int handleExist(String key, int dbIndex, String type, String value, Long ttl) {
        if (this.skipForExist.isSelected()) {
            return 2;
        }
        if (this.overrideForExist.isSelected()) {
            this.client.del(dbIndex, key);
            this.createNode(key, dbIndex, type, value, ttl);
            return 4;
        }
        if (this.updateForExist.isSelected()) {
            String keyType = RedisKeyUtil.getKeyType(dbIndex, key, this.client);
            if (!StrUtil.equalsIgnoreCase(keyType, type)) {
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
    private void createNode(String key, int dbIndex, String type, String value, Long ttl) {
        RedisKey redisKey = RedisKeyUtil.deserializeNode(type, value);
        if (redisKey instanceof RedisStringKey stringNode) {
            this.client.set(dbIndex, key, (String) stringNode.value());
        } else if (redisKey instanceof RedisHyperLogLogKey) {
            this.client.pfadd(dbIndex, key, "");
        } else if (redisKey instanceof RedisListKey listNode) {
            String[] arr;
            if (CollUtil.isEmpty(listNode.value())) {
                arr = new String[]{""};
            } else {
                List<String> strings = listNode.value().parallelStream().map(RedisListRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(strings, String.class);
            }
            this.client.lpush(dbIndex, key, arr);
        } else if (redisKey instanceof RedisSetKey setNode) {
            String[] arr;
            if (CollUtil.isEmpty(setNode.value())) {
                arr = new String[]{""};
            } else {
                List<String> strings = setNode.value().parallelStream().map(RedisSetRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(strings, String.class);
            }
            this.client.sadd(dbIndex, key, arr);
        } else if (redisKey instanceof RedisZSetKey zSetNode) {
            Map<String, Double> scoreMembers;
            if (CollUtil.isEmpty(zSetNode.value())) {
                scoreMembers = new HashMap<>();
            } else {
                scoreMembers = new HashMap<>();
                for (RedisZSetRow row : zSetNode.value()) {
                    scoreMembers.put(row.getValue(), row.getScore());
                }
            }
            this.client.zadd(dbIndex, key, scoreMembers);
        } else if (redisKey instanceof RedisHashKey hashNode) {
            Map<String, String> hash;
            if (CollUtil.isEmpty(hashNode.value())) {
                hash = new HashMap<>();
            } else {
                hash = new HashMap<>();
                for (RedisHashRow row : hashNode.value()) {
                    hash.put(row.getField(), row.getValue());
                }
            }
            this.client.hmset(dbIndex, key, hash);
        } else if (redisKey instanceof RedisStreamKey streamNode) {
            if (CollUtil.isNotEmpty(streamNode.value())) {
                for (RedisStreamRow row : streamNode.value()) {
                    this.client.xadd(dbIndex, key, row.getEntry().getID(), row.getEntry().getFields());
                }
            }
        }
        if (redisKey != null && ttl != null && this.retainTTL.isSelected()) {
            this.client.expire(dbIndex, key, ttl, null);
        }
    }

    /**
     * 结束导入
     */
    @FXML
    private void stopImport() {
        ThreadUtil.interrupt(this.importTask);
        this.importTask = null;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.groupDisabled.addNodes(this.importBtn, this.retainTTL, this.chooseFile, this.skipForExist, this.updateForExist, this.overrideForExist);
        this.treeItem = this.getStageProp("treeItem");
        this.client = treeItem.client();
        this.scriptInfo.managedProperty().bind(this.scriptInfo.visibleProperty());
        this.scriptInfo.addTextChangeListener((observableValue, s, t1) -> this.scriptInfo.setVisible(StrUtil.isNotBlank(t1)));
        this.stage.hideOnEscape();
        // 文件拖拽相关
        this.stage.scene().setOnDragOver(event1 -> {
            this.stage.disable();
            this.stage.appendTitle("===松开鼠标以释放文件===");
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
    public void onStageHidden(WindowEvent event) {
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
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 成功";
            this.counter.updateSuccess();
        } else if (status == 2) {
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 跳过，此键已存在";
            this.counter.updateIgnore();
        } else if (status == 3) {
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 成功，此键已更新";
            this.counter.updateSuccess();
        } else if (status == 4) {
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 成功，此键已覆盖";
            this.counter.updateSuccess();
        } else if (status == 5) {
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 失败，此键已存在，且类型不一致";
            this.counter.updateFail();
        } else {
            msg = "导入键：[" + key + "(db" + dbIndex + ")] 失败";
            if (ex != null) {
                msg += "，错误信息：" + RedisExceptionParser.INSTANCE.apply(ex);
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
}
