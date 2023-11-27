package cn.oyzh.easyredis.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Setter;
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
     * 当前角色
     */
    private String role;

    /**
     * 是否cluster集群
     */
    @Setter
    private boolean cluster;

    /**
     * 是否master集群
     */
    @Setter
    private boolean master;

    /**
     * 是否只读
     */
    @Setter
    private boolean readOnly;

    private final RedisConnectTreeItem item;

    public RedisConnectTreeItemValue(RedisConnectTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value().getName());
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/redis.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isConnected() && glyph.getColor() != Color.GREEN) {
            glyph.setColor(Color.GREEN);
        } else if (!this.item.isConnected() && glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
        }
    }

    /**
     * 设置角色类型
     *
     * @param role 当前角色
     */
    public void role(String role) {
        if (StrUtil.equalsIgnoreCase("sentinel", role)) {
            this.role = "哨兵";
        } else if (StrUtil.equalsIgnoreCase("master", role)) {
            this.role = "主节点";
        } else if (StrUtil.equalsIgnoreCase("slave", role)) {
            this.role = "从节点";
        } else {
            this.role = null;
        }
        this.flushRole();
    }

    /**
     * 清除角色组件
     */
    public void clearRole() {
        this.role = null;
        FXUtil.runLater(this::flushRole);
    }

    /**
     * 刷新角色组件
     */
    public void flushRole() {
        // 寻找组件
        FXText role = (FXText) this.lookup("#role");
        if (this.role == null) {
            this.removeChild(role);
        } else {
            if (role == null) {
                role = new FXText();
                role.setId("role");
                role.setFill(Color.valueOf("#228B22"));
                this.addChild(role);
                HBox.setMargin(role, new Insets(0, 0, 0, 3));
            }
            String str = "(" + this.role;
            if (this.cluster) {
                str += "-cluster集群";
            }
            if (this.master) {
                str += "-主从集群";
            }
            if (this.readOnly) {
                str += "-只读模式";
            }
            str += ")";
            role.setText(str);
        }
    }

    // @Override
    // public HBox create() {
    //     super.create();
    //     // 初始化组件
    //     this.init();
    //     return this.getRootNode();
    // }

}
