package cn.oyzh.easyredis.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.controller.info.RedisInfoUpdateController;
import cn.oyzh.easyredis.controller.key.RedisKeyExportController;
import cn.oyzh.easyredis.controller.key.RedisKeyImportController;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.easyredis.trees.server.RedisServerInfoTreeItem;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.menu.CancelActionMenuItem;
import cn.oyzh.fx.plus.menu.ClearDataMenuItem;
import cn.oyzh.fx.plus.menu.CloseConnectMenuItem;
import cn.oyzh.fx.plus.menu.DeleteConnectMenuItem;
import cn.oyzh.fx.plus.menu.EditConnectMenuItem;
import cn.oyzh.fx.plus.menu.EditMenuItem;
import cn.oyzh.fx.plus.menu.ExportDataMenuItem;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.ImportDataMenuItem;
import cn.oyzh.fx.plus.menu.OpenTerminalMenuItem;
import cn.oyzh.fx.plus.menu.RenameConnectMenuItem;
import cn.oyzh.fx.plus.menu.RepeatConnectMenuItem;
import cn.oyzh.fx.plus.menu.ServerInfoMenuItem;
import cn.oyzh.fx.plus.menu.StartConnectMenuItem;
import cn.oyzh.fx.plus.menu.TransportDataMenuItem;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.thread.BackgroundService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
    private RedisInfo value;

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

    public RedisConnectTreeItem(@NonNull RedisInfo value, @NonNull RedisTreeView treeView) {
        super(treeView);
        this.value(value);
        // 监听键变化
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
            BackgroundService.submitFXLater(() -> this.getValue().flushRole());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isWaiting()) {
            CancelActionMenuItem cancel = new CancelActionMenuItem("12", this::cancelConnect);
            items.add(cancel);
        } else if (this.isConnected()) {
            CloseConnectMenuItem disConnect = new CloseConnectMenuItem("12", this::closeConnect);
            EditConnectMenuItem editConnect = new EditConnectMenuItem("12", this::editConnect);
            ServerInfoMenuItem serverInfo = new ServerInfoMenuItem("12", this::serverInfo);
            ExportDataMenuItem exportData = new ExportDataMenuItem("12", this::exportNode);
            ImportDataMenuItem importData = new ImportDataMenuItem("12", this::importNode);
            TransportDataMenuItem transportData = new TransportDataMenuItem("12", this::transportData);
            ClearDataMenuItem flushAll = new ClearDataMenuItem("12", this::flushAll);
            RepeatConnectMenuItem repeatConnect = new RepeatConnectMenuItem("12", this::repeatConnect);

            items.add(disConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(serverInfo);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(flushAll);
        } else {
            TransportDataMenuItem connect = new TransportDataMenuItem("12", this::connect);
            EditConnectMenuItem editConnect = new EditConnectMenuItem("12", this::editConnect);
            RenameConnectMenuItem renameConnect = new RenameConnectMenuItem("12", this::rename);
            DeleteConnectMenuItem deleteConnect = new DeleteConnectMenuItem("12", this::delete);
            RepeatConnectMenuItem repeatConnect = new RepeatConnectMenuItem("12", this::repeatConnect);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(deleteConnect);
        }

        OpenTerminalMenuItem terminal = new OpenTerminalMenuItem("12", this::openTerminal);
        items.add(terminal);
        return items;
    }

    /**
     * 服务信息
     */
    @FXML
    private void serverInfo() {
        // EventUtil.fire(RedisEventTypes.REDIS_SERVER_INFO, this.client);
        RedisEventUtil.serverMonitor(this.client);
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        RedisEventUtil.terminalOpen(this.value);
    }

    /**
     * 传输数据
     */
    @FXML
    private void transportData() {
        StageWrapper fxView = StageUtil.getStage(RedisInfoTransportController.class);
        if (fxView != null) {
            fxView.disappear();
        }
        fxView = StageUtil.parseStage(RedisInfoTransportController.class);
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 导入键
     */
    public void importNode() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyImportController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 导出键
     */
    public void exportNode() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyExportController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 清空所有
     */
    private void flushAll() {
        if (!MessageBox.confirm("第1次确认，共3次", "确定清空所有数据库？")) {
            return;
        }
        if (!MessageBox.confirm("第2次确认，共3次", "请慎重操作，确定清空所有数据库？")) {
            return;
        }
        if (!MessageBox.confirm("第3次确认，共3次", "最后确认下，确定清空所有数据库？")) {
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
                                MessageBox.warn(this.value.getName() + "连接失败");
                            }
                            this.canceled = false;
                        } else if (this.initConnect()) {
                            this.extend();
                        } else {
                            this.closeConnect(false);
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
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm("需要关闭连接，继续么？")) {
                return;
            }
            this.closeConnect(false);
        }
        StageWrapper fxView = StageUtil.parseStage(RedisInfoUpdateController.class, this.window());
        fxView.setProp("redisInfo", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        RedisInfo redisInfo = new RedisInfo();
        redisInfo.copy(this.value);
        redisInfo.setName(this.value.getName() + "-复制");
        redisInfo.setCollects(Collections.emptyList());
        if (this.infoStore.add(redisInfo)) {
            this.parent().addConnect(redisInfo);
        } else {
            MessageBox.warn("复制连接失败！");
        }
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
        // 实际业务
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

    @Override
    public void delete() {
        if (MessageBox.confirm("删除" + this.value.getName(), "确定删除连接？")) {
            this.closeConnect(false);
            if (this.parent().delConnectItem(this)) {
                RedisEventUtil.infoDeleted(this.value);
            } else {
                MessageBox.warn("删除连接失败！");
            }
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt("请输入新的连接名称", this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(connectName)) {
            return;
        }
        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(connectName);
        if (this.infoStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn("此连接名称已存在！");
            return;
        }
        // 修改名称
        if (this.infoStore.update(this.value)) {
            this.getValue().name(connectName);
        } else {
            MessageBox.warn("修改连接名称失败！");
        }
    }

    /**
     * 设置值
     *
     * @param value redis信息
     */
    public void value(@NonNull RedisInfo value) {
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
     * 是否以广播
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.client != null && this.client.isClosed();
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
