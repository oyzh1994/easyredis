package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * Redis DB树键值
 *
 * @author oyzh
 * @since 2023/07/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisKeyTreeItemValue extends RedisTreeItemValue {

    /**
     * 类型
     */
    @Setter
    private String type;

    public RedisKeyTreeItemValue(@NonNull String nodeName) {
        super(nodeName);
    }

    public RedisKeyTreeItemValue(@NonNull RedisKey node) {
        super(node);
    }

    /**
     * 初始化类型数量组件
     */
    protected void initType() {
        if (this.type == null) {
            return;
        }
        // 创建组件
        if (this.getRootNode().getChildren().size() < 3) {
            FXText text = new FXText("(" + this.type + ")");
            text.setFill(Color.valueOf("#228B22"));
            this.getRootNode().getChildren().add(text);
            HBox.setMargin(text, new Insets(0, 0, 0, 3));
        }
    }

    @Override
    public HBox create() {
        super.create();
        // 初始化键数量组件
        this.initType();
        return this.getRootNode();
    }

}
