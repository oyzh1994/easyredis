package cn.oyzh.easyredis.trees.root;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.controller.info.RedisInfoAddController;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.dto.RedisInfoExport;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.BaseTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItemFilter;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.group.RedisGroupTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.FlexImageView;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.util.FXFileChooser;
import cn.oyzh.fx.plus.util.IconUtil;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class RedisRootTreeItem extends BaseTreeItem implements RedisConnectManager {

    /**
     * redis信息储存
     */
    private final RedisInfoStore infoStore = RedisInfoStore.INSTANCE;

    /**
     * redis分组储存
     */
    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    public RedisRootTreeItem(@NonNull RedisTreeView treeView) {
        this.treeView(treeView);
        this.itemValue(new RedisRootTreeItemValue());
        // this.itemValue("Redis连接列表");
        // 注册事件处理
        EventUtil.register(this);
        // 初始化子节点
        this.initChildes();
        // 监听键变化
        this.getChildren().addListener((ListChangeListener<? super BaseTreeItem>) c -> {
            this.treeView().fireChildChanged();
            this.treeView().flushLocal();
        });
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        List<RedisGroup> groups = this.groupStore.load();
        List<RedisInfo> redisInfos = this.infoStore.load();
        this.addGroups(groups);
        this.addConnects(redisInfos);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem addConnect = MenuItemExt.newItem("添加连接", new SVGGlyph("/font/add.svg", "12"), "添加redis连接", this::addConnect);
        MenuItem addGroup = MenuItemExt.newItem("添加分组", new SVGGlyph("/font/addGroup.svg", "12"), "添加分组", this::addGroup);
        MenuItem exportConnect = MenuItemExt.newItem("导出连接", new SVGGlyph("/font/export.svg", "12"), "导出redis连接", this::exportConnect);
        MenuItem importConnect = MenuItemExt.newItem("导入连接", new SVGGlyph("/font/Import.svg", "12"), "选择文件，导入redis连接，也可拖拽文件到窗口进行导入", this::importConnect);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(addGroup);
        items.add(exportConnect);
        items.add(importConnect);
        return items;
    }

    /**
     * 导出连接
     */
    private void exportConnect() {
        List<RedisInfo> redisInfos = this.infoStore.load();
        if (redisInfos.isEmpty()) {
            MessageBox.warn("连接为空！");
            return;
        }
        RedisInfoExport export = RedisInfoExport.fromConnects(redisInfos);
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
        File file = FXFileChooser.save("保存Redis连接列表", "Redis连接列表.json", new FileChooser.ExtensionFilter[]{extensionFilter});
        if (file != null) {
            try {
                FileUtil.writeUtf8String(export.toJSONString(), file);
                MessageBox.okToast("保存连接成功！");
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.warn("保存连接失败！");
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
            MessageBox.warn("仅支持单个文件！");
            return;
        }
        File file = files.get(0);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files", "*.json");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FXFileChooser.choose("选择redis连接列表", new FileChooser.ExtensionFilter[]{filter1, filter2});
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
            MessageBox.warn("文件不存在！");
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn("不支持文件夹！");
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn("仅支持json文件！");
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn("文件内容为空！");
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            RedisInfoExport export = RedisInfoExport.fromJSON(text);
            List<RedisInfo> redisInfos = export.getConnects();
            if (CollUtil.isNotEmpty(redisInfos)) {
                for (RedisInfo info : redisInfos) {
                    if (this.infoStore.exist(info)) {
                        MessageBox.warn("连接[" + info.getName() + "]已存在");
                    } else if (this.infoStore.add(info)) {
                        this.addConnect(info);
                    } else {
                        MessageBox.warn("连接[" + info.getName() + "]导入失败");
                    }
                }
                MessageBox.okToast("导入连接成功！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.warn("解析连接失败！");
        }
    }

    /**
     * 添加连接
     */
    @EventReceiver(RedisEventTypes.REDIS_ADD_CONNECT)
    private void addConnect() {
        StageUtil.showStage(RedisInfoAddController.class, this.window());
    }

    /**
     * 添加分组
     */
    @EventReceiver(RedisEventTypes.REDIS_ADD_GROUP)
    private void addGroup() {
        String groupName = MessageBox.prompt("请输入分组名称");

        // 名称为空，则忽略
        if (StrUtil.isBlank(groupName)) {
            return;
        }

        RedisGroup group = new RedisGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn("此分组已存在！");
            return;
        }
        group = this.groupStore.add(groupName);
        if (group != null) {
            this.addChild(new RedisGroupTreeItem(group, this.treeView()));
        } else {
            MessageBox.warn("添加分组失败！");
        }
    }

    @Override
    public ObservableList<BaseTreeItem> getChildren() {
        return super.getChildren();
    }

    /**
     * 添加多个分组
     *
     * @param redisGroups redis分组列表
     */
    private void addGroups(List<RedisGroup> redisGroups) {
        if (CollUtil.isNotEmpty(redisGroups)) {
            List<RedisGroupTreeItem> list = new ArrayList<>();
            for (RedisGroup group : redisGroups) {
                RedisGroupTreeItem groupTreeItem = new RedisGroupTreeItem(group, this.treeView());
                list.add(groupTreeItem);
            }
            this.getChildren().addAll(list);
            this.sort(this.treeView().sortOrder());
        }
    }

    /**
     * 获取分组键
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
     * 获取分组键
     *
     * @return 分组键
     */
    private List<RedisGroupTreeItem> getGroupItems() {
        List<RedisGroupTreeItem> items = new ArrayList<>(this.getChildren().size());
        for (BaseTreeItem item : this.getChildren()) {
            if (item instanceof RedisGroupTreeItem groupTreeItem) {
                items.add(groupTreeItem);
            }
        }
        return items;
    }

    @Override
    public void sort(Boolean sortOrder) {
        if (sortOrder != null) {
            super.sort(sortOrder);
            for (RedisGroupTreeItem groupItem : this.getGroupItems()) {
                groupItem.sort(sortOrder);
            }
        }
    }

    @Override
    public void filter(@NonNull RedisTreeItemFilter filter) {
        List<RedisConnectTreeItem> connectedItems = this.getConnectedItems();
        if (CollUtil.isNotEmpty(connectedItems)) {
            List<Runnable> tasks = new ArrayList<>(connectedItems.size());
            for (RedisConnectTreeItem connectedItem : connectedItems) {
                tasks.add(() -> connectedItem.filter(filter));
            }
            // 提交任务
            ThreadUtil.submit(tasks);
        }
    }

    @Override
    public void flushGraphic() {
        if (this.itemValue().graphic() == null) {
            this.itemValue().graphic(new FlexImageView(IconUtil.getIcon(RedisConst.ICON_PATH), 16));
        }
    }

    /**
     * 连接新增事件
     *
     * @param info 连接信息
     */
    @EventReceiver(RedisEventTypes.REDIS_INFO_ADD)
    private void onConnectAdd(RedisInfo info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接信息
     */
    @EventReceiver(RedisEventTypes.REDIS_INFO_UPDATED)
    private void onConnectUpdate(RedisInfo info) {
        ObservableList<BaseTreeItem> items = this.getChildren();
        f1:
        for (BaseTreeItem item : items) {
            if (item instanceof RedisConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof RedisGroupTreeItem groupTreeItem) {
                for (RedisConnectTreeItem connectTreeItem : groupTreeItem.getChildren()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect(@NonNull RedisInfo redisInfo) {
        RedisGroupTreeItem groupTreeItem = this.getGroupItem(redisInfo.getGroupId());
        if (groupTreeItem == null) {
            super.addChild(new RedisConnectTreeItem(redisInfo, this.treeView()));
        } else {
            groupTreeItem.addConnect(redisInfo);
        }
    }

    @Override
    public void addConnectItem(@NonNull RedisConnectTreeItem item) {
        if (this.getChildren().contains(item)) {
            return;
        }
        if (item.value().getGroupId() != null) {
            item.value().setGroupId(null);
            this.infoStore.update(item.value());
        }
        super.addChild(item);
        if (!this.isExpanded()) {
            this.extend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<RedisConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.getChildren().addAll(items);
            this.sort(this.treeView().sortOrder());
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
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildren().size());
        for (BaseTreeItem child : this.getChildren()) {
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
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildren().size());
        for (BaseTreeItem item : this.getChildren()) {
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
}
