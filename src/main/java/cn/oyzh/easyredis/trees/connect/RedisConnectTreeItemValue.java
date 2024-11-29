package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis 连接值
 *
 * @author oyzh
 * @since 2023/08/10
 */
@Accessors(chain = true, fluent = true)
public class RedisConnectTreeItemValue extends RedisTreeItemValue {

    // /**
    //  * 节点
    //  */
    // private final RedisConnectTreeItem item;

    public RedisConnectTreeItemValue(RedisConnectTreeItem item) {
        super(item);
        // this.flushGraphic();
        // this.flushGraphicColor();
        // this.name(item.value().getName());
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
            this.graphic = new SVGGlyph("/font/redis.svg", 10);
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

    /**
     * 清除角色组件
     */
    public void clearRole() {
        // FXText role = (FXText) this.lookup("#role");
        // this.removeChild(role);
    }

    /**
     * 刷新角色组件
     */
    public void flushRole() {
        // if (this.item.role() != null) {
        //     // 角色名称
        //     String roleName = switch (this.item.role().toLowerCase()) {
        //         case "sentinel" -> I18nHelper.sentinel();
        //         case "master" -> I18nHelper.master();
        //         case "slave" -> I18nHelper.slave();
        //         default -> null;
        //     };
        //     // 寻找组件
        //     FXText role = (FXText) this.lookup("#role");
        //     if (roleName == null) {
        //         this.removeChild(role);
        //     } else {
        //         if (role == null) {
        //             role = new FXText();
        //             role.setId("role");
        //             role.setFill(Color.valueOf("#228B22"));
        //             this.addChild(role);
        //             HBox.setMargin(role, new Insets(0, 0, 0, 3));
        //         }
        //         String str = "(";
        //         if (this.item.isSentinelMode()) {
        //             str += roleName;
        //         } else if (this.item.isClusterMode()) {
        //             str += I18nHelper.cluster() + "/" + roleName;
        //         } else if (this.item.isMasterMode()) {
        //             str += I18nHelper.master_slave() + "/" + roleName;
        //         }
        //         if (this.item.isReadonly()) {
        //             str += "/" + I18nHelper.readonly();
        //         }
        //         str += ")";
        //         role.setText(str);
        //     }
        // }
    }
}
