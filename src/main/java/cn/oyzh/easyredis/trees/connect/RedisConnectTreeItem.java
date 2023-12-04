package cn.oyzh.easyredis.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.controller.info.RedisInfoUpdateController;
import cn.oyzh.easyredis.controller.key.RedisKeyExportController;
import cn.oyzh.easyredis.controller.key.RedisKeyImportController;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventTypes;
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
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * 初始化连接
     *
     * @return 结果
     */
    private boolean initConnect() {
        try {
            this.getValue().role(this.client.getRole());
            this.getValue().master(this.client.isMasterMode());
            this.getValue().readOnly(this.client.isReadOnly());
            this.getValue().cluster(this.client.isClusterMode());
            // 哨兵模式
            if (this.client.isSentinelMode()) {
                this.setChild(new RedisServerInfoTreeItem(this, this.getTreeView()));
            } else if (this.client.isClusterMode()) {// cluster集群模式
                List<TreeItem<?>> dbTreeItems = new ArrayList<>();
                dbTreeItems.add(new RedisDBTreeItem(null, this, this.getTreeView()));
                this.setChild(dbTreeItems);
            } else {// 其他模式
                int databases = this.client().databases();
                List<TreeItem<?>> dbTreeItems = new ArrayList<>(databases);
                for (int dbIndex = 0; dbIndex < databases; dbIndex++) {
                    dbTreeItems.add(new RedisDBTreeItem(dbIndex, this, this.getTreeView()));
                }
                this.setChild(dbTreeItems);
            }
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
            MenuItem cancel = MenuItemExt.newItem("取消连接", new SVGGlyph("/font/close.svg", "11"), "取消redis连接", this::cancelConnect);
            items.add(cancel);
        } else if (this.isConnected()) {
            MenuItemExt disConnect = MenuItemExt.newItem("断开连接", new SVGGlyph("/font/poweroff.svg", "12"), "断开redis连接(快捷键pause)", this::disConnect);
            MenuItemExt editConnect = MenuItemExt.newItem("编辑连接", new SVGGlyph("/font/edit.svg", "12"), "编辑连接", this::editConnect);
            MenuItemExt serverInfo = MenuItemExt.newItem("服务信息", new SVGGlyph("/font/server.svg", "12"), "查看服务信息", this::serverInfo);
            MenuItemExt exportData = MenuItemExt.newItem("导出数据", new SVGGlyph("/font/export.svg", "12"), "导出redis数据", this::exportNode);
            MenuItemExt importData = MenuItemExt.newItem("导入数据", new SVGGlyph("/font/Import.svg", "12"), "导入redis数据", this::importNode);
            MenuItemExt transportData = MenuItemExt.newItem("传输数据", new SVGGlyph("/font/arrow-left-right-line.svg", "12"), "传输redis数据", this::transportData);
            MenuItemExt flushAll = MenuItemExt.newItem("清空数据", new SVGGlyph("/font/clear.svg", "12"), "清空所有数据库", this::flushAll);
            MenuItemExt repeatConnect = MenuItemExt.newItem("复制连接", new SVGGlyph("/font/repeated.svg", "12"), "复制此zk连接为新连接", this::repeatConnect);

            items.add(disConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(serverInfo);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(flushAll);
        } else {
            MenuItemExt connect = MenuItemExt.newItem("开始连接", new SVGGlyph("/font/play-circle.svg", "12"), "开始连接(鼠标左键双击)", this::connect);
            MenuItemExt editConnect = MenuItemExt.newItem("编辑连接", new SVGGlyph("/font/edit.svg", "12"), "编辑连接", this::editConnect);
            MenuItemExt renameConnect = MenuItemExt.newItem("连接更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改连接名称(快捷键f2)", this::rename);
            MenuItemExt deleteConnect = MenuItemExt.newItem("删除连接", new SVGGlyph("/font/delete.svg", "12"), "删除连接(快捷键delete)", this::delete);
            MenuItemExt repeatConnect = MenuItemExt.newItem("复制连接", new SVGGlyph("/font/repeated.svg", "12"), "复制此zk连接为新连接", this::repeatConnect);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(deleteConnect);
        }

        MenuItem terminal = MenuItemExt.newItem("打开终端", new SVGGlyph("/font/code library.svg", "11"), "打开redis终端", this::openTerminal);
        items.add(terminal);
        return items;
    }

    /**
     * 服务信息
     */
    @FXML
    private void serverInfo() {
        EventUtil.fire(RedisEventTypes.REDIS_SERVER_INFO, this.client);
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
            for (TreeItem<?> child : this.getShowChildren()) {
                if (child instanceof RedisDBTreeItem treeItem) {
                    treeItem.clearChild();
                    // treeItem.flushValue();
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
        if (!this.isWaiting() && !this.isConnected() && !this.isConnecting()) {
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
                            this.flushGraphic();
                        } else {
                            this.closConnect();
                        }
                    })
                    .onFinish(this::stopWaiting)
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
        if (this.isConnected() && MessageBox.confirm("需要关闭连接，继续么？")) {
            this.closConnect();
            StageWrapper fxView = StageUtil.parseStage(RedisInfoUpdateController.class, this.window());
            fxView.setProp("redisInfo", this.value());
            fxView.display();
        }
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
     * 断开连接
     */
    public void disConnect() {
        if (!this.isWaiting() && this.isConnected()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(this::closConnect)
                    .onFinish(this::stopWaiting)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        }
    }

    /**
     * 断开连接实际业务
     */
    public void closConnect() {
        this.getValue().clearRole();
        this.client.close();
        this.clearChild();
        this.flushGraphic();
        SystemUtil.gcLater();
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
            this.closConnect();
            if (!this.parent().delConnectItem(this)) {
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
            // MessageBox.warn("连接名称不能为空！");
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
        this.disConnect();
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
        for (TreeItem<?> child : this.getShowChildren()) {
            if (child instanceof RedisDBTreeItem treeItem && treeItem.dbIndex() == index) {
                return treeItem;
            }
        }
        return null;
    }

    @Override
    public void sortAsc() {
        if (this.isSortEnable()) {
            this.sortType = 0;
            // 执行排序
            List<RedisDBTreeItem> childes = this.getChildren();
            if (!childes.isEmpty()) {
                childes.sort(Comparator.comparingInt(RedisDBTreeItem::dbIndex));
            }
        }
    }

    @Override
    public void sortDesc() {
        if (this.isSortEnable()) {
            this.sortType = 1;
            // 执行排序
            List<RedisDBTreeItem> childes = this.getChildren();
            if (!childes.isEmpty()) {
                childes.sort((a, b) -> Integer.compare(b.dbIndex(), a.dbIndex()));
            }
        }
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
}
