package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.trees.RichTreeItemValue;
import lombok.experimental.Accessors;


/**
 * redis树键值
 *
 * @author oyzh
 * @since 2023/07/7
 */
//@Slf4j
@Accessors(chain = true, fluent = true)
public class RedisTreeItemValue extends RichTreeItemValue {

    // /**
    //  * 图标
    //  */
    // @Getter
    // protected Node graphic;
    //
    // private SimpleObjectProperty<FXHBox> rootNodeProperty;
    //
    // public SimpleObjectProperty<FXHBox> rootNodeProperty() {
    //     if (this.rootNodeProperty == null) {
    //         this.rootNodeProperty = new SimpleObjectProperty<>();
    //     }
    //     return this.rootNodeProperty;
    // }
    //
    // public FXHBox getRootNode() {
    //     return this.rootNodeProperty == null ? null : this.rootNodeProperty().get();
    // }
    //
    // public void setRootNode(FXHBox rootNode) {
    //     this.rootNodeProperty().set(rootNode);
    // }

    // /**
    //  * 根键
    //  */
    // @Getter
    // protected FXHBox rootNode;

    // /**
    //  * redis键
    //  */
    // protected RedisKey node;
    //
    // public RedisTreeItemValue(@NonNull String name) {
    //     this.name(name);
    // }
    //
    // public RedisTreeItemValue(@NonNull RedisKey node) {
    //     this(node.key());
    //     this.node = node;
    // }

    // /**
    //  * 移除键图标组件
    //  */
    // protected void removeGraphic() {
    //     if (this.getRootNode() == null) {
    //         return;
    //     }
    //     // 移除键图标组件
    //     if (!(this.getRootNode().getChildren().get(0) instanceof FXText)) {
    //         this.getRootNode().getChildren().remove(0);
    //         if (log.isDebugEnabled()) {
    //             StaticLog.debug("remove graphic.");
    //         }
    //     }
    // }
    //
    // /**
    //  * 初始化图标组件
    //  */
    // protected void initGraphic() {
    //     if (this.getRootNode() == null) {
    //         return;
    //     }
    //
    //     Node node = this.getRootNode().getChildren().get(0);
    //     // 移除图标
    //     if (this.graphic == null && !(node instanceof FXText)) {
    //         this.getRootNode().getChildren().remove(0);
    //         if (log.isDebugEnabled()) {
    //             StaticLog.debug("remove graphic.");
    //         }
    //         return;
    //     }
    //
    //     if (this.graphic == null) {
    //         return;
    //     }
    //
    //     boolean updateGraphic = false;
    //     // 添加图标
    //     if (node instanceof FXText) {
    //         this.getRootNode().getChildren().add(0, this.graphic);
    //         updateGraphic = true;
    //     } else if (this.graphic != node) { // 更新图标
    //         this.getRootNode().getChildren().set(0, this.graphic);
    //         updateGraphic = true;
    //     }
    //
    //     // 更新图标
    //     if (updateGraphic) {
    //         HBox.setMargin(this.graphic, new Insets(0, 3, 0, 0));
    //         if (this.graphic instanceof SVGGlyph glyph) {
    //             glyph.setSize(13);
    //         }
    //     }
    // }

    // /**
    //  * 初始化图标组件
    //  */
    // public void graphic(Node graphic) {
    //     if (this.graphic != graphic) {
    //         this.graphic = graphic;
    //         FXUtil.runLater(() -> {
    //             this.removeGraphic();
    //             this.initGraphic();
    //         });
    //     }
    // }

    @Override
    public void flushGraphic() {

    }

    @Override
    public void flushGraphicColor() {

    }

    // /**
    //  * 初始化键名称组件
    //  */
    // protected void initNodeName() {
    //     FXText text = null;
    //     if (this.getRootNode().getChildren().size() == 1 && this.getRootNode().getChildren().get(0) instanceof FXText text1) {
    //         text = text1;
    //     } else if (this.getRootNode().getChildren().size() >= 2 && this.getRootNode().getChildren().get(1) instanceof FXText text1) {
    //         text = text1;
    //     }
    //
    //     // 添加名称
    //     if (text == null) {
    //         text = new FXText(this.name());
    //         this.getRootNode().getChildren().add(text);
    //     }
    // }

    // /**
    //  * 获取键名称组件
    //  *
    //  * @return 键名称组件
    //  */
    // public Text nodeNameText() {
    //     if (this.getRootNode() == null) {
    //         return null;
    //     }
    //     if (this.getRootNode().getChildren().get(1) instanceof FXText text) {
    //         return text;
    //     }
    //     if (this.getRootNode().getChildren().get(0) instanceof FXText text) {
    //         return text;
    //     }
    //     return null;
    // }

    // /**
    //  * 创建组件
    //  *
    //  * @return 组件
    //  */
    // public HBox create() {
    //     try {
    //         if (this.getRootNode() == null) {
    //             // 初始化根键
    //             this.setRootNode(new FXHBox());
    //             this.getRootNode().setCursor(Cursor.HAND);
    //             if (log.isDebugEnabled()) {
    //                 StaticLog.debug("create rootNode:{}", this.name());
    //             }
    //         }
    //         // 初始化键名称组件
    //         this.initNodeName();
    //         // 初始化图标组件
    //         this.initGraphic();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return this.getRootNode();
    // }

    // /**
    //  * 销毁组件
    //  */
    // public void destroy() {
    //     try {
    //         if (this.getRootNode() != null) {
    //             this.setRootNode(null);
    //             if (log.isDebugEnabled()) {
    //                 StaticLog.debug("destroy rootNode:{}", this.name());
    //             }
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
}
