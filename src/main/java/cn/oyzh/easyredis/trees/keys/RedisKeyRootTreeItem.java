package cn.oyzh.easyredis.trees.keys;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.redis.batch.RedisScanResult;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.store.RedisSettingJdbcStore;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.FXUtil;
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
public class RedisKeyRootTreeItem extends RichTreeItem<RedisKeyRootTreeItem.RedisUnnamedTreeItemValue> {
    /**
     * 设置
     */
    private final RedisSetting setting = RedisSettingJdbcStore.SETTING;

    public RedisKeyRootTreeItem(@NonNull RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisUnnamedTreeItemValue());
    }

    public static class RedisUnnamedTreeItemValue extends RichTreeItemValue {

        @Override
        public String name() {
            return "";
        }
    }

    /**
     * 获取当前键节点
     *
     * @return 当前键节点
     */
    public List<RedisKeyTreeItem<?>> keyChildren() {
        // // 获取已有子节点
        // List<RedisKeyTreeItem<?, ?>> items = new CopyOnWriteArrayList<>();
        // for (RedisTypeTreeItem item : this.realChildren()) {
        //     items.addAll((List) item.unfilteredChildren());
        // }
        // return items;
        List list = super.unfilteredChildren();
        return list;
    }

    public void loadItems(RedisDatabaseTreeItem dbTreeItem) {
        this.setLoaded(true);
        this.setLoading(true);
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    this.loadChild1(dbTreeItem);
                })
                .onError(ex -> {
                    this.setLoaded(false);
                    MessageBox.exception(ex);
                })
                .onSuccess(this::refresh)
                .onFinish(() -> this.setLoading(false))
                .build();
        // 执行业务
        this.startWaiting(task);
    }

    /**
     * 正常模式加载子节点
     */
    public void loadChild1(RedisDatabaseTreeItem dbTreeItem) {
        // 获取已有子节点
        List<RedisKeyTreeItem<?>> keyItems = this.keyChildren();
        // 禁用排序
        this.setSortable(false);
        // 当前光标
        String cursor = null;
        // 扫描参数
        String pattern = StrUtil.isBlank(dbTreeItem.getFilterPattern()) ? "*" : dbTreeItem.getFilterPattern();
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
                FXUtil.runWait(() -> this.renderChild(dbTreeItem, keyItems, Collections.emptyList(), allKeys, true));
                break;
            }
            // 设置加载数量
            params.count(limit);
            // 扫描数据
            RedisScanResult result = RedisKeyUtil.scanKeys(dbTreeItem.dbIndex(), cursor, params, dbTreeItem.client());
            // 渲染数据
            FXUtil.runWait(() -> this.renderChild(dbTreeItem, keyItems, result.getKeys(), allKeys, result.isFinish()));
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
    private RedisKeyTreeItem<?> initItemByNode(RedisKey node, RedisDatabaseTreeItem dbTreeItem) {
        if (node instanceof RedisStringKey stringNode) {
            return new RedisStringKeyTreeItem(stringNode, dbTreeItem);
        }

        if (node instanceof RedisListKey listNode) {
            return new RedisListKeyTreeItem(listNode, dbTreeItem);
        }

        if (node instanceof RedisSetKey setNode) {
            return new RedisSetKeyTreeItem(setNode, dbTreeItem);
        }

        if (node instanceof RedisZSetKey zSetNode) {
            return new RedisZSetKeyTreeItem(zSetNode, dbTreeItem);
        }

        if (node instanceof RedisHashKey hashNode) {
            return new RedisHashKeyTreeItem(hashNode, dbTreeItem);
        }

        if (node instanceof RedisStreamKey streamNode) {
            return new RedisStreamKeyTreeItem(streamNode, dbTreeItem);
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
    private void renderChild(RedisDatabaseTreeItem dbTreeItem, List<RedisKeyTreeItem<?>> keyItems, List<RedisKey> keys, List<RedisKey> allKeys, boolean finish) {
        allKeys.addAll(keys);
        // 单次查询数据
        List<TreeItem<?>> shows = new ArrayList<>(keys.size());
        for (RedisKey key : keys) {
            // 数据不存在，则添加到集合
            Optional<RedisKeyTreeItem<?>> optional = keyItems.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
            if (optional.isEmpty()) {
                RedisKeyTreeItem<?> item = this.initItemByNode(key, dbTreeItem);
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
                for (RedisKeyTreeItem<?> item : keyItems) {
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
