package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisTerminalTreeItem extends RichTreeItem<RedisTerminalTreeItem.RedisTerminalTreeItemValue> {

    private final Integer dbIndex;

    public RedisTerminalTreeItem(RichTreeView treeView, Integer dbIndex) {
        super(treeView);
        this.dbIndex = dbIndex;
        this.setValue(new RedisTerminalTreeItemValue());
    }

    public RedisConnect redisConnect() {
        if (this.parent() instanceof RedisDatabaseTreeItem item) {
            return item.redisConnect();
        }
        if (this.parent() instanceof RedisConnectTreeItem item) {
            return item.value();
        }
        return null;
    }

    @Override
    public void onPrimaryDoubleClick() {
        RedisEventUtil.terminalOpen(this.redisConnect(), this.dbIndex);
    }

    /**
     * redis树节点值
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
