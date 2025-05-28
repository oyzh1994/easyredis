package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.fx.svg.glyph.RedisSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.paint.Color;

/**
 * redis树节点值
 *
 * @author oyzh
 * @since 2023/08/10
 */
public class RedisConnectTreeItemValue extends RichTreeItemValue {

    public RedisConnectTreeItemValue(RedisConnectTreeItem item) {
        super(item);
        super.setRichMode(true);
    }

    @Override
    protected RedisConnectTreeItem item() {
        return (RedisConnectTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new RedisSVGGlyph(12);
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (this.item().isConnected() || this.item().isConnecting()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }

    @Override
    public String extra() {
        if (this.item().isConnected()) {
            String role = this.item().role();
            if (role != null) {
                // 角色名称
                String roleName = switch (role.toLowerCase()) {
                    case "sentinel" -> I18nHelper.sentinel();
                    case "master" -> I18nHelper.master();
                    case "slave" -> I18nHelper.slave();
                    default -> null;
                };
                String str = "(";
                if (this.item().isSentinelMode()) {
                    str += roleName;
                } else if (this.item().isClusterMode()) {
                    str += I18nHelper.cluster() + "/" + roleName;
                } else if (this.item().isMasterMode()) {
                    str += I18nHelper.master_slave() + "/" + roleName;
                }
                if (this.item().isReadonly()) {
                    str += "/" + I18nHelper.readonly();
                }
                str += ")";
                return str;
            }
        }
        return super.extra();
    }
}
