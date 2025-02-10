package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.data.RedisDataExportController;
import cn.oyzh.easyredis.controller.data.RedisDataTransportController;
import cn.oyzh.easyredis.controller.key.RedisKeyAddController;
import cn.oyzh.easyredis.controller.key.RedisKeyBatchOperationController;
import cn.oyzh.easyredis.controller.key.RedisKeyFilterController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.node.NodeLifeCycle;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * redis数据库树节点
 *
 * @author oyzh
 * @since 2023/07/12
 */
public class RedisDatabaseTreeItem extends RichTreeItem<RedisDatabaseTreeItemValue> implements NodeLifeCycle {

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
    @Accessors(chain = false, fluent = true)
    private final String value;

    /**
     * 键过滤模式
     */
    @Getter
    private String filterPattern;

    @Getter
    @Accessors(chain = true, fluent = true)
    private Long dbSize;

    /**
     * 当前内部db索引
     */
    @Getter
    private final Integer innerDbIndex;

    public RedisDatabaseTreeItem(Integer dbIndex, RedisConnectTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        this.innerDbIndex = dbIndex;
        this.dbIndex = dbIndex == null ? 0 : dbIndex;
        this.value = dbIndex == null ? I18nHelper.cluster() : "db" + dbIndex;
        this.setValue(new RedisDatabaseTreeItemValue(this));
    }

    private void flushDbSize() {
        if (!this.isSentinelMode()) {
            this.dbSize = this.client().dbSize(this.dbIndex);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>(12);
        FXMenuItem add = MenuItemHelper.addKey("12", this::addKey);
//        FXMenuItem keyFilter = MenuItemHelper.keyFilter("12", this::keyFilter);
        // FXMenuItem refresh = MenuItemHelper.refreshData("12", this::reloadChild);
        FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
        FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
        FXMenuItem batchOperation = MenuItemHelper.batchOpt("12", this::batchOperation);
        FXMenuItem openTerminal = MenuItemHelper.openTerminal("12", this::openTerminal);

        items.add(add);
//        items.add(keyFilter);
        // items.add(refresh);
        items.add(exportData);
        items.add(transportData);
        items.add(batchOperation);
        items.add(openTerminal);
        return items;
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        RedisEventUtil.terminalOpen(this.client(), this.dbIndex);
    }

    /**
     * 批量操作
     */
    @FXML
    private void batchOperation() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyBatchOperationController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 传输数据
     */
    @FXML
    private void transportData() {
        StageAdapter adapter = StageManager.parseStage(RedisDataTransportController.class);
        adapter.setProp("sourceInfo", this.redisConnect());
        adapter.setProp("dbIndex", this.dbIndex);
        adapter.display();
    }

//    /**
//     * 键过滤
//     */
//    private void keyFilter() {
//        StageAdapter fxView = StageManager.parseStage(RedisKeyFilterController.class, this.window());
//        fxView.setProp("treeItem", this);
//        fxView.setProp("pattern", this.filterPattern);
//        fxView.display();
//    }

    /**
     * 执行键过滤
     *
     * @param pattern 模式
     */
    public void doKeyFilter(String pattern) {
        if (!StringUtil.equals(this.filterPattern, pattern)) {
            this.filterPattern = pattern;
            this.refresh();
            RedisEventUtil.keyFiltered(this);
        }
    }

    /**
     * 导出键
     */
    public void exportData() {
        StageAdapter fxView = StageManager.parseStage(RedisDataExportController.class);
        fxView.setProp("connect", this.redisConnect());
        fxView.setProp("dbIndex", this.dbIndex);
        fxView.display();
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

//    /**
//     * 加载子节点
//     */
//    @Override
//    public void loadChild() {
//        if (!this.isLoaded()) {
//            try {
//                this.setLoaded(true);
//                RedisDataTreeItem item1 = new RedisDataTreeItem(this.getTreeView());
//                // RedisQueryTreeItem item2 = new RedisQueryTreeItem(this.getTreeView());
//                RedisTerminalTreeItem item3 = new RedisTerminalTreeItem(this.getTreeView(), this.innerDbIndex);
//                this.setChild(List.of(item1, item3));
//                this.expend();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                this.setLoaded(false);
//            }
//        }
//    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        if (this.parent() instanceof RedisDatabasesTreeItem item) {
            return item.client();
        }
        if (this.parent() instanceof RedisConnectTreeItem item) {
            return item.client();
        }
        return null;
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public RedisConnect redisConnect() {
        return this.client().redisConnect();
    }

    /**
     * 添加键
     */
    public void addKey() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyAddController.class, this.window());
        fxView.setProp("dbItem", this);
        fxView.display();
    }

    /**
     * 键添加事件
     */
    public void onKeyAdded() {
        this.flushDbSize();
        this.refresh();
    }

    /**
     * 键删除事件
     */
    public void onKeyDeleted() {
        this.flushDbSize();
        this.refresh();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof RedisDatabaseTreeItem item) {
            return Comparator.comparingInt(RedisDatabaseTreeItem::dbIndex).compare(this, item);
        }
        return super.compareTo(o);
    }

    private void setOpening(boolean opening) {
        super.bitValue().set(7, opening);
    }

    private boolean isOpening() {
        return super.bitValue().get(7);
    }

    @Override
    public void onPrimaryDoubleClick() {
//        if (!this.isLoaded()) {
//            this.loadChild();
//            this.expend();
//        } else {
//            super.onPrimaryDoubleClick();
//        }
        if (!this.isOpening()) {
            this.setOpening(true);
            super.startWaiting(() -> {
                try {
                    RedisEventUtil.connectionOpened(this);
                } finally {
                    this.setOpening(false);
                }
            });
        }
    }

    @Override
    public void onNodeInitialize() {
        if (!NodeLifeCycle.super.isNodeInitialize()) {
            NodeLifeCycle.super.onNodeInitialize();
            this.flushDbSize();
        }
    }

}
