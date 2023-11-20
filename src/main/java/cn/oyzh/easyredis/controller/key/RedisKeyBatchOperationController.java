package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.fx.RedisDBComboBox;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.RedisDBTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * reids键批量操作业务
 *
 * @author oyzh
 * @since 2020/10/09
 */
@Slf4j
@StageAttribute(
        title = "Redis键批量操作",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyBatchOperation.fxml"
)
public class RedisKeyBatchOperationController extends Controller {

    /**
     * ttl值
     */
    @FXML
    private NumberTextField ttl;

    /**
     * 删除键表达式
     */
    @FXML
    private ClearableTextField pattern1;

    /**
     * 设置ttl键表达式
     */
    @FXML
    private ClearableTextField pattern2;

    /**
     * 移动键表达式
     */
    @FXML
    private ClearableTextField pattern4;

    /**
     * 复制键表达式
     */
    @FXML
    private ClearableTextField pattern5;

    /**
     * 删除键表达式
     */
    @FXML
    private ReadOnlyTextArea keys1;

    /**
     * 设置ttl键表达式
     */
    @FXML
    private ReadOnlyTextArea keys2;

    /**
     * 清空库键表达式
     */
    @FXML
    private ReadOnlyTextArea keys3;

    /**
     * 移动键表达式
     */
    @FXML
    private ReadOnlyTextArea keys4;

    /**
     * 复制键表达式
     */
    @FXML
    private ReadOnlyTextArea keys5;

    /**
     * db索引
     */
    private int dbIndex;

    /**
     * redis客户端对象
     */
    private RedisClient client;

    /**
     * 树键
     */
    private RedisDBTreeItem treeItem;

    /**
     * 删除键列表
     */
    private Set<String> delKeys;

    /**
     * ttl键列表
     */
    private Set<String> ttlKeys;

    /**
     * 清空库键列表
     */
    private Set<String> flushDBKeys;

    /**
     * 移动键列表
     */
    private Set<String> moveKeys;

    /**
     * 移动目标库
     */
    @FXML
    private RedisDBComboBox moveTargetDB;

    /**
     * 复制键列表
     */
    private Set<String> copyKeys;

    /**
     * 复制目标库
     */
    @FXML
    private RedisDBComboBox copyTargetDB;

    /**
     * 复制时替换
     */
    @FXML
    private FlexCheckBox replaceOnCopy;

