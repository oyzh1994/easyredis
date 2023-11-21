package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.BaseTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.scene.control.MenuItem;
import lombok.NonNull;

import java.util.List;

/**
 * redis服务信息树节点
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class RedisServerInfoTreeItem extends BaseTreeItem {

    /**
     * 父节点
     */
    private final RedisConnectTreeItem parent;

    public RedisServerInfoTreeItem(@NonNull RedisConnectTreeItem treeItem, @NonNull RedisTreeView treeView) {
        this.parent = treeItem;
        this.treeView(treeView);
        this.itemValue(new RedisServerInfoTreeItemValue());
        // this.itemValue("服务信息");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.itemValue().graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/info-circle.svg", "12");
            this.itemValue().graphic(glyph);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return null;
    }

    /**
     * 显示服务信息
     */
    public void showServerInfo() {
        EventUtil.fire(RedisEventTypes.REDIS_SERVER_INFO, this.parent.client());
    }
}
