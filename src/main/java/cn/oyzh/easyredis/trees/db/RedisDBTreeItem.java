package cn.oyzh.easyredis.trees.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.controller.info.RedisInfoTransportController;
import cn.oyzh.easyredis.controller.key.RedisKeyAddController;
import cn.oyzh.easyredis.controller.key.RedisKeyBatchOperationController;
import cn.oyzh.easyredis.controller.key.RedisKeyExportController;
import cn.oyzh.easyredis.controller.key.RedisKeyFilterController;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.redis.RedisScanResult;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.redis.key.RedisHyperLogLogKey;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.easyredis.trees.hylog.RedisHyLogKeyTreeItem;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.type.RedisTypeTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import redis.clients.jedis.params.ScanParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * redis数据库树节点
 *
 * @author oyzh
 * @since 2023/07/12
 */
public class RedisDBTreeItem extends RedisTreeItem<RedisDBTreeItemValue> {

    /**
     * 当前db索引
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final int dbIndex;

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final String value;

    /**
     * 键加载标志位
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private boolean nodeLoaded;

    /**
     * 键过滤模式
     */
    private String keyFilterPattern;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected RedisConnectTreeItem parent;

    public RedisDBTreeItem(Integer dbIndex, RedisConnectTreeItem parent) {
        super(parent.getTreeView());
        this.setFilterable(true);
        this.parent = parent;
        this.dbIndex = dbIndex == null ? 0 : dbIndex;
        this.value = dbIndex == null ? "键列表" : "db" + dbIndex;
        this.setValue(new RedisDBTreeItemValue(this));
        this.initTypes();
        this.flushValue();
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    /**
     * 初始化类型
     */
    private void initTypes() {
        List<TreeItem<?>> typeItems = new ArrayList<>();
        for (RedisKeyType keyType : RedisKeyType.values()) {
            typeItems.add(new RedisTypeTreeItem(this, keyType));
        }
        super.setChild(typeItems);
    }

    /**
     * 刷新值
     */
    private void flushValue() {
        BackgroundService.submitFXLater(() -> {
            // if (!this.isSentinelMode()) {
            //     this.getValue().flushNum(this.client().dbSize(this.dbIndex), this.getChildren().size());
            // }
            this.getValue().filterPattern(this.keyFilterPattern);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItemExt add = MenuItemExt.newItem("添加新键", new SVGGlyph("/font/add.svg", "12"), "添加redis键", this::addNode);
        MenuItemExt keyFilter = MenuItemExt.newItem("键过滤器", new SVGGlyph("/font/filter.svg", "12"), "过滤redis键", this::keyFilter);
        MenuItemExt reload = MenuItemExt.newItem("重新载入", new SVGGlyph("/font/reload.svg", "12"), "重新加载redis键", this::reloadChild);
        MenuItemExt exportData = MenuItemExt.newItem("导出数据", new SVGGlyph("/font/export.svg", "12"), "导出redis数据", this::exportNode);
        MenuItemExt transportData = MenuItemExt.newItem("传输数据", new SVGGlyph("/font/arrow-left-right-line.svg", "12"), "传输redis数据", this::transportData);
        // MenuItemExt flushDB = MenuItemExt.newItem("清空数据", new SVGGlyph("/font/clear.svg", "12"), "清空此数据库所有数据", this::flushDB);
        MenuItemExt batchOperation = MenuItemExt.newItem("批量操作", new SVGGlyph("/font/mml-batch-command-16.svg", "12"), "批量操作数据", this::batchOperation);

        items.add(add);
        items.add(keyFilter);
        items.add(reload);
        items.add(exportData);
        items.add(transportData);
        // items.add(flushDB);
        items.add(batchOperation);
        return items;
    }

    /**
     * 批量操作
     */
    @FXML
    private void batchOperation() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyBatchOperationController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
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
     * 键过滤
     */
    private void keyFilter() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyFilterController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.setProp("pattern", this.keyFilterPattern);
        fxView.display();
    }

    /**
     * 执行键过滤
     *
     * @param pattern 模式
     */
    public void doKeyFilter(String pattern) {
        if (!StrUtil.equals(this.keyFilterPattern, pattern)) {
            this.keyFilterPattern = pattern;
            this.reloadChild();
        }
    }

    /**
     * 导出键
     */
    public void exportNode() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyExportController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    // /**
    //  * 清空数据库
    //  */
    // private void flushDB() {
    //     if (!MessageBox.confirm("第1次确认，共2次", "确定清空此数据库所有数据？")) {
    //         return;
    //     }
    //     if (!MessageBox.confirm("第2次确认，共2次", "请慎重操作，确定清空此数据库所有数据？")) {
    //         return;
    //     }
    //     try {
    //         this.client().flushDB(this.dbIndex);
    //         this.clearChild();
    //         // this.flushItemValue();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //         MessageBox.exception(ex);
    //     }
    // }

    @Override
    public void reloadChild() {
        this.nodeLoaded = false;
        this._loadChild();
    }

    @Override
    public synchronized void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.flushValue();
    }

    /**
     * cluster模式加载子节点
     */
    private void loadChildByCluster() {
        // 获取已有子节点
        List<RedisKeyTreeItem<?, ?>> items = this.keyItems();
        // 查询数据
        String pattern = StrUtil.isBlank(this.keyFilterPattern) ? "*" : this.keyFilterPattern;
        List<RedisKey> dbKeys = RedisKeyUtil.allNodes(this.dbIndex, pattern, false, this.client());
        if (CollUtil.isNotEmpty(dbKeys)) {
            List<TreeItem<?>> shows = new ArrayList<>(dbKeys.size());
            List<TreeItem<?>> hides = new ArrayList<>(dbKeys.size());
            for (RedisKey key : dbKeys) {
                // 数据不存在，则添加到集合
                Optional<RedisKeyTreeItem<?, ?>> optional = items.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
                if (optional.isEmpty()) {
                    RedisKeyTreeItem<?, ?> item = this.initItemByNode(key);
                    if (item != null) {
                        shows.add(item);
                    }
                }
            }
            // 寻找在树，但是不在库的数据
            for (RedisKeyTreeItem<?, ?> item : items) {
                Optional<RedisKey> optional = dbKeys.parallelStream().filter(v -> v.key().equals(item.key())).findAny();
                if (optional.isEmpty()) {
                    hides.add(item);
                }
            }
            // 添加不在树的数据
            if (!shows.isEmpty()) {
                this.addChild(shows);
            }
            // 删除不在库的数据
            if (!hides.isEmpty()) {
                this.removeChild(hides);
            }
            // 展开节点
            this.extend();
        } else {
            // 清除节点
            this.clearChild();
        }
    }

    /**
     * 正常模式加载子节点
     */
    private void loadChildByNormal() {
        // 获取已有子节点
        List<RedisKeyTreeItem<?, ?>> items = this.keyItems();
        // 禁用排序
        this.disableSort();
        // 是否为空
        String cursor = null;
        String pattern = StrUtil.isBlank(this.keyFilterPattern) ? "*" : this.keyFilterPattern;
        ScanParams params = new ScanParams();
        params.count(20);
        params.match(pattern);
        // 库表节点
        List<RedisKey> dbKeys = new CopyOnWriteArrayList<>();
        // 统计工具
        StopWatch watch = new StopWatch();
        // 渲染线程池
        while (true) {
            watch.start("scan nodes");
            RedisScanResult result = RedisKeyUtil.scanNodes(this.dbIndex, cursor, params, this.client());
            // 数据为空
            List<RedisKey> keys = result.getKeys();
            watch.stop();
            StaticLog.info(watch.prettyPrint(TimeUnit.MILLISECONDS));
            // 处理键
            if (CollUtil.isNotEmpty(keys)) {
                watch.start("render nodes");
                // 单次查询数据
                List<TreeItem<?>> shows = new ArrayList<>(keys.size());
                for (RedisKey key : keys) {
                    // 数据不存在，则添加到集合
                    Optional<RedisKeyTreeItem<?, ?>> optional = items.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
                    if (optional.isEmpty()) {
                        RedisKeyTreeItem<?, ?> item = this.initItemByNode(key);
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
                this.extend();
                // 添加到集合
                dbKeys.addAll(keys);
                watch.stop();
                StaticLog.info(watch.prettyPrint(TimeUnit.MILLISECONDS));
            }
            // 查询结束
            if (result.isFinish()) {
                break;
            }
            // 更新光标
            cursor = result.getCursor();
        }
        // 无数据
        if (dbKeys.isEmpty()) {
            this.clearChild();
        } else {// 删除不存在的数据
            List<TreeItem<?>> hides = new ArrayList<>();
            // 寻找在树，但是不在库的数据
            for (RedisKeyTreeItem<?, ?> item : items) {
                Optional<RedisKey> optional = dbKeys.parallelStream().filter(v -> v.key().equals(item.key())).findAny();
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
        this.enableSort();
        this.sort();
    }

    @Override
    public synchronized void addChild(@NonNull List<TreeItem<?>> items) {
        List<TreeItem<?>> list = new ArrayList<>();
        List<TreeItem<?>> hash = new ArrayList<>();
        List<TreeItem<?>> set = new ArrayList<>();
        List<TreeItem<?>> zset = new ArrayList<>();
        List<TreeItem<?>> string = new ArrayList<>();
        List<TreeItem<?>> stream = new ArrayList<>();
        List<TreeItem<?>> hyLog = new ArrayList<>();
        for (TreeItem<?> item : items) {
            if (item instanceof RedisStringKeyTreeItem treeItem) {
                string.add(treeItem);
            } else if (item instanceof RedisListKeyTreeItem treeItem) {
                list.add(treeItem);
            } else if (item instanceof RedisSetKeyTreeItem treeItem) {
                set.add(treeItem);
            } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
                zset.add(treeItem);
            } else if (item instanceof RedisHashKeyTreeItem treeItem) {
                hash.add(treeItem);
            } else if (item instanceof RedisStreamKeyTreeItem treeItem) {
                stream.add(treeItem);
            } else if (item instanceof RedisHyLogKeyTreeItem treeItem) {
                hyLog.add(treeItem);
            }
        }
        for (RedisTypeTreeItem child : this.children()) {
            if (child.value() == RedisKeyType.STRING) {
                child.addChild(string);
            } else if (child.value() == RedisKeyType.HASH) {
                child.addChild(hash);
            } else if (child.value() == RedisKeyType.LIST) {
                child.addChild(list);
            } else if (child.value() == RedisKeyType.SET) {
                child.addChild(set);
            } else if (child.value() == RedisKeyType.ZSET) {
                child.addChild(zset);
            } else if (child.value() == RedisKeyType.HYPERLOGLOG) {
                child.addChild(hyLog);
            } else if (child.value() == RedisKeyType.STREAM) {
                child.addChild(stream);
            }
        }
    }

    @Override
    public synchronized void removeChild(@NonNull List<TreeItem<?>> items) {
        List<TreeItem<?>> list = new ArrayList<>();
        List<TreeItem<?>> hash = new ArrayList<>();
        List<TreeItem<?>> set = new ArrayList<>();
        List<TreeItem<?>> zset = new ArrayList<>();
        List<TreeItem<?>> string = new ArrayList<>();
        List<TreeItem<?>> stream = new ArrayList<>();
        List<TreeItem<?>> hyLog = new ArrayList<>();
        for (TreeItem<?> item : items) {
            if (item instanceof RedisStringKeyTreeItem treeItem) {
                string.add(treeItem);
            } else if (item instanceof RedisListKeyTreeItem treeItem) {
                list.add(treeItem);
            } else if (item instanceof RedisSetKeyTreeItem treeItem) {
                set.add(treeItem);
            } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
                zset.add(treeItem);
            } else if (item instanceof RedisHashKeyTreeItem treeItem) {
                hash.add(treeItem);
            } else if (item instanceof RedisStreamKeyTreeItem treeItem) {
                stream.add(treeItem);
            } else if (item instanceof RedisHyLogKeyTreeItem treeItem) {
                hyLog.add(treeItem);
            }
        }
        for (RedisTypeTreeItem child : this.children()) {
            if (child.value() == RedisKeyType.STRING) {
                child.removeChild(string);
            } else if (child.value() == RedisKeyType.HASH) {
                child.removeChild(hash);
            } else if (child.value() == RedisKeyType.LIST) {
                child.removeChild(list);
            } else if (child.value() == RedisKeyType.SET) {
                child.removeChild(set);
            } else if (child.value() == RedisKeyType.ZSET) {
                child.removeChild(zset);
            } else if (child.value() == RedisKeyType.HYPERLOGLOG) {
                child.removeChild(hyLog);
            } else if (child.value() == RedisKeyType.STREAM) {
                child.removeChild(stream);
            }
        }
    }

    @Override
    public void clearChild() {
        for (RedisTypeTreeItem child : this.children()) {
            child.clearChild();
        }
    }

    public List<RedisTypeTreeItem> children() {
        return super.getChildren();
    }

    /**
     * 获取键节点
     *
     * @return 键节点列表
     */
    public List<RedisKeyTreeItem<?, ?>> keyItems() {
        // 获取已有子节点
        List<RedisKeyTreeItem<?, ?>> items = new CopyOnWriteArrayList<>();
        for (RichTreeItem<?> item : this.getRichChildren()) {
            if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    /**
     * 加载子节点实际业务
     */
    private void _loadChild() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    if (this.isClusterMode()) {
                        this.loadChildByCluster();
                    } else {
                        this.loadChildByNormal();
                    }
                })
                .onError(ex -> {
                    this.nodeLoaded = false;
                    MessageBox.exception(ex);
                })
                // .onSuccess(this::flushValue)
                .onFinish(this::stopWaiting)
                .build();
        // 执行业务
        this.startWaiting(task);
    }

    /**
     * 是否cluster集群模式
     *
     * @return 结果
     */
    public boolean isClusterMode() {
        return this.client().isClusterMode();
    }

    /**
     * 是否哨兵模式
     *
     * @return 结果
     */
    public boolean isSentinelMode() {
        return this.client().isSentinelMode();
    }

    /**
     * 加载子节点
     */
    public void loadChild() {
        if (!this.isWaiting() && (!this.nodeLoaded)) {
            this.nodeLoaded = true;
            this._loadChild();
        }
    }

    /**
     * 初始化redis树键
     *
     * @param node redis键
     * @return redis树键
     */
    private RedisKeyTreeItem<?, ?> initItemByNode(RedisKey node) {
        if (node instanceof RedisStringKey stringNode) {
            return new RedisStringKeyTreeItem(stringNode, this);
        }

        if (node instanceof RedisListKey listNode) {
            return new RedisListKeyTreeItem(listNode, this);
        }

        if (node instanceof RedisSetKey setNode) {
            return new RedisSetKeyTreeItem(setNode, this);
        }

        if (node instanceof RedisZSetKey zSetNode) {
            return new RedisZSetKeyTreeItem(zSetNode, this);
        }

        if (node instanceof RedisHashKey hashNode) {
            return new RedisHashKeyTreeItem(hashNode, this);
        }

        if (node instanceof RedisHyperLogLogKey logLogNode) {
            return new RedisHyLogKeyTreeItem(logLogNode, this);
        }

        if (node instanceof RedisStreamKey streamNode) {
            return new RedisStreamKeyTreeItem(streamNode, this);
        }

        return null;
    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.parent.client();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public RedisInfo info() {
        return this.parent.value();
    }

    /**
     * 添加键
     */
    public void addNode() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyAddController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }
}
