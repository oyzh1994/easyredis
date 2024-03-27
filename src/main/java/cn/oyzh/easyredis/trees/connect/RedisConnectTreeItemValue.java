package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * Redis 连接值
 *
 * @author oyzh
 * @since 2023/08/10
 */
//@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisConnectTreeItemValue extends RedisTreeItemValue {

    /**
     * 节点
     */
    private final RedisConnectTreeItem item;

    public RedisConnectTreeItemValue(RedisConnectTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value().getName());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/redis.svg", 10);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isConnected() || this.item.isConnecting()) {
            glyph.setColor(Color.GREEN);
        } else {
            super.flushGraphicColor();
        }
    }

    /**
     * 清除角色组件
     */
    public void clearRole() {
        FXText role = (FXText) this.lookup("#role");
        this.removeChild(role);
    }

    /**
     * 刷新角色组件
     */
    public void flushRole() {
        // 角色名称
        String roleName = switch (item.role().toLowerCase()) {
            case "sentinel" -> "哨兵节点";
            case "master" -> "主节点";
            case "slave" -> "从节点";
            default -> null;
        };
        // 寻找组件
        FXText role = (FXText) this.lookup("#role");
        if (roleName == null) {
            this.removeChild(role);
        } else {
            if (role == null) {
                role = new FXText();
                role.setId("role");
                role.setFill(Color.valueOf("#228B22"));
                this.addChild(role);
                HBox.setMargin(role, new Insets(0, 0, 0, 3));
            }
            String str = "(" + roleName;
            if (this.item.isClusterMode()) {
                str += "/cluster集群";
            } else if (this.item.isMasterMode()) {
                str += "/主从集群";
            }
            if (this.item.isReadonly()) {
                str += "/只读模式";
            }
            str += ")";
            role.setText(str);
        }
    }
}
