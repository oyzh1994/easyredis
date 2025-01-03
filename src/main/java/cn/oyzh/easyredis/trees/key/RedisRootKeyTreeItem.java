package cn.oyzh.easyredis.trees.key;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.IRunnable;
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
        this.setValue(new RedisRootTreeItemValue(this));
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
        if (!this.isLoaded() && !this.isLoading()) {
            Task task = TaskBuilder.newBuilder()
                    .onFinish(() -> this.setLoading(false))
                    .onStart(() -> {
                        this.setLoaded(true);
                        this.setLoading(true);
                        this.loadChild(0);
                    })
                    .onError(err -> {
                        this.setLoaded(false);
                        MessageBox.exception(err);
                    })
                    .build();
            this.startWaiting(task);
        }
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
     * 获取当前键节点数量
     *
     * @return 当前键节点
     */
    public int keyChildrenSize() {
        return super.getChildren().filtered(i -> i instanceof RedisKeyTreeItem).size();
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
            IRunnable func = () -> {
                try {
                    this.setLoaded(true);
                    this.setLoading(true);
                    this.loadChild(this.setting.keyLoadLimit());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.setLoaded(false);
                }
            };
            Task task = TaskBuilder.newBuilder()
                    .onStart(func)
                    .onSuccess(this::expend)
                    .onFinish(() -> this.setLoading(false))
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
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

    @Override
    public void onPrimaryDoubleClick() {
        if (this.isLoaded()) {
            super.onPrimaryDoubleClick();
        } else {
            this.loadChild();
        }
    }

    public static class RedisRootTreeItemValue extends RichTreeItemValue {

        public RedisRootTreeItemValue(RedisRootKeyTreeItem item) {
            super(item);
        }

        @Override
        protected RedisRootKeyTreeItem item() {
            return (RedisRootKeyTreeItem) super.item();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/key.svg", 10);
                this.graphic.disableTheme();
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.keys();
        }

        @Override
        public String extra() {
            int size = this.item().keyChildrenSize();
            return "(" + size + ")";
        }
    }
}
