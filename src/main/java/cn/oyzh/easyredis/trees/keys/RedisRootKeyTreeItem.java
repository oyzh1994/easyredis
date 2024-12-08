package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.redis.batch.RedisScanResult;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisSettingJdbcStore;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;
import lombok.NonNull;
import redis.clients.jedis.params.ScanParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisRootKeyTreeItem extends RichTreeItem<RedisRootKeyTreeItem.RedisUnnamedTreeItemValue> {
    /**
     * 设置
     */
    private final RedisSetting setting = RedisSettingJdbcStore.SETTING;

    public RedisRootKeyTreeItem(@NonNull RedisKeysTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new RedisUnnamedTreeItemValue());
    }

    public void keyAdded(String key) {
        RedisKeysTreeView treeView = this.getTreeView();
        RedisKey redisKey = RedisKeyUtil.getKey(treeView.dbIndex(), key, false, false, treeView.client());
        if (redisKey == null) {
            JulLog.warn("redisKey is null");
        } else {
            this.addChild(this.initItemByNode(redisKey));
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

    public static class RedisUnnamedTreeItemValue extends RichTreeItemValue {

        @Override
        public String name() {
            return I18nHelper.keys();
        }
    }

    /**
     * 获取当前键节点
     *
     * @return 当前键节点
     */
    public List<RedisKeyTreeItem> keyChildren() {
        return (List) super.unfilteredChildren();
    }

    @Override
    public RedisKeysTreeView getTreeView() {
        return (RedisKeysTreeView) super.getTreeView();
    }

    public RedisDatabaseTreeItem dbItem() {
        return this.getTreeView().dbItem();
    }

    @Override
    public void loadChild() {
        RedisDatabaseTreeItem dbItem = this.dbItem();
        // 获取已有子节点
        List<RedisKeyTreeItem> keyItems = this.keyChildren();
        // 禁用排序
        this.setSortable(false);
        // 当前光标
        String cursor = null;
        // 扫描参数
        String pattern = StringUtil.isBlank(dbItem.getFilterPattern()) ? "*" : dbItem.getFilterPattern();
        ScanParams params = new ScanParams();
        params.match(pattern);
        // 全部节点
        List<RedisKey> allKeys = new CopyOnWriteArrayList<>();
        // 数据计数
        int count = 0;
        // 扫描数据
        while (true) {
            // 计算限制
            int limit = this.setting.calcLimit(1000, count);
            // 处理结束
            if (limit <= 0) {
                FXUtil.runWait(() -> this.renderChild(keyItems, Collections.emptyList(), allKeys, true));
                break;
            }
            // 设置加载数量
            params.count(limit);
            // 扫描数据
            RedisScanResult result = RedisKeyUtil.scanKeys(dbItem.dbIndex(), cursor, params, dbItem.client());
            // 渲染数据
            FXUtil.runWait(() -> this.renderChild(keyItems, result.getKeys(), allKeys, result.isFinish()));
            // 查询结束
            if (result.isFinish()) {
                break;
            }
            count += result.keySize();
            // 更新光标
            cursor = result.getCursor();
        }
    }

    /**
     * 初始化redis树键
     *
     * @param node redis键
     * @return redis树键
     */
    private RedisKeyTreeItem initItemByNode(RedisKey node) {
        if (node.isStringKey()) {
            return new RedisStringKeyTreeItem(node, this.getTreeView());
        }
        if (node.isListKey()) {
            return new RedisListKeyTreeItem(node, this.getTreeView());
        }
        if (node.isSetKey()) {
            return new RedisSetKeyTreeItem(node, this.getTreeView());
        }
        if (node.isZSetKey()) {
            return new RedisZSetKeyTreeItem(node, this.getTreeView());
        }
        if (node.isHashKey()) {
            return new RedisHashKeyTreeItem(node, this.getTreeView());
        }
        if (node.isStreamKey()) {
            return new RedisStreamKeyTreeItem(node, this.getTreeView());
        }
        return null;
    }

    /**
     * 渲染子节点
     *
     * @param keyItems 所有键节点
     * @param keys     当前键
     * @param allKeys  所有键
     * @param finish   是否结束
     */
    private void renderChild(List<RedisKeyTreeItem> keyItems, List<RedisKey> keys, List<RedisKey> allKeys, boolean finish) {
        allKeys.addAll(keys);
        // 单次查询数据
        List<TreeItem<?>> shows = new ArrayList<>(keys.size());
        for (RedisKey key : keys) {
            // 数据不存在，则添加到集合
            Optional<RedisKeyTreeItem> optional = keyItems.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
            if (optional.isEmpty()) {
                RedisKeyTreeItem item = this.initItemByNode(key);
                if (item != null) {
                    shows.add(item);
                }
            }
        }
        // 添加不在树的数据
        if (!shows.isEmpty()) {
            this.addChild(shows);
        }
        // 展开节点
        this.expend();
        // 结束处理
        if (finish) {
            // 无数据
            if (allKeys.isEmpty()) {
                this.clearChild();
            } else {// 删除不存在的数据
                List<TreeItem<?>> hides = new ArrayList<>();
                // 寻找在树，但是不在库的数据
                for (RedisKeyTreeItem item : keyItems) {
                    Optional<RedisKey> optional = allKeys.parallelStream().filter(v -> v.key().equals(item.key())).findAny();
                    if (optional.isEmpty()) {
                        hides.add(item);
                    }
                }
                // 删除不存在的数据
                if (!hides.isEmpty()) {
                    this.removeChild(hides);
                }
            }
            // 启用排序并执行排序
            allKeys.clear();
            // this.getValue().flushNum();
            this.setSortable(true);
            this.sort();
        }
    }
}
