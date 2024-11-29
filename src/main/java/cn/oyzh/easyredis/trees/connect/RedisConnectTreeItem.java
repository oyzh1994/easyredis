package cn.oyzh.easyredis.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.controller.info.RedisInfoUpdateController;
import cn.oyzh.easyredis.controller.key.RedisKeyExportController;
import cn.oyzh.easyredis.controller.key.RedisKeyImportController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.server.RedisServerInfoTreeItem;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.thread.BackgroundService;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * redis连接键
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class RedisConnectTreeItem extends RedisTreeItem<RedisConnectTreeItemValue> {

    /**
     * redis信息
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisConnect value;

    /**
     * redis客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisClient client;

    /**
     * 已取消操作标志位
     */
    private boolean canceled;

    /**
     * redis信息储存
     */
    private final RedisInfoStore infoStore = RedisInfoStore.INSTANCE;

    public RedisConnectTreeItem(@NonNull RedisConnect value, @NonNull RedisTreeView treeView) {
        super(treeView);
        this.value(value);
        // 监听变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            RedisEventUtil.treeChildChanged();
            this.flushLocal();
        });
    }

    /**
     * 当前连接角色
     */
    public String role() {
        return this.client.getRole();
    }

    /**
     * 是否master集群模式
     *
     * @return 结果
     */
    public boolean isMasterMode() {
        return this.client.isMasterMode();
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return this.client.isReadonly();
    }

    /**
     * 是否cluster集群模式
     *
     * @return 结果
     */
    public boolean isClusterMode() {
        return this.client.isClusterMode();
    }

    /**
     * 是否单机模式
     *
     * @return 结果
     */
    public boolean isStandaloneMode() {
        return this.client.isStandaloneMode();
    }

    /**
     * 是否哨兵模式
     *
     * @return 结果
     */
    public boolean isSentinelMode() {
        return this.client.isSentinelMode();
    }

    /**
     * 初始化连接
     *
     * @return 结果
     */
    private boolean initConnect() {
        try {
            // 哨兵模式
            if (this.client.isSentinelMode()) {
                this.setChild(new RedisServerInfoTreeItem(this));
            } else if (this.client.isClusterMode()) {// cluster集群模式
                this.setChild(new RedisDBTreeItem(null, this));
            } else {// 其他模式
                int databases = this.client().databases();
                List<TreeItem<?>> items = new ArrayList<>(databases);
                for (int dbIndex = 0; dbIndex < databases; dbIndex++) {
                    items.add(new RedisDBTreeItem(dbIndex, this));
                }
                this.setChild(items);
            }
            // 刷新角色
            BackgroundService.submitFXLater(this::flushRole);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    public void flushRole() {
        this.getValue().flushRole();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            FXMenuItem cancelConnect = MenuItemHelper.cancelConnect("12", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            FXMenuItem closeConnect = MenuItemHelper.closeConnect("12", this::closeConnect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            FXMenuItem server = MenuItemHelper.serverInfo("12", this::serverInfo);
            FXMenuItem exportData =  MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
            FXMenuItem flushAll = MenuItemHelper.clearData("12", this::flushAll);

            items.add(closeConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(server);
            items.add(flushAll);
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect("12", this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect("12", this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect("12", this::delete);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(transportData);
            items.add(deleteConnect);
        }
        FXMenuItem openTerminal = MenuItemHelper.openTerminal("12", this::openTerminal);
        items.add(openTerminal);
        return items;
    }

    /**
     * 导出zk节点
     */
    public void exportData() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyExportController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 查看服务信息
     */
    private void serverInfo() {
        RedisEventUtil.serverMonitor(this.client);
    }

    /**
     * 打开终端
     */
    private void openTerminal() {
        RedisEventUtil.terminalOpen(this.value);
    }

    /**
     * 清空所有
     */
    private void flushAll() {
        if (!MessageBox.confirm(RedisI18nHelper.connectTip1(), RedisI18nHelper.connectTip4())) {
            return;
        }
        if (!MessageBox.confirm(RedisI18nHelper.connectTip2(), RedisI18nHelper.connectTip4())) {
            return;
        }
        if (!MessageBox.confirm(RedisI18nHelper.connectTip3(), RedisI18nHelper.connectTip4())) {
            return;
        }
        try {
            // 清空数据
            this.client().flushAll();
            for (TreeItem<?> child : this.getRealChildren()) {
                if (child instanceof RedisDBTreeItem treeItem) {
                    treeItem.clearChild();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 取消连接
     */
    public void cancelConnect() {
        this.canceled = true;
        ThreadUtil.startVirtual(() -> {
            this.client.close();
            this.stopWaiting();
        });
    }

    /**
     * 连接
     */
    public void connect() {
        if (!this.isConnected() && !this.isConnecting()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.client.start();
                        if (!this.isConnected()) {
                            if (!this.canceled) {
                                MessageBox.warn("[" + this.value.getName() + "] " + I18nHelper.connectFail());
                            }
                            this.canceled = false;
                            this.closeConnect(false);
                        } else if (this.initConnect()) {
                            this.extend();
                        }
                        this.flushGraphic();
                    })
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            // 执行连接
            this.startWaiting(task);
        }
    }

    /**
     * 导入数据
     */
    private void importData() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyImportController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 传输数据
     */
    private void transportData() {
        StageAdapter wrapper = StageManager.getStage(RedisInfoTransportController.class);
        if (wrapper != null) {
            wrapper.disappear();
        }
        wrapper = StageManager.parseStage(RedisInfoTransportController.class);
        wrapper.setProp("treeItem", this);
        wrapper.display();
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
            this.closeConnect(true);
        }
    }

    /**
     * 关闭连接
     *
     * @param waiting 是否开启等待动画
     */
    public void closeConnect(boolean waiting) {
        Runnable func = () -> {
            this.client.close();
            this.getValue().clearRole();
            this.clearChild();
            this.flushGraphic();
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func)
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
        }
    }

    @Override
    public void free() {
        if (!this.isConnected()) {
            this.connect();
        } else {
            super.free();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm(I18nHelper.closeAndContinue())) {
                return;
            }
            this.closeConnect();
        }
        StageAdapter fxView = StageManager.parseStage(RedisInfoUpdateController.class, this.window());
        fxView.setProp("redisInfo", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        RedisConnect redisInfo = new RedisConnect();
        redisInfo.copy(this.value);
        redisInfo.setName(this.value.getName() + "-" + I18nHelper.repeat());
        redisInfo.setCollects(Collections.emptyList());
        if (this.infoStore.add(redisInfo)) {
            this.parent().addConnect(redisInfo);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.parent().delConnectItem(this)) {
                RedisEventUtil.infoDeleted(this.value);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(connectName)) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        this.value.setName(connectName);
        // 修改名称
        if (this.infoStore.update(this.value)) {
            this.setValue(new RedisConnectTreeItemValue(this));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 设置值
     *
     * @param value redis信息
     */
    public void value(@NonNull RedisConnect value) {
        this.value = value;
        this.client = new RedisClient(value);
        this.setValue(new RedisConnectTreeItemValue(this));
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 获取数据库节点
     *
     * @param index 索引
     * @return 数据库节点
     */
    public RedisDBTreeItem getDatabaseItem(int index) {
        for (TreeItem<?> child : this.getRealChildren()) {
            if (child instanceof RedisDBTreeItem treeItem && treeItem.dbIndex() == index) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取当前父节点
     *
     * @return 父节点
     */
    public RedisConnectManager parent() {
        Object object = this.getParent();
        if (object instanceof RedisConnectManager connectManager) {
            return connectManager;
        }
        return null;
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.connect();
    }
}
