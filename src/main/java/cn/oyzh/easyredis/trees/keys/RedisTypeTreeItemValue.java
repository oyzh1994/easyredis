// package cn.oyzh.easyredis.trees.type;
//
//
// import cn.oyzh.easyredis.trees.RedisTreeItemValue;
// import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
// import cn.oyzh.fx.plus.controls.text.FXText;
// import javafx.geometry.Insets;
// import javafx.scene.layout.HBox;
// import javafx.scene.paint.Color;
//
// /**
//  * Redis 类型树节点值
//  *
//  * @author oyzh
//  * @since 2023/12/08
//  */
// public class RedisTypeTreeItemValue extends RedisTreeItemValue {
//
//     // private final RedisTypeTreeItem item;
//
//     public RedisTypeTreeItemValue(RedisTypeTreeItem item) {
//         // this.item = item;
//         // this.flushGraphic();
//         // this.name(item.value().desc());
//         super(item);
//     }
//
//     @Override
//     protected RedisTypeTreeItem item() {
//         return (RedisTypeTreeItem) super.item();
//     }
//
//     @Override
//     public String name() {
//         return this.item().value().desc();
//     }
//
//     @Override
//     public SVGGlyph graphic() {
//         if (this.graphic == null) {
//             this.graphic = new SVGGlyph("/font/folder.svg", 10);
//         }
//         return super.graphic();
//     }
//
//     /**
//      * 刷新节点数量
//      */
//     public void flushNum() {
//         // try {
//         //     int showNum = item.getChildrenSize();
//         //     int totalNum = item.getRealChildrenSize();
//         //     // 寻找组件
//         //     FXText text = (FXText) this.lookup("#num");
//         //     if (text == null) {
//         //         text = new FXText();
//         //         this.addChild(text);
//         //         text.disableTheme();
//         //         text.setId("num");
//         //         text.setFill(Color.valueOf("#228B22"));
//         //         HBox.setMargin(text, new Insets(0, 0, 0, 3));
//         //     }
//         //     if (showNum == totalNum) {
//         //         text.setText("(" + totalNum + ")");
//         //     } else {
//         //         text.setText("(" + showNum + "/" + totalNum + ")");
//         //     }
//         // } catch (Exception ex) {
//         //     ex.printStackTrace();
//         // }
//     }
// }
