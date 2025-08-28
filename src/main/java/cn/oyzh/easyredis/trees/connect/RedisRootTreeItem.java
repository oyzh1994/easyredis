package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.easyredis.util.RedisViewFactory;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

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
public class RedisRootTreeItem extends RichTreeItem<RedisRootTreeItemValue> implements RedisConnectManager {

    /**
     * redis分组储存
     */
    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    /**
     * redis信息储存
     */
    private final RedisConnectStore connectStore = RedisConnectStore.INSTANCE;

    public RedisRootTreeItem( RedisConnectTreeView treeView) {
        super(treeView);
        this.setValue(new RedisRootTreeItemValue());
        // 加载子节点
        this.loadChild();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>(4);
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
    private void exportConnect() {
//        List<ZKConnect> infos = this.connectStore.load();
//        if (infos.isEmpty()) {
//            MessageBox.warn(I18nHelper.connectionIsEmpty());
//            return;
//        }
//        ZKConnectExport export = ZKConnectExport.fromConnects(infos);
//        FileExtensionFilter extensionFilter = FileChooserHelper.jsonExtensionFilter();
//        File file = FileChooserHelper.save(I18nHelper.saveConnection(), I18nResourceBundle.i18nString("base.zk", "base.connect", "base._json"), extensionFilter);
//        if (file != null) {
//            try {
//                FileUtil.writeUtf8String(export.toJSONString(), file);
//                MessageBox.okToast(I18nHelper.exportConnectionSuccess());
//            } catch (Exception ex) {
//                MessageBox.exception(ex, I18nHelper.exportConnectionFail());
//            }
//        }
        RedisViewFactory.exportConnect();
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
//        // 解析文件
//        this.parseConnect(file);
        RedisViewFactory.importConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
//        FileExtensionFilter filter1 = FileChooserHelper.jsonExtensionFilter();
//        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
//        // 解析文件
//        this.parseConnect(file);
        RedisViewFactory.importConnect(null);
    }

//    /**
//     * 解析连接文件
//     *
//     * @param file 文件
//     */
//    private void parseConnect(File file) {
//        if (file == null) {
//            return;
//        }
//        if (!file.exists()) {
//            MessageBox.warn(I18nHelper.fileNotExists());
//            return;
//        }
//        if (file.isDirectory()) {
//            MessageBox.warn(I18nHelper.notSupportFolder());
//            return;
//        }
//        if (!FileNameUtil.isJsonType(FileNameUtil.extName(file.getName()))) {
//            MessageBox.warn(I18nHelper.invalidFormat());
//            return;
//        }
//        if (file.length() == 0) {
//            MessageBox.warn(I18nHelper.contentCanNotEmpty());
//            return;
//        }
//        try {
//            String text = FileUtil.readUtf8String(file);
//            ZKConnectExport export = ZKConnectExport.fromJSON(text);
//            List<ZKConnect> connects = export.getConnects();
//            if (CollectionUtil.isNotEmpty(connects)) {
//                for (ZKConnect connect : connects) {
//                    if (!this.connectStore.replace(connect)) {
//                        MessageBox.warn(I18nHelper.connect() + " : " + connect.getName() + " " + I18nHelper.importFail());
//                    }
//                }
//                // 重新加载节点
//                this.reloadChild();
//                // 提示成功
//                MessageBox.okToast(I18nHelper.importConnectionSuccess());
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            MessageBox.exception(ex, I18nHelper.importConnectionFail());
//        }
//    }

    /**
     * 添加连接
     */
    private void addConnect() {
//        StageManager.showStage(RedisAddConnectController.class, this.window());
        RedisViewFactory.addConnect(null);
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
        // 检查是否存在
        if (this.groupStore.exist(groupName)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        RedisGroup group = new RedisGroup();
        group.setName(groupName);
        if (this.groupStore.replace(group)) {
            this.addChild(new RedisGroupTreeItem(group, this.getTreeView()));
            RedisEventUtil.groupAdded(groupName);
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
    public List<RedisGroupTreeItem> getGroupItems() {
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
     * @param redisConnect 连接
     */
    public void connectAdded(RedisConnect redisConnect) {
        this.addConnect(redisConnect);
    }

    /**
     * 连接变更事件
     *
     * @param redisConnect 连接
     */
    public void connectUpdate(RedisConnect redisConnect) {
        f1:
        for (TreeItem<?> item : this.unfilteredChildren()) {
            if (item instanceof RedisConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == redisConnect) {
                    connectTreeItem.value(redisConnect);
                    break;
                }
            } else if (item instanceof RedisGroupTreeItem groupTreeItem) {
                for (RedisConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == redisConnect) {
                        connectTreeItem.value(redisConnect);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect( RedisConnect redisConnect) {
        RedisGroupTreeItem groupItem = this.getGroupItem(redisConnect.getGroupId());
        if (groupItem == null) {
            super.addChild(new RedisConnectTreeItem(redisConnect, this.getTreeView()));
            this.expend();
        } else {
            groupItem.addConnect(redisConnect);
        }
    }

    @Override
    public void addConnectItem( RedisConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.connectStore.update(item.value());
            }
            super.addChild(item);
            this.expend();
        }
    }

    @Override
    public void addConnectItems( List<RedisConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.expend();
        }
    }

    @Override
    public boolean delConnectItem( RedisConnectTreeItem item) {
        // 删除连接
        if (this.connectStore.delete(item.value())) {
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
        for (TreeItem<?> item : this.unfilteredChildren()) {
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
    public void reloadChild() {
        super.reloadChild();
        this.clearChild();
        this.loadChild();
    }

    @Override
    public void loadChild() {
        // 关闭连接
        List<RedisConnectTreeItem> connectedItems = this.getConnectedItems();
        for (RedisConnectTreeItem item : connectedItems) {
            item.closeConnect();
        }
        // 初始化分组
        List<RedisGroup> groups = this.groupStore.load();
        if (CollectionUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>(groups.size());
            for (RedisGroup group : groups) {
                list.add(new RedisGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<RedisConnect> connects = this.connectStore.loadFull();
        if (CollectionUtil.isNotEmpty(connects)) {
            for (RedisConnect connect : connects) {
                this.addConnect(connect);
            }
        }
        this.refresh();
    }

    public void queryAdded(RedisQuery query) {
        List<RedisConnectTreeItem> items = this.getConnectItems();
        if (items != null) {
            for (RedisConnectTreeItem item : items) {
                if (StringUtil.equals(item.getId(), query.getIid())) {
                    item.queriesItem().add(query);
                }
            }
        }
    }
}
