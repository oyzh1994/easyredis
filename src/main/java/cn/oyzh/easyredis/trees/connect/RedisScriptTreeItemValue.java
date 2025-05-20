//package cn.oyzh.easyredis.trees.connect;
//
//import cn.oyzh.fx.gui.svg.glyph.ScriptSVGGlyph;
//import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
//import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
//
///**
// * redis树节点值
// *
// * @author oyzh
// * @since 2023/4/7
// */
//public class RedisScriptTreeItemValue extends RichTreeItemValue {
//
//    public RedisScriptTreeItemValue(RedisScriptTreeItem item) {
//        super(item);
//    }
//
//    @Override
//    protected RedisScriptTreeItem item() {
//        return (RedisScriptTreeItem) super.item();
//    }
//
//    @Override
//    public SVGGlyph graphic() {
//        if (this.graphic == null) {
//            this.graphic = new ScriptSVGGlyph("10");
//        }
//        return super.graphic();
//    }
//
//    @Override
//    public String name() {
//        return this.item().value().getSha1();
//    }
//}
