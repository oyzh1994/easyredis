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
import cn.oyzh.easyredis.redis.batch.RedisScanResult;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
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
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.easyredis.trees.type.RedisTypeTreeItem;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.AddKeyMenuItem;
import cn.oyzh.fx.plus.menu.BatchOperationMenuItem;
import cn.oyzh.fx.plus.menu.ExportDataMenuItem;
import cn.oyzh.fx.plus.menu.KeyFilterMenuItem;
import cn.oyzh.fx.plus.menu.ReloadDataMenuItem;
import cn.oyzh.fx.plus.menu.TransportDataMenuItem;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.thread.RenderService;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    @Getter
    private String filterPattern;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected RedisConnectTreeItem parent;

    public RedisDBTreeItem(Integer dbIndex, RedisConnectTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.dbIndex = dbIndex == null ? 0 : dbIndex;
        this.value = dbIndex == null ? I18nHelper.keys() : "db" + dbIndex;
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
            this.getValue().flushNum();
            this.getValue().flushFilterPattern();
        });
    }

    /**
     * 获取当前键数量
     *
     * @return 当前键数量
     */
    public Long dbSize() {
        if (!this.isSentinelMode()) {
            return this.client().dbSize(this.dbIndex);
        }
        return null;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        AddKeyMenuItem add = new AddKeyMenuItem("12", this::addKey);
        KeyFilterMenuItem keyFilter = new KeyFilterMenuItem("12", this::keyFilter);
        ReloadDataMenuItem reload = new ReloadDataMenuItem("12", this::reloadChild);
        ExportDataMenuItem exportData = new ExportDataMenuItem("12", this::exportNode);
        TransportDataMenuItem transportData = new TransportDataMenuItem("12", this::transportData);
        BatchOperationMenuItem batchOperation = new BatchOperationMenuItem("12", this::batchOperation);

        items.add(add);
        items.add(keyFilter);
        items.add(reload);
        items.add(exportData);
        items.add(transportData);
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
        fxView.setProp("pattern", this.filterPattern);
        fxView.display();
    }

    /**
     * 执行键过滤
     *
     * @param pattern 模式
     */
    public void doKeyFilter(String pattern) {
        if (!StrUtil.equals(this.filterPattern, pattern)) {
            this.filterPattern = pattern;
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
        List<RedisKeyTreeItem<?, ?>> items = this.keyChildren();
        // 查询数据
        String pattern = StrUtil.isBlank(this.filterPattern) ? "*" : this.filterPattern;
        List<RedisKey> dbKeys = RedisKeyUtil.allKeys(this.dbIndex, pattern, this.client());
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
        List<RedisKeyTreeItem<?, ?>> keyItems = this.keyChildren();
        // 禁用排序
        this.setSortable(false);
        // 当前光标
        String cursor = null;
        // 扫描参数
        String pattern = StrUtil.isBlank(this.filterPattern) ? "*" : this.filterPattern;
        ScanParams params = new ScanParams();
        params.count(500);
        params.match(pattern);
        // 全部节点
        List<RedisKey> allKeys = new CopyOnWriteArrayList<>();
        // 统计工具
        StopWatch scanWatch = new StopWatch("scan");
        StopWatch renderWatch = new StopWatch("render");
        // 扫描数据
        while (true) {
            scanWatch.start("scan nodes");
            // 扫描数据
            RedisScanResult result = RedisKeyUtil.scanKeys(this.dbIndex, cursor, params, this.client());
            scanWatch.stop();
            StaticLog.info(scanWatch.prettyPrint(TimeUnit.MILLISECONDS));
            // 渲染数据
            RenderService.submit(() -> this.renderChild(renderWatch, keyItems, result.getKeys(), allKeys, result.isFinish()));
            // 查询结束
            if (result.isFinish()) {
                break;
            }
            // 更新光标
            cursor = result.getCursor();
        }
    }

    /**
     * 渲染子节点
     *
     * @param renderWatch 统计工具
     * @param keyItems    所有键节点
     * @param keys        当前键
     * @param allKeys     所有键
     * @param finish      是否结束
     */
    private void renderChild(StopWatch renderWatch, List<RedisKeyTreeItem<?, ?>> keyItems, List<RedisKey> keys, List<RedisKey> allKeys, boolean finish) {
        allKeys.addAll(keys);
        renderWatch.start("render nodes");
        // 单次查询数据
        List<TreeItem<?>> shows = new ArrayList<>(keys.size());
        for (RedisKey key : keys) {
            // 数据不存在，则添加到集合
            Optional<RedisKeyTreeItem<?, ?>> optional = keyItems.parallelStream().filter(v -> v.key().equals(key.key())).findAny();
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
        renderWatch.stop();
        StaticLog.info(renderWatch.prettyPrint(TimeUnit.MILLISECONDS));
        // 结束处理
        if (finish) {
            // 无数据
            if (allKeys.isEmpty()) {
                this.clearChild();
            } else {// 删除不存在的数据
                List<TreeItem<?>> hides = new ArrayList<>();
                // 寻找在树，但是不在库的数据
                for (RedisKeyTreeItem<?, ?> item : keyItems) {
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
            this.setSortable(true);
            this.sort();
        }
    }

    @Override
    public synchronized void addChild(TreeItem<?> item) {
        if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
            for (RedisTypeTreeItem child : this.realChildren()) {
                if (child.value() == treeItem.type()) {
                    child.addChild(treeItem);
                    break;
                }
            }
        }
    }

    @Override
    public synchronized void addChild(@NonNull List<TreeItem<?>> items) {
        List<TreeItem<?>> list = null, string = null, hash = null, set = null, zset = null, stream = null;
        for (TreeItem<?> item : items) {
            if (item instanceof RedisStringKeyTreeItem treeItem) {
                if (string == null) {
                    string = new ArrayList<>();
                }
                string.add(treeItem);
            } else if (item instanceof RedisListKeyTreeItem treeItem) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(treeItem);
            } else if (item instanceof RedisSetKeyTreeItem treeItem) {
                if (set == null) {
                    set = new ArrayList<>();
                }
                set.add(treeItem);
            } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
                if (zset == null) {
                    zset = new ArrayList<>();
                }
                zset.add(treeItem);
            } else if (item instanceof RedisHashKeyTreeItem treeItem) {
                if (hash == null) {
                    hash = new ArrayList<>();
                }
                hash.add(treeItem);
            } else if (item instanceof RedisStreamKeyTreeItem treeItem) {
                if (stream == null) {
                    stream = new ArrayList<>();
                }
                stream.add(treeItem);
            }
        }
        for (RedisTypeTreeItem child : this.realChildren()) {
            if (child.value() == RedisKeyType.STRING) {
                if (CollUtil.isNotEmpty(string)) {
                    child.addChild(string);
                }
            } else if (child.value() == RedisKeyType.HASH) {
                if (CollUtil.isNotEmpty(hash)) {
                    child.addChild(hash);
                }
            } else if (child.value() == RedisKeyType.LIST) {
                if (CollUtil.isNotEmpty(list)) {
                    child.addChild(list);
                }
            } else if (child.value() == RedisKeyType.SET) {
                if (CollUtil.isNotEmpty(set)) {
                    child.addChild(set);
                }
            } else if (child.value() == RedisKeyType.ZSET) {
                if (CollUtil.isNotEmpty(zset)) {
                    child.addChild(zset);
                }
            } else if (child.value() == RedisKeyType.STREAM) {
                if (CollUtil.isNotEmpty(stream)) {
                    child.addChild(stream);
                }
            }
        }
    }

    @Override
    public synchronized void removeChild(TreeItem<?> item) {
        if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
            for (RedisTypeTreeItem child : this.realChildren()) {
                if (child.value() == treeItem.type()) {
                    child.removeChild(treeItem);
                    break;
                }
            }
        }
    }

    @Override
    public synchronized void removeChild(@NonNull List<TreeItem<?>> items) {
        List<TreeItem<?>> list = null, string = null, hash = null, set = null, zset = null, stream = null, hyLog = null;
        for (TreeItem<?> item : items) {
            if (item instanceof RedisStringKeyTreeItem treeItem) {
                if (string == null) {
                    string = new ArrayList<>();
                }
                string.add(treeItem);
            } else if (item instanceof RedisListKeyTreeItem treeItem) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(treeItem);
            } else if (item instanceof RedisSetKeyTreeItem treeItem) {
                if (set == null) {
                    set = new ArrayList<>();
                }
                set.add(treeItem);
            } else if (item instanceof RedisZSetKeyTreeItem treeItem) {
                if (zset == null) {
                    zset = new ArrayList<>();
                }
                zset.add(treeItem);
            } else if (item instanceof RedisHashKeyTreeItem treeItem) {
                if (hash == null) {
                    hash = new ArrayList<>();
                }
                hash.add(treeItem);
            } else if (item instanceof RedisStreamKeyTreeItem treeItem) {
                if (stream == null) {
                    stream = new ArrayList<>();
                }
                stream.add(treeItem);
            }
        }
        for (RedisTypeTreeItem child : this.realChildren()) {
            if (child.value() == RedisKeyType.STRING) {
                if (CollUtil.isNotEmpty(string)) {
                    child.removeChild(string);
                }
            } else if (child.value() == RedisKeyType.HASH) {
                if (CollUtil.isNotEmpty(hash)) {
                    child.removeChild(hash);
                }
            } else if (child.value() == RedisKeyType.LIST) {
                if (CollUtil.isNotEmpty(list)) {
                    child.removeChild(list);
                }
            } else if (child.value() == RedisKeyType.SET) {
                if (CollUtil.isNotEmpty(set)) {
                    child.removeChild(set);
                }
            } else if (child.value() == RedisKeyType.ZSET) {
                if (CollUtil.isNotEmpty(zset)) {
                    child.removeChild(zset);
                }
            } else if (child.value() == RedisKeyType.STREAM) {
                if (CollUtil.isNotEmpty(stream)) {
                    child.removeChild(stream);
                }
            }
        }
    }

    @Override
    public void clearChild() {
        for (RedisTypeTreeItem child : this.realChildren()) {
            child.clearChild();
        }
    }

    /**
     * 获取真实子节点
     *
     * @return 真实子节点
     */
    public List<RedisTypeTreeItem> realChildren() {
        return (List) super.getRealChildren();
    }

    /**
     * 获取当前键节点
     *
     * @return 当前键节点
     */
    public List<RedisKeyTreeItem<?, ?>> keyChildren() {
        // 获取已有子节点
        List<RedisKeyTreeItem<?, ?>> items = new CopyOnWriteArrayList<>();
        for (RedisTypeTreeItem item : this.realChildren()) {
            items.addAll((List) item.getRealChildren());
        }
        return items;
    }

    /**
     * 键节点是否为空
     *
     * @return 结果
     */
    public boolean isKeyEmpty() {
        for (RedisTypeTreeItem item : this.realChildren()) {
            if (!item.getRealChildren().isEmpty()) {
                return false;
            }
        }
        return true;
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
                .onSuccess(this::flushValue)
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
    public void addKey() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyAddController.class, this.window());
        fxView.setProp("dbItem", this);
        fxView.display();
    }

    /**
     * 键添加事件
     *
     * @param key 键
     */
    public void onKeyAdded(String key) {
        try {
            RedisKey redisKey = RedisKeyUtil.getKey(this.dbIndex, key, false, false, this.client());
            this.addChild(this.initItemByNode(redisKey));
            this.flushValue();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 键删除事件
     *
     * @param key 键
     */
    public void onKeyDeleted(String key) {
        try {
            List<RedisKeyTreeItem<?, ?>> items = this.keyChildren();
            for (RedisKeyTreeItem<?, ?> item : items) {
                if (Objects.equals(key, item.key())) {
                    item.removeChild(item);
                    break;
                }
            }
            this.flushValue();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof RedisDBTreeItem item) {
            return Comparator.comparingInt(RedisDBTreeItem::dbIndex).compare(this, item);
        }
        return super.compareTo(o);
    }
}
