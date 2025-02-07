package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.SystemUtil;
import cn.oyzh.easyredis.controller.connect.RedisConnectUpdateController;
import cn.oyzh.easyredis.controller.data.RedisDataExportController;
import cn.oyzh.easyredis.controller.data.RedisDataImportController;
import cn.oyzh.easyredis.controller.data.RedisDataTransportController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.trees.server.RedisServerInfoTreeItem;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * redis连接键
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class RedisConnectTreeItem extends RichTreeItem<RedisConnectTreeItemValue> {

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
    private final RedisConnectStore connectStore = RedisConnectStore.INSTANCE;

    public RedisConnectTreeItem(@NonNull RedisConnect value, @NonNull RedisConnectTreeView treeView) {
        super(treeView);
        this.value(value);
    }

    @Override
    public RedisConnectTreeView getTreeView() {
        return (RedisConnectTreeView) super.getTreeView();
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

    @Override
    public void loadChild() {
        if (!this.isLoaded() && !this.isLoading()) {
            try {
                this.setLoaded(true);
                this.setLoading(true);
                // 哨兵模式
                if (this.isSentinelMode()) {
                    RedisServerInfoTreeItem item2 = new RedisServerInfoTreeItem(this.getTreeView());
                    RedisTerminalTreeItem item3 = new RedisTerminalTreeItem(this.getTreeView(), null);
                    RedisQueriesTreeItem item4 = new RedisQueriesTreeItem(this.getTreeView());
//                    this.setChild(List.of(item2, item3));
                    this.setChild(List.of(item2, item3, item4));
                } else if (this.isClusterMode()) {// 集群模式
                    RedisDatabaseTreeItem item1 = new RedisDatabaseTreeItem(null, this.getTreeView());
                    RedisServerInfoTreeItem item2 = new RedisServerInfoTreeItem(this.getTreeView());
                    RedisTerminalTreeItem item3 = new RedisTerminalTreeItem(this.getTreeView(), null);
                    RedisQueriesTreeItem item4 = new RedisQueriesTreeItem(this.getTreeView());
//                    this.setChild(List.of(item1, item2, item3));
                    this.setChild(List.of(item1, item2, item3, item4));
                } else {// 正常模式
                    RedisDatabasesTreeItem item1 = new RedisDatabasesTreeItem(this.getTreeView());
                    RedisServerInfoTreeItem item2 = new RedisServerInfoTreeItem(this.getTreeView());
                    RedisTerminalTreeItem item3 = new RedisTerminalTreeItem(this.getTreeView(), null);
                    RedisQueriesTreeItem item4 = new RedisQueriesTreeItem(this.getTreeView());
//                    this.setChild(List.of(item1, item2, item3));
                    this.setChild(List.of(item1, item2, item3, item4));
                }
                this.expend();
            } catch (Exception ex) {
                this.setLoaded(false);
                ex.printStackTrace();
                JulLog.warn("loadChild error", ex);
            } finally {
                this.setLoading(false);
            }
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isWaiting()) {
            FXMenuItem cancelConnect = MenuItemHelper.cancelConnect("12", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            FXMenuItem closeConnect = MenuItemHelper.closeConnect("12", this::closeConnect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem cloneConnect = MenuItemHelper.cloneConnect("12", this::cloneConnect);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
            FXMenuItem flushAll = MenuItemHelper.clearData("12", this::flushAll);
            FXMenuItem openTerminal = MenuItemHelper.openTerminal("12", this::openTerminal);

            items.add(closeConnect);
            items.add(editConnect);
            items.add(cloneConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(flushAll);
            items.add(openTerminal);
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect("12", this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect("12", this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect("12", this::delete);
            FXMenuItem cloneConnect = MenuItemHelper.cloneConnect("12", this::cloneConnect);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
            FXMenuItem openTerminal = MenuItemHelper.openTerminal("12", this::openTerminal);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(cloneConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(deleteConnect);
            items.add(openTerminal);
        }
        return items;
    }

    /**
     * 导出redis节点
     */
    public void exportData() {
        StageAdapter fxView = StageManager.parseStage(RedisDataExportController.class);
        fxView.setProp("connect", this.value);
        fxView.display();
    }

    /**
     * 打开终端
     */
    private void openTerminal() {
        RedisEventUtil.terminalOpen(this.client, null);
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
            for (TreeItem<?> child : this.unfilteredChildren()) {
                if (child instanceof RedisDatabaseTreeItem treeItem) {
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
            this.setLoaded(false);
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
                        } else {
                            this.loadChild();
                        }
                    })
                    .onSuccess(this::refresh)
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
        StageAdapter fxView = StageManager.parseStage(RedisDataImportController.class);
        fxView.setProp("connect", this.client.redisConnect());
        fxView.display();
    }

    /**
     * 传输数据
     */
    private void transportData() {
        StageAdapter adapter = StageManager.parseStage(RedisDataTransportController.class);
        adapter.setProp("sourceInfo", this.value);
        adapter.display();
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
            this.setLoaded(false);
            this.clearChild();
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func::run)
                    .onSuccess(SystemUtil::gcLater)
                    .onError(MessageBox::exception)
                    .onFinish(this::refresh)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
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
        StageAdapter fxView = StageManager.parseStage(RedisConnectUpdateController.class, this.window());
        fxView.setProp("redisInfo", this.value());
        fxView.display();
    }

    /**
     * 克隆连接
     */
    private void cloneConnect() {
        RedisConnect redisConnect = new RedisConnect();
        redisConnect.copy(this.value);
        redisConnect.setName(this.value.getName() + "-" + I18nHelper.clone1());
        redisConnect.setCollects(Collections.emptyList());
        if (this.connectStore.replace(redisConnect)) {
            this.connectManager().addConnect(redisConnect);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.connectManager().delConnectItem(this)) {
                RedisEventUtil.connectDeleted(this.value);
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
        if (StringUtil.isBlank(connectName)) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        this.value.setName(connectName);
        // 修改名称
        if (this.connectStore.update(this.value)) {
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
        this.client.stateProperty().addListener((observable, o, n) -> {
            // 连接关闭
            if (n == null || !n.isConnected()) {
                // 清理子节点
                this.clearChild();
            }
        });
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
    public RedisDatabaseTreeItem getDatabaseItem(int index) {
        for (TreeItem<?> child : this.unfilteredChildren()) {
            if (child instanceof RedisDatabaseTreeItem treeItem && treeItem.dbIndex() == index) {
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
    public RedisConnectManager connectManager() {
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
        if (!this.isConnected() && !this.isConnecting()) {
            this.connect();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public String connectName() {
        return this.value.getName();
    }

    public String getId() {
        return this.value.getId();
    }

    public RedisQueriesTreeItem queriesItem() {
        return (RedisQueriesTreeItem) this.unfilteredChildren().stream().filter(i-> i instanceof RedisQueriesTreeItem).findAny().get();
    }
}
