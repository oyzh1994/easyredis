package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisZSetKeyTreeItem;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisHashKey;
import cn.oyzh.easyredis.redis.RedisHyperLogLogKey;
import cn.oyzh.easyredis.redis.RedisKey;
import cn.oyzh.easyredis.redis.RedisListKey;
import cn.oyzh.easyredis.redis.RedisSetKey;
import cn.oyzh.easyredis.redis.RedisStreamKey;
import cn.oyzh.easyredis.redis.RedisStringKey;
import cn.oyzh.easyredis.redis.RedisZSetKey;
import cn.oyzh.easyredis.tabs.RedisBaseTab;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * redis节点tab
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisKeyTab<T extends RedisKeyTreeItem<?>> extends DynamicTab {

    {
        this.setClosable(true);
        this.setOnCloseRequest(event -> {
            // 取消当前键的选中
            if (this.treeItem != null && this.treeItem.treeView().getSelectedItem() == this.treeItem) {
                this.treeItem.treeView().select(this.treeItem.root());
            }
        });
    }

    /**
     * redis键
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private final RedisKey node;

    /**
     * redis树组件
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private RedisKeyTreeItem<?> treeItem;

    /**
     * 内容Controller
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private RedisBaseKeyTabContentController<T> contentController;

    /**
     * 执行初始化
     *
     * @param treeItem redis树键
     */
    public RedisKeyTab(@NonNull T treeItem) {
        this.treeItem = treeItem;
        this.node = treeItem.value();
        // 加载内容
        this.loadContent();
        // 初始化
        if (!this.contentController.init(treeItem)) {
            this.disable();
        }
        // 刷新图标
        this.flushGraphic();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/treeNode.svg", 12);
        }
        if (graphic.getCursor() != Cursor.DEFAULT) {
            graphic.setCursor(Cursor.DEFAULT);
        }
        // 设置文本
        this.setText("（" + this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "）" + this.treeItem.key());
        // 设置提示文本
        this.setTipText("（" + this.treeItem.infoName() + "-db" + this.treeItem.dbIndex() + "）" + this.treeItem.key());
    }

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        String url = null;
        if (this.node instanceof RedisStringKey) {
            url = "/tabs/key/redisStringKeyTabContent.fxml";
        } else if (this.node instanceof RedisListKey) {
            url = "/tabs/key/redisListKeyTabContent.fxml";
        } else if (this.node instanceof RedisSetKey) {
            url = "/tabs/key/redisSetKeyTabContent.fxml";
        } else if (this.node instanceof RedisZSetKey) {
            if (((RedisZSetKeyTreeItem) this.treeItem).isGEOView()) {
                url = "/tabs/key/redisGEOKeyTabContent.fxml";
            } else {
                url = "/tabs/key/redisZSetKeyTabContent.fxml";
            }
        } else if (this.node instanceof RedisHashKey) {
            url = "/tabs/key/redisHashKeyTabContent.fxml";
        } else if (this.node instanceof RedisHyperLogLogKey) {
            url = "/tabs/key/redisHyperLogLogKeyTabContent.fxml";
        } else if (this.node instanceof RedisStreamKey) {
            url = "/tabs/key/redisStreamKeyTabContent.fxml";
        }
        Node content = loaderExt.load(url);
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    /**
     * 获取键数据组件
     *
     * @return 键数据组件
     */
    public FlexTextArea getNodeDataNode() {
        return this.contentController.getNodeDataNode();
    }

    /**
     * 重新载入
     */
    public void reload() {
        this.contentController.reloadNode();
    }

    /**
     * 刷新ttl
     */
    public void flushTTL() {
        this.contentController.flushTTL();
    }

    /**
     * redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.treeItem.client();
    }
}
