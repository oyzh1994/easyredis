package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisKeysTab extends RichTab {

    public RedisKeysTab(RedisDatabaseTreeItem treeItem) {
        super();
        this.controller().init(treeItem);
        super.flush();
    }

    public void flushData() {
        this.controller().initData();
    }

    @Override
    protected String getTabTitle() {
        String name = this.treeItem().redisConnect().getName();
        Integer dbIndex = this.treeItem().getInnerDbIndex();
        if (dbIndex != null) {
            name += "@" + dbIndex;
        }
        RedisKeyTreeItem keyItem = this.activeItem();
        if (keyItem != null) {
            name += "#" + keyItem.key();
        }
        return name;
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph graphic = this.treeItem().itemGraphic();
        if (graphic == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null || !StringUtil.notEquals(glyph.getUrl(), graphic.getUrl())) {
            glyph = graphic.clone();
            glyph.disableTheme();
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph graphic = this.treeItem().itemGraphic();
        if (graphic == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            return;
        }
        if (graphic.getColor() != glyph.getColor()) {
            glyph.setColor(graphic.getColor());
        }
    }

    /**
     * redis键节点
     */
    public RedisKeyTreeItem activeItem() {
        return this.controller().getActiveItem();
    }

    @Override
    protected String url() {
        return "/tabs/key/redisKeysTab.fxml";
    }

    @Override
    protected RedisKeysTabController controller() {
        return (RedisKeysTabController) super.controller();
    }

    /**
     * ttl更新事件
     */
    public void flushTTL() {
        this.controller().flushTTL();
    }

    public RedisDatabaseTreeItem treeItem() {
        return this.controller().treeItem();
    }

    public int dbIndex() {
        RedisDatabaseTreeItem treeItem = this.treeItem();
        return treeItem == null ? -1 : treeItem.dbIndex();
    }

    public RedisClient client() {
        return this.controller().client();
    }

    public RedisConnect redisConnect() {
        return this.client().redisConnect();
    }

}
