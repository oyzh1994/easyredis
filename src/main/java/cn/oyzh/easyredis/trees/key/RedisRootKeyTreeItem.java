package cn.oyzh.easyredis.trees.key;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.data.RedisDataExportController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisRootKeyTreeItem extends RichTreeItem<RedisRootKeyTreeItem.RedisRootTreeItemValue> {

    /**
     * 设置
     */
    private final RedisSetting setting = RedisSettingStore.SETTING;

    public RedisRootKeyTreeItem(@NonNull RedisKeyTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new RedisRootTreeItemValue());
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        // 重载
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        // 卸载
        FXMenuItem unload = MenuItemHelper.unload("12", this::unloadChild);
        // 加载全部
        FXMenuItem loadAll = MenuItemHelper.loadAll("12", this::loadChildAll);
        // 导出数据
        FXMenuItem export = MenuItemHelper.exportData("12", this::exportData);
        items.add(reload);
        items.add(unload);
        items.add(loadAll);
        items.add(export);
        return items;
    }

    /**
     * 导出zk节点
     */
    public void exportData() {
        StageAdapter fxView = StageManager.parseStage(RedisDataExportController.class, this.window());
        fxView.setProp("connect", this.redisConnect());
        fxView.setProp("dbIndex", this.dbIndex());
        fxView.display();
    }

    @Override
    public void reloadChild() {
        this.loadChild();
    }

    private void loadChildAll() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> this.loadChild(0))
                .onError(MessageBox::exception)
                .build();
        this.startWaiting(task);
    }

    /**
     * 取消加载
     */
    public void unloadChild() {
        this.clearChild();
        this.setLoaded(false);
    }

    public void keyAdded(String key) {
        try {
            RedisKeyTreeView treeView = this.getTreeView();
            RedisKey redisKey = treeView == null ? null : RedisKeyUtil.getKey(treeView.dbIndex(), key, false, false, treeView.client());
            if (redisKey == null) {
                JulLog.warn("redisKey is null");
            } else {
                this.addChild(this.initKeyItem(redisKey));
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public void keyDeleted(String key) {
        for (RedisKeyTreeItem keyItem : this.keyChildren()) {
            if (StringUtil.equals(key, keyItem.key())) {
                keyItem.remove();
                break;
            }
        }
    }

    /**
     * 获取当前键节点
     *
     * @return 当前键节点
     */
    public List<RedisKeyTreeItem> keyChildren() {
        return (List) super.unfilteredChildren().filtered(i -> i instanceof RedisKeyTreeItem);
    }

    /**
     * 子节点-更多
     *
     * @return RedisMoreTreeItem
     */
    protected RedisMoreTreeItem moreChildren() {
        List list = super.unfilteredChildren().filtered(e -> e instanceof RedisMoreTreeItem);
        return list.isEmpty() ? null : (RedisMoreTreeItem) list.getFirst();
    }

    @Override
    public RedisKeyTreeView getTreeView() {
        return (RedisKeyTreeView) super.getTreeView();
    }

    public RedisDatabaseTreeItem dbItem() {
        return this.getTreeView().dbItem();
    }

    @Override
    public void loadChild() {
        if (!this.isLoading()) {
            try {
                this.setLoading(true);
                this.loadChild(this.setting.keyLoadLimit());
                this.expend();
            } finally {
                this.setLoading(false);
            }
        }
    }

    /**
     * 加载子节点
     *
     * @param limit 限制数量
     */
    protected void loadChild(int limit) {
        // 当前树
        RedisKeyTreeView treeView = this.getTreeView();
        // 获取选中节点
        TreeItem<?> selectedItem = treeView == null ? null : treeView.getSelectedItem();
        try {
            // 扫描参数
            String pattern = StringUtil.isBlank(this.getFilterPattern()) ? "*" : this.getFilterPattern();
            // 节点列表
            List<RedisKeyTreeItem> itemList = this.keyChildren();
            // 添加列表
            List<TreeItem<?>> addList = new ArrayList<>();
            // 移除列表
            List<TreeItem<?>> delList = new ArrayList<>();
            // 已存在节点
            List<String> existingKeys = itemList.parallelStream().map(RedisKeyTreeItem::key).toList();
            // 获取节点列表
            List<RedisKey> list = RedisKeyUtil.getKeys(this.client(), this.dbIndex(), pattern, existingKeys, limit);
            // 处理节点
            for (RedisKey node : list) {
                // 添加到集合
                addList.add(this.initKeyItem(node));
            }
            // 限制节点加载数量
            if (limit > 0 && !list.isEmpty()) {
                RedisMoreTreeItem moreItem = this.moreChildren();
                if (moreItem != null) {
                    delList.add(moreItem);
                    addList.add(moreItem);
                } else {
                    addList.add(new RedisMoreTreeItem(this.getTreeView()));
                }
            } else {// 处理不限制的情况
                RedisMoreTreeItem moreItem = this.moreChildren();
                if (moreItem != null) {
                    delList.add(moreItem);
                }
            }
            // 删除节点
            this.removeChild(delList);
            // 添加节点
            this.addChild(addList);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.setLoaded(false);
        } finally {
            this.doFilter();
            this.doSort();
            // 选中节点
            if (selectedItem != null) {
                treeView.select(selectedItem);
            }
        }
    }

    private String getFilterPattern() {
        return this.dbItem().getFilterPattern();
    }

    private RedisConnect redisConnect() {
        return this.dbItem().redisConnect();
    }

    private int dbIndex() {
        return this.dbItem().dbIndex();
    }

    private RedisClient client() {
        return this.dbItem().client();
    }

//    @Override
//    public void loadChild() {
//        RedisDatabaseTreeItem dbItem = this.dbItem();
//        // 获取已有子节点
//        List<RedisKeyTreeItem> keyItems = this.keyChildren();
//        // 禁用排序
//        this.setSortable(false);
//        // 当前光标
//        String cursor = null;
//        // 扫描参数
//        String pattern = StringUtil.isBlank(dbItem.getFilterPattern()) ? "*" : dbItem.getFilterPattern();
//        ScanParams params = new ScanParams();
//        params.match(pattern);
//        // 全部节点
//        List<RedisKey> allKeys = new CopyOnWriteArrayList<>();
//        // 数据计数
//        int count = 0;
//        // 扫描数据
//        while (true) {
//            // 计算限制
//            int limit = this.setting.calcLimit(1000, count);
//            // 处理结束
//            if (limit <= 0) {
//                this.renderChild(keyItems, Collections.emptyList(), allKeys, true);
//                break;
//            }
//            // 设置加载数量
//            params.count(limit);
//            // 扫描数据
//            RedisScanResult result = RedisKeyUtil.scanKeys(dbItem.dbIndex(), cursor, params, dbItem.client());
//            // 渲染数据
//            this.renderChild(keyItems, result.getKeys(), allKeys, result.isFinish());
//            // 查询结束
//            if (result.isFinish()) {
//                break;
//            }
//            count += result.keySize();
//            // 更新光标
//            cursor = result.getCursor();
//        }
//    }

    /**
     * 初始化redis树键
     *
     * @param redisKey redis键
     * @return redis树键
     */
    private RedisKeyTreeItem initKeyItem(RedisKey redisKey) {
        if (redisKey.isStringKey()) {
            return new RedisStringKeyTreeItem(redisKey, this.getTreeView());
        }
        if (redisKey.isListKey()) {
            return new RedisListKeyTreeItem(redisKey, this.getTreeView());
        }
        if (redisKey.isSetKey()) {
            return new RedisSetKeyTreeItem(redisKey, this.getTreeView());
        }
        if (redisKey.isZSetKey()) {
            return new RedisZSetKeyTreeItem(redisKey, this.getTreeView());
        }
        if (redisKey.isHashKey()) {
            return new RedisHashKeyTreeItem(redisKey, this.getTreeView());
        }
        if (redisKey.isStreamKey()) {
            return new RedisStreamKeyTreeItem(redisKey, this.getTreeView());
        }
        return null;
    }

//    /**
//     * 渲染子节点
//     *
//     * @param keyItems 所有键节点
//     * @param keys     当前键
//     * @param allKeys  所有键
//     * @param finish   是否结束
//     */
//    private void renderChild(List<RedisKeyTreeItem> keyItems, List<RedisKey> keys, List<RedisKey> allKeys, boolean finish) {
//        allKeys.addAll(keys);
//        // 单次查询数据
//        List<TreeItem<?>> shows = new ArrayList<>(keys.size());
//        for (RedisKey key : keys) {
//            // 数据不存在，则添加到集合
//            Optional<RedisKeyTreeItem> optional = keyItems.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
//            if (optional.isEmpty()) {
//                RedisKeyTreeItem item = this.initKeyItem(key);
//                if (item != null) {
//                    shows.add(item);
//                }
//            }
//        }
//        // 添加不在树的数据
//        if (!shows.isEmpty()) {
//            this.addChild(shows);
//        }
//        // 展开节点
//        this.expend();
//        // 结束处理
//        if (finish) {
//            // 无数据
//            if (allKeys.isEmpty()) {
//                this.clearChild();
//            } else {// 删除不存在的数据
//                List<TreeItem<?>> hides = new ArrayList<>();
//                // 寻找在树，但是不在库的数据
//                for (RedisKeyTreeItem item : keyItems) {
//                    Optional<RedisKey> optional = allKeys.parallelStream().filter(v -> v.key().equals(item.key())).findAny();
//                    if (optional.isEmpty()) {
//                        hides.add(item);
//                    }
//                }
//                // 删除不存在的数据
//                if (!hides.isEmpty()) {
//                    this.removeChild(hides);
//                }
//            }
//            // 启用排序并执行排序
//            allKeys.clear();
//            this.setSortable(true);
//            this.doSort();
//        }
//    }

    public static class RedisRootTreeItemValue extends RichTreeItemValue {

        @Override
        public String name() {
            return I18nHelper.keys();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/key.svg", 10);
                this.graphic.disableTheme();
            }
            return super.graphic();
        }
    }
}