    /**
     * 删除键
     */
    @FXML
    private void delKeys() {
        try {
            this.client.throwSentinelException();
            if (CollUtil.isEmpty(this.delKeys)) {
                this.delKeys = this.client.keys(this.dbIndex, this.pattern1.getText());
            }
            if (CollUtil.isEmpty(this.delKeys)) {
                MessageBox.warn("未发现匹配的键");
                return;
            }
            if (MessageBox.confirm("确定删除这些键？")) {
                try {
                    this.stage.disable();
                    this.stage.appendTitle("操作中...");
                    this.client.del(this.dbIndex, this.delKeys);
                    this.showKeys(this.delKeys, this.keys1);
                    EventUtil.fire(RedisEventTypes.REDIS_KEY_FLUSH, this.treeItem);
                    MessageBox.okToast("删除键成功");
                } finally {
                    this.stage.enable();
                    this.stage.restoreTitle();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 设置键过期时间
     */
    @FXML
    private void expireKeys() {
        try {
            this.client.throwSentinelException();
            if (CollUtil.isEmpty(this.ttlKeys)) {
                this.ttlKeys = this.client.keys(this.dbIndex, this.pattern2.getText());
            }
            if (CollUtil.isEmpty(this.ttlKeys)) {
                MessageBox.warn("未发现匹配的键");
                return;
            }
            try {
                this.stage.disable();
                this.stage.appendTitle("操作中...");
                long ttl = this.ttl.getValue();
                if (ttl == 0) {
                    if (MessageBox.confirm("ttl为0时，这些键将被删除，确定么？")) {
                        this.client.del(this.dbIndex, this.ttlKeys);
                        this.showKeys(this.ttlKeys, this.keys2);
                        EventUtil.fire(RedisEventTypes.REDIS_KEY_FLUSH, this.treeItem);
                        MessageBox.okToast("操作成功");
                    }
                } else if (ttl == -1) {
                    if (MessageBox.confirm("ttl为-1时，这些键将被持久化，确定么？")) {
                        for (String ttlKey : this.ttlKeys) {
                            this.client.persist(this.dbIndex, ttlKey);
                        }
                        this.showKeys(this.ttlKeys, this.keys2);
                        EventUtil.fire(RedisEventTypes.REDIS_KEY_FLUSH, this.treeItem);
                        MessageBox.okToast("操作成功");
                    }
                } else {
                    for (String ttlKey : this.ttlKeys) {
                        this.client.expire(this.dbIndex, ttlKey, ttl, null);
                    }
                    this.showKeys(this.ttlKeys, this.keys2);
                    EventUtil.fire(RedisEventTypes.REDIS_KEY_FLUSH, this.treeItem);
                    MessageBox.okToast("设置ttl成功");
                }
            } finally {
                this.stage.enable();
                this.stage.restoreTitle();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 清空数据库
     */
    @FXML
    private void flushDB() {
        try {
            this.client.throwSentinelException();
            if (CollUtil.isEmpty(this.flushDBKeys)) {
                this.flushDBKeys = this.client.keys(this.dbIndex, "*");
            }
            if (MessageBox.confirm("确定清空数据库？")) {
                try {
                    this.stage.disable();
                    this.stage.appendTitle("操作中...");
                    this.client.flushDB(this.dbIndex);
                    this.showKeys(this.ttlKeys, this.keys3);
                    EventUtil.fire(RedisEventTypes.REDIS_KEY_FLUSH, this.treeItem);
                    MessageBox.okToast("清空数据库成功");
                } finally {
                    this.stage.enable();
                    this.stage.restoreTitle();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 移动键
     */
    @FXML
    private void moveKeys() {
        try {
            this.client.throwClusterException();
            this.client.throwSentinelException();
            if (CollUtil.isEmpty(this.moveKeys)) {
                this.moveKeys = this.client.keys(this.dbIndex, this.pattern4.getText());
            }
            if (CollUtil.isEmpty(this.moveKeys)) {
                MessageBox.warn("未发现匹配的键");
                return;
            }
            int targetDBIndex = this.moveTargetDB.getDB();
            if (targetDBIndex == this.dbIndex) {
                MessageBox.warn("目标库不能与当前库相同！");
                return;
            }
            if (MessageBox.confirm("确定移动这些键？")) {
                try {
                    this.stage.disable();
                    this.stage.appendTitle("操作中...");
                    for (String moveKey : this.moveKeys) {
                        this.client.move(moveKey, this.dbIndex, targetDBIndex);
                    }
                    this.showKeys(this.moveKeys, this.keys4);
                    this.treeItem.treeView().setProp("targetDB", targetDBIndex);
                    EventUtil.fire(RedisEventTypes.REDIS_KEY_MOVED, this.treeItem);
                    MessageBox.okToast("移动键成功");
                } finally {
                    this.stage.enable();
                    this.stage.restoreTitle();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制键
     */
    @FXML
    private void copyKeys() {
        try {
            this.client.throwClusterException();
            this.client.throwSentinelException();
            this.client.throwCommandException("copy");
            if (CollUtil.isEmpty(this.copyKeys)) {
                this.copyKeys = this.client.keys(this.dbIndex, this.pattern5.getText());
            }
            if (CollUtil.isEmpty(this.copyKeys)) {
                MessageBox.warn("未发现匹配的键");
                return;
            }
            int targetDBIndex = this.copyTargetDB.getDB();
            if (targetDBIndex == this.dbIndex) {
                MessageBox.warn("目标库不能与当前库相同！");
                return;
            }
            if (MessageBox.confirm("确定复制这些键？")) {
                try {
                    this.stage.disable();
                    this.stage.appendTitle("操作中...");
                    Set<String> keys = new HashSet<>();
                    for (String copyKey : this.copyKeys) {
                        boolean result = this.client.copy(this.dbIndex, copyKey, copyKey, targetDBIndex, this.replaceOnCopy.isSelected());
                        if (result) {
                            keys.add(copyKey);
                        }
                    }
                    this.showKeys(keys, this.keys5);
                    this.treeItem.treeView().setProp("targetDB", targetDBIndex);
                    EventUtil.fire(RedisEventTypes.REDIS_KEY_COPY, this.treeItem);
                    MessageBox.okToast("复制键成功");
                } finally {
                    this.stage.enable();
                    this.stage.restoreTitle();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 显示受影响的键
     *
     * @param keys 键列表
     * @param area 文本域组件
     */
    private void showKeys(Set<String> keys, FlexTextArea area) {
        area.clear();
        if (CollUtil.isNotEmpty(keys)) {
            List<String> texts = new ArrayList<>(keys.size());
            int index = 0;
            for (String key : keys) {
                texts.add(++index + ". " + key);
            }
            area.appendLines(texts);
        }
    }

    /**
     * 显示受影响的删除键
     */
    @FXML
    private void showKeys1() {
        this.delKeys = this.client.keys(this.dbIndex, this.pattern1.getText());
        this.showKeys(this.delKeys, this.keys1);
    }

    /**
     * 显示受影响的ttl键
     */
    @FXML
    private void showKeys2() {
        this.ttlKeys = this.client.keys(this.dbIndex, this.pattern2.getText());
        this.showKeys(this.ttlKeys, this.keys2);
    }

    /**
     * 显示受影响的ttl键
     */
    @FXML
    private void showKeys3() {
        this.flushDBKeys = this.client.keys(this.dbIndex, "*");
        this.showKeys(this.flushDBKeys, this.keys3);
    }

    /**
     * 显示受影响的移动键
     */
    @FXML
    private void showKeys4() {
        this.moveKeys = this.client.keys(this.dbIndex, this.pattern4.getText());
        this.showKeys(this.moveKeys, this.keys4);
    }

    /**
     * 显示受影响的复制键
     */
    @FXML
    private void showKeys5() {
        this.copyKeys = this.client.keys(this.dbIndex, this.pattern5.getText());
        this.showKeys(this.copyKeys, this.keys5);
    }

    @Override
    protected void bindListeners() {
        this.pattern1.addTextChangeListener((observable, oldValue, newValue) -> this.keys1.clear());
        this.pattern2.addTextChangeListener((observable, oldValue, newValue) -> this.keys2.clear());
        this.pattern4.addTextChangeListener((observable, oldValue, newValue) -> this.keys4.clear());
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        this.treeItem = this.getStageProp("treeItem");
        this.client = this.treeItem.client();
        this.dbIndex = this.treeItem.dbIndex();
        this.moveTargetDB.setDbCount(this.client.databases());
        this.moveTargetDB.selectFirst();
        this.copyTargetDB.setDbCount(this.client.databases());
        this.copyTargetDB.selectFirst();
        this.stage.appendTitle("(" + this.treeItem.info().getName() + "-db" + this.treeItem.dbIndex() + ")");
    }
}
