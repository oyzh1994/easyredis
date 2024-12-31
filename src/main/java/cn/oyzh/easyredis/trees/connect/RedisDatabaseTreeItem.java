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
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.node.NodeLifeCycle;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
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
public class RedisDatabaseTreeItem extends RichTreeItem<RedisDatabaseTreeItem.RedisDatabaseTreeItemValue> implements NodeLifeCycle {

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

    private Integer innerDbIndex;

    public RedisDatabaseTreeItem(Integer dbIndex, RedisConnectTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        this.innerDbIndex = dbIndex;
        this.dbIndex = dbIndex == null ? 0 : dbIndex;
        this.value = dbIndex == null ? I18nHelper.keys() : "db" + dbIndex;
        this.setValue(new RedisDatabaseTreeItemValue(this));
    }

    private void flushDbSize() {
        if (!this.isSentinelMode()) {
            this.dbSize = this.client().dbSize(this.dbIndex);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addKey("12", this::addKey);
        FXMenuItem keyFilter = MenuItemHelper.keyFilter("12", this::keyFilter);
        // FXMenuItem refresh = MenuItemHelper.refreshData("12", this::reloadChild);
        FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
        FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
        FXMenuItem batchOperation = MenuItemHelper.batchOpt("12", this::batchOperation);

        items.add(add);
        items.add(keyFilter);
        // items.add(refresh);
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
        StageAdapter fxView = StageManager.parseStage(RedisKeyBatchOperationController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 传输数据
     */
    @FXML
    private void transportData() {
        // StageAdapter fxView = StageManager.getStage(RedisInfoTransportController.class);
        // if (fxView != null) {
        //     fxView.disappear();
        // }
        // fxView = StageManager.parseStage(RedisInfoTransportController.class);
        // fxView.setProp("treeItem", this);
        // fxView.display();

        StageAdapter adapter = StageManager.parseStage(RedisDataTransportController.class);
        adapter.setProp("sourceInfo", this.redisConnect());
        adapter.setProp("dbIndex", this.dbIndex);
        adapter.display();
    }

    /**
     * 键过滤
     */
    private void keyFilter() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyFilterController.class, this.window());
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
        if (!StringUtil.equals(this.filterPattern, pattern)) {
            this.filterPattern = pattern;
            // this.reloadChild();
            this.refresh();
            RedisEventUtil.keyFiltered(this);
        }
    }

    /**
     * 导出键
     */
    public void exportData() {
        // StageAdapter fxView = StageManager.parseStage(RedisKeyExportController.class, this.window());
        // fxView.setProp("treeItem", this);
        // fxView.display();
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

    /**
     * 加载子节点
     */
    @Override
    public void loadChild() {
        if (!this.isLoaded()) {
            try {
                this.setLoaded(true);
                RedisDataTreeItem item1 = new RedisDataTreeItem(this.getTreeView());
                // RedisQueryTreeItem item2 = new RedisQueryTreeItem(this.getTreeView());
                RedisTerminalTreeItem item3 = new RedisTerminalTreeItem(this.getTreeView(), this.innerDbIndex);
                this.setChild(List.of(item1, item3));
                this.expend();
            } catch (Exception ex) {
                ex.printStackTrace();
                this.setLoaded(false);
            }
        }
    }

    @Override
    public RedisDatabasesTreeItem parent() {
        return (RedisDatabasesTreeItem) super.parent();
    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.parent().client();
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

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
            this.expend();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    @Override
    public void onNodeInitialize() {
        if (!NodeLifeCycle.super.isNodeInitialize()) {
            NodeLifeCycle.super.onNodeInitialize();
            this.flushDbSize();
        }
    }

    /**
     * Redis DB值
     *
     * @author oyzh
     * @since 2023/06/22
     */
    @Accessors(chain = true, fluent = true)
    public static class RedisDatabaseTreeItemValue extends RichTreeItemValue {

        public RedisDatabaseTreeItemValue(RedisDatabaseTreeItem item) {
            super(item);
        }

        @Override
        protected RedisDatabaseTreeItem item() {
            return (RedisDatabaseTreeItem) super.item();
        }

        @Override
        public String name() {
            return this.item().value();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/database-2-line.svg", 10);
                this.graphic.disableTheme();
            }
            return super.graphic();
        }

        @Override
        public Color graphicColor() {
            if (this.item().isChildEmpty()) {
                return super.graphicColor();
            }
            return Color.DARKGREEN;
        }

        @Override
        public String extra() {
            try {
                String extra = "";
                Long dbSize = this.item().dbSize();
                if (dbSize != null) {
                    extra += "(" + dbSize + ")";
                }
                String filterPattern = this.item().getFilterPattern();
                if (StringUtil.isNotBlank(filterPattern)) {
                    extra += "[" + I18nHelper.keyFilter() + ":" + filterPattern + "]";
                }
                return extra;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return super.extra();
        }
    }
}
