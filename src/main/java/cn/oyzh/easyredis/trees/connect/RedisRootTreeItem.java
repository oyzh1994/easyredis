package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoAddController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.dto.RedisInfoExport;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisConnectJdbcStore;
import cn.oyzh.easyredis.store.RedisGroupJdbcStore;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * redis树根节点
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class RedisRootTreeItem extends RichTreeItem<RedisRootTreeItem.RedisRootTreeItemValue> implements RedisConnectManager {

    /**
     * redis信息储存
     */
    private final RedisConnectJdbcStore infoStore = RedisConnectJdbcStore.INSTANCE;

    /**
     * redis分组储存
     */
    private final RedisGroupJdbcStore groupStore = RedisGroupJdbcStore.INSTANCE;

    public RedisRootTreeItem(@NonNull RedisConnectTreeView treeView) {
        super(treeView);
        this.setValue(new RedisRootTreeItemValue());
        // 加载子节点
        this.loadChild();
    }

    @Override
    public RedisConnectTreeView getTreeView() {
        return (RedisConnectTreeView) super.getTreeView();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect("12", this::addConnect);
        FXMenuItem exportConnect = MenuItemHelper.exportConnect("12", this::exportConnect);
        FXMenuItem importConnect = MenuItemHelper.importConnect("12", this::importConnect);
        FXMenuItem addGroup = MenuItemHelper.addGroup("12", this::addGroup);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(exportConnect);
        items.add(importConnect);
        items.add(addGroup);
        return items;
    }

    /**
     * 导出连接
     */
    public void exportConnect() {
        List<RedisConnect> infos = this.infoStore.load();
        if (infos.isEmpty()) {
            MessageBox.warn(I18nHelper.connectionIsEmpty());
            return;
        }
        RedisInfoExport export = RedisInfoExport.fromConnects(infos);
        FileExtensionFilter extensionFilter = FileChooserHelper.jsonExtensionFilter();
        File file = FileChooserHelper.save(I18nHelper.saveConnection(), I18nResourceBundle.i18nString("base.redis", "base.connect", "base._json"), extensionFilter);
        if (file != null) {
            try {
                FileUtil.writeUtf8String(export.toJSONString(), file);
                MessageBox.okToast(I18nHelper.exportConnectionSuccess());
            } catch (Exception ex) {
                MessageBox.exception(ex, I18nHelper.exportConnectionFail());
            }
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile());
            return;
        }
        File file = CollectionUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    public void importConnect() {
        FileExtensionFilter filter1 = FileChooserHelper.jsonExtensionFilter();
        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 解析连接文件
     *
     * @param file 文件
     */
    private void parseConnect(File file) {
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
        if (!FileNameUtil.isJsonType(FileNameUtil.extName(file.getName()))) {
            MessageBox.warn(I18nHelper.invalidFormat());
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            RedisInfoExport export = RedisInfoExport.fromJSON(text);
            List<RedisConnect> infos = export.getConnects();
            if (CollectionUtil.isNotEmpty(infos)) {
                for (RedisConnect info : infos) {
                    if (!this.infoStore.replace(info)) {
                        MessageBox.warn(I18nHelper.connect() + " : " + info.getName() + " " + I18nHelper.importFail());
                    }
                }
                // 重新加载节点
                this.loadChild();
                // 提示成功
                MessageBox.okToast(I18nHelper.importConnectionSuccess());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.importConnectionFail());
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageManager.showStage(RedisInfoAddController.class, this.window());
    }

    /**
     * 添加分组
     */
    public void addGroup() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1());

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StringUtil.isBlank(groupName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }

        RedisGroup group = new RedisGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }

        if (this.groupStore.replace(group)) {
            this.addChild(new RedisGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 获取分组树节点组件
     *
     * @param groupId 分组id
     */
    private RedisGroupTreeItem getGroupItem(String groupId) {
        if (StringUtil.isNotBlank(groupId)) {
            List<RedisGroupTreeItem> items = this.getGroupItems();
            Optional<RedisGroupTreeItem> groupTreeItem = items.parallelStream().filter(g -> Objects.equals(g.value().getGid(), groupId)).findAny();
            return groupTreeItem.orElse(null);
        }
        return null;
    }

    /**
     * 获取分组树节点组件
     *
     * @return 分组树节点组件
     */
    private List<RedisGroupTreeItem> getGroupItems() {
        List<RedisGroupTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.unfilteredChildren()) {
            if (item instanceof RedisGroupTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    /**
     * 连接新增事件
     *
     * @param info 连接
     */
    public void infoAdded(RedisConnect info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接
     */
    public void infoUpdate(RedisConnect info) {
        f1:
        for (TreeItem<?> item : this.unfilteredChildren()) {
            if (item instanceof RedisConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof RedisGroupTreeItem groupTreeItem) {
                for (RedisConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect(@NonNull RedisConnect info) {
        RedisGroupTreeItem groupItem = this.getGroupItem(info.getGroupId());
        if (groupItem == null) {
            super.addChild(new RedisConnectTreeItem(info, this.getTreeView()));
            this.expend();
        } else {
            groupItem.addConnect(info);
        }
    }

    @Override
    public void addConnectItem(@NonNull RedisConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.infoStore.update(item.value());
            }
            super.addChild(item);
            this.expend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<RedisConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.expend();
        }
    }

    @Override
    public boolean delConnectItem(@NonNull RedisConnectTreeItem item) {
        // 删除连接
        if (this.infoStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<RedisConnectTreeItem> getConnectItems() {
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> child : this.unfilteredChildren()) {
            if (child instanceof RedisConnectTreeItem connectTreeItem) {
                items.add(connectTreeItem);
            } else if (child instanceof RedisGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectItems());
            }
        }
        return items;
    }

    @Override
    public List<RedisConnectTreeItem> getConnectedItems() {
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (Object item : this.unfilteredChildren()) {
            if (item instanceof RedisConnectTreeItem connectTreeItem) {
                if (connectTreeItem.isConnected()) {
                    items.add(connectTreeItem);
                }
            } else if (item instanceof RedisGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectedItems());
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        return item instanceof RedisConnectTreeItem;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof RedisConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }

    @Override
    public void loadChild() {
        // 初始化分组
        List<RedisGroup> groups = this.groupStore.load();
        if (CollectionUtil.isNotEmpty(groups)) {
            List<RedisGroupTreeItem> groupItems = this.getGroupItems();
            List<TreeItem<?>> list = new ArrayList<>();
            f1:
            for (RedisGroup group : groups) {
                for (RedisGroupTreeItem groupItem : groupItems) {
                    if (StringUtil.equals(groupItem.getGid(), group.getGid())) {
                        continue f1;
                    }
                }
                list.add(new RedisGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<RedisConnect> connects = this.infoStore.load();
        if (CollectionUtil.isNotEmpty(connects)) {
            List<RedisConnectTreeItem> connectItems = this.getConnectItems();
            List<RedisConnect> list = new ArrayList<>();
            f1:
            for (RedisConnect connect : connects) {
                for (RedisConnectTreeItem connectItem : connectItems) {
                    if (StringUtil.equals(connectItem.getId(), connect.getId())) {
                        continue f1;
                    }
                }
                list.add(connect);
            }
            this.addConnects(list);
        }
    }

    /**
     * redis 根节点值
     *
     * @author oyzh
     * @since 2023/11/21
     */
    public static class RedisRootTreeItemValue extends RichTreeItemValue {

        @Override
        public String name() {
            return I18nHelper.redis();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/redis.svg", 10);
            }
            return super.graphic();
        }
    }
}
