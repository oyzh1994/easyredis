// package cn.oyzh.easyredis.tabs.keys;
//
// import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
// import cn.oyzh.fx.gui.tabs.DynamicTab;
// import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
// import cn.oyzh.i18n.I18nHelper;
// import javafx.scene.Cursor;
//
// /**
//  * @author oyzh
//  * @since 2024-12-03
//  */
// public class RedisKeyInfoTab extends DynamicTab {
//
//     public RedisKeyInfoTab(RedisKeyTreeItem<?> treeItem) {
//         super();
//         super.flush();
//         this.init(treeItem);
//     }
//
//     public void init(RedisKeyTreeItem<?> treeItem) {
//         this.controller().init(treeItem);
//     }
//
//     @Override
//     public void flushGraphic() {
//         SVGGlyph glyph = (SVGGlyph) this.getGraphic();
//         if (glyph == null) {
//             glyph = new SVGGlyph("/font/status.svg", "12");
//             glyph.setCursor(Cursor.DEFAULT);
//             this.graphic(glyph);
//         }
//     }
//
//     @Override
//     protected RedisKeyInfoController controller() {
//         return (RedisKeyInfoController) super.controller();
//     }
//
//     @Override
//     protected String getTabTitle() {
//         return I18nHelper.info();
//     }
//
//     @Override
//     protected String url() {
//         return "/tabs/keys/redisKeyInfoTab.fxml";
//     }
//
// }
