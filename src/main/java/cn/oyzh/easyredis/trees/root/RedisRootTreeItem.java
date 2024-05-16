package cn.oyzh.easyredis.trees.root;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoAddController;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.dto.RedisInfoExport;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.group.RedisGroupTreeItem;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.AddConnectMenuItem;
import cn.oyzh.fx.plus.menu.AddGroupMenuItem;
import cn.oyzh.fx.plus.menu.ExportConnectMenuItem;
import cn.oyzh.fx.plus.menu.ImportConnectMenuItem;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.util.FileChooserUtil;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
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
public class RedisRootTreeItem extends RedisTreeItem<RedisRootTreeItemValue> implements RedisConnectManager {

    /**
     * redis信息储存
     */
    private final RedisInfoStore infoStore = RedisInfoStore.INSTANCE;

    /**
     * redis分组储存
     */
    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    public RedisRootTreeItem(@NonNull RedisTreeView treeView) {
        super(treeView);
        this.setValue(new RedisRootTreeItemValue());
        // 初始化子节点
        this.initChildes();
        // 监听变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            RedisEventUtil.treeChildChanged();
            this.flushLocal();
        });
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        // 初始化分组
        List<RedisGroup> groups = this.groupStore.load();
        if (CollUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>();
            for (RedisGroup group : groups) {
                list.add(new RedisGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<RedisInfo> infos = this.infoStore.load();
        if (CollUtil.isNotEmpty(infos)) {
            this.addConnects(infos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        AddConnectMenuItem addConnect = new AddConnectMenuItem("12", this::addConnect);
        ExportConnectMenuItem exportConnect = new ExportConnectMenuItem("12", this::exportConnect);
        ImportConnectMenuItem importConnect = new ImportConnectMenuItem("12", this::importConnect);
        AddGroupMenuItem addGroup = new AddGroupMenuItem("12", this::addGroup);

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
    private void exportConnect() {
        List<RedisInfo> infos = this.infoStore.load();
        if (infos.isEmpty()) {
            MessageBox.warn(I18nHelper.connectionIsEmpty());
            return;
        }
        RedisInfoExport export = RedisInfoExport.fromConnects(infos);
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
        File file = FileChooserUtil.save(I18nHelper.saveConnection(), I18nResourceBundle.i18nString("base.redis", "base.connect", "base._json"), new FileChooser.ExtensionFilter[]{extensionFilter});
        if (file != null) {
            try {
                FileUtil.writeUtf8String(export.toJSONString(), file);
                MessageBox.okToast(I18nHelper.operationSuccess());
            } catch (Exception ex) {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile());
            return;
        }
        File file = CollUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files", "*.json");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FileChooserUtil.choose(I18nHelper.chooseFile(), new FileChooser.ExtensionFilter[]{filter1, filter2});
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
        if (!FileNameUtil.isType(file.getName(), "json")) {
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
            List<RedisInfo> infos = export.getConnects();
            if (CollUtil.isNotEmpty(infos)) {
                for (RedisInfo info : infos) {
                    if (this.infoStore.add(info)) {
                        this.addConnect(info);
                    } else {
                        MessageBox.warn(I18nHelper.connect()+ "[" + info.getName() + "]" + I18nHelper.importFail());
                    }
                }
                MessageBox.okToast(I18nHelper.operationSuccess());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException());
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageUtil.showStage(RedisInfoAddController.class, this.window());
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
        if (StrUtil.isBlank(groupName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }

        RedisGroup group = new RedisGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        group = this.groupStore.add(groupName);
        if (group != null) {
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
        if (StrUtil.isNotBlank(groupId)) {
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
        for (TreeItem<?> item : this.getRealChildren()) {
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
    public void infoAdded(RedisInfo info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接
     */
    public void infoUpdate(RedisInfo info) {
        f1:
        for (TreeItem<?> item : this.getRealChildren()) {
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
    public void addConnect(@NonNull RedisInfo info) {
        RedisGroupTreeItem groupItem = this.getGroupItem(info.getGroupId());
        if (groupItem == null) {
            super.addChild(new RedisConnectTreeItem(info, this.getTreeView()));
            this.extend();
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
            this.extend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<RedisConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.extend();
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
        for (TreeItem<?> child : this.getRealChildren()) {
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
        for (Object item : this.getRealChildren()) {
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
}
