package cn.oyzh.easyredis.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.controller.info.RedisInfoUpdateController;
import cn.oyzh.easyredis.controller.key.RedisKeyExportController;
import cn.oyzh.easyredis.controller.key.RedisKeyImportController;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.BaseTreeItem;
import cn.oyzh.easyredis.trees.group.RedisGroupTreeItem;
import cn.oyzh.easyredis.trees.server.RedisServerInfoTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItemFilter;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * redis连接键
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class RedisConnectTreeItem extends BaseTreeItem {

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
        this.treeView(treeView);
        this.value(value);
        // 监听键变化
        this.getChildren().addListener((ListChangeListener<? super RedisDBTreeItem>) c -> {
            this.treeView().fireChildChanged();
            this.treeView().flushLocal();
        });
    }

    /**
     * 初始化连接
     *
     * @return 结果
     */
    private boolean initConnect() {
        try {
            this.itemValue().role(this.client.getRole());
            this.itemValue().master(this.client.isMasterMode());
            this.itemValue().readOnly(this.client.isReadOnly());
            this.itemValue().cluster(this.client.isClusterMode());
            if (this.client.isSentinelMode()) {
                this.addChild(new RedisServerInfoTreeItem(this, this.treeView()));
            } else if (this.client.isClusterMode()) {
                List<RedisDBTreeItem> dbTreeItems = new ArrayList<>();
                dbTreeItems.add(new RedisDBTreeItem(null, this, this.treeView()));
                this.replaceChildes(dbTreeItems);
            } else {
                int databases = this.client().databases();
                List<RedisDBTreeItem> dbTreeItems = new ArrayList<>(databases);
                for (int dbIndex = 0; dbIndex < databases; dbIndex++) {
                    dbTreeItems.add(new RedisDBTreeItem(dbIndex, this, this.treeView()));
                }
                this.replaceChildes(dbTreeItems);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public RedisConnectTreeItemValue itemValue() {
        return (RedisConnectTreeItemValue) super.itemValue();
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

            items.add(disConnect);
            items.add(editConnect);
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

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
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
        EventUtil.fire(RedisEventTypes.REDIS_OPEN_TERMINAL, this.value);
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
            for (RedisDBTreeItem child : this.getChildren()) {
                child.clearChild();
                child.flushItemValue();
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
            Task task = TaskBuilder.newBuilder().onStart(() -> {
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
                            this._disConnect();
                        }
                    })
                    .onFinish(this::stopWaiting)
                    .onError(ex -> {
                        ex.printStackTrace();
                        MessageBox.exception(ex);
                    })
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
            this._disConnect();
        }
        StageWrapper fxView = StageUtil.parseStage(RedisInfoUpdateController.class, this.treeView().window());
        fxView.setProp("redisInfo", this.value());
        fxView.display();
    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (!this.isWaiting() && this.isConnected()) {
            this.startWaiting(this::_disConnect);
        }
    }

    /**
     * 断开连接实际业务
     */
    private void _disConnect() {
        this.itemValue().clearRole();
        this.client.close();
        this.clearChildren();
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
            this._disConnect();
            if (this.getParent() instanceof RedisConnectManager connectManager) {
                if (!connectManager.delConnectItem(this)) {
                    MessageBox.warn("删除连接失败！");
                }
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
            this.itemValue().name(connectName);
            // this.itemValue(connectName);
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
        this.itemValue(new RedisConnectTreeItemValue(this));
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

    @Override
    public ObservableList<RedisDBTreeItem> getChildren() {
        return super.getChildren();
    }

    /**
     * 获取数据库节点
     *
     * @param index 索引
     * @return 数据库节点
     */
    public RedisDBTreeItem getDatabaseItem(int index) {
        for (RedisDBTreeItem child : this.getChildren()) {
            if (child.dbIndex() == index) {
                return child;
            }
        }
        return null;
    }

    /**
     * 清理子节点
     */
    public void clearChildren() {
        try {
            this.setExpanded(false);
            this.getChildren().clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void filter(@NonNull RedisTreeItemFilter filter) {
        if (this.isConnected()) {
            for (RedisDBTreeItem dbTreeItem : this.getChildren()) {
                dbTreeItem.filter(filter);
            }
        }
    }

    // @Override
    // public void flushGraphic() {
    //     SVGGlyph glyph = (SVGGlyph) this.itemValue().graphic();
    //     if (glyph == null) {
    //         glyph = new SVGGlyph("/font/redis.svg", "12");
    //         this.itemValue().graphic(glyph);
    //     }
    //     if (this.isConnected() && glyph.getColor() != Color.GREEN) {
    //         glyph.setColor(Color.GREEN);
    //     }
    //     if (!this.isConnected() && glyph.getColor() != Color.BLACK) {
    //         glyph.setColor(Color.BLACK);
    //     }
    // }

    /**
     * 获分组键
     *
     * @return 分组键
     */
    public RedisGroupTreeItem getGroupItem() {
        if (this.getParent() instanceof RedisGroupTreeItem groupItem) {
            return groupItem;
        }
        return null;
    }
}
