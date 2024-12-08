package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisTerminalTreeItem extends RichTreeItem<RedisTerminalTreeItem.RedisTerminalTreeItemValue> {

    public RedisTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisTerminalTreeItemValue());
    }

    @Override
    public RedisDatabaseTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisDatabaseTreeItem) parent;
    }

    public RedisConnect redisConnect(){
        return this.parent().info();
    }

    @Override
    public void onPrimaryDoubleClick() {
        RedisEventUtil.terminalOpen(this.redisConnect());
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class RedisTerminalTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new TerminalSVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.terminal();
        }
    }
}
