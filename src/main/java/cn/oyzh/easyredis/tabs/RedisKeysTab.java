package cn.oyzh.easyredis.tabs;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisKeysTab extends DynamicTab {

    public RedisKeysTab(RedisConnectTreeItem treeItem) {
        super();
        super.flush();
        this.controller().init(treeItem);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new FilterSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/redisKeysTab.fxml";
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.filter.main");
    }

    @Override
    protected RedisKeysTabController controller() {
        return (RedisKeysTabController) super.controller();
    }

    public RedisConnectTreeItem treeItem() {
        return this.controller().treeItem();
    }

    public RedisClient client() {
        return this.controller().client();
    }

    public RedisConnect redisConnect() {
        return this.client().redisInfo();
    }

    /**
     * @author oyzh
     * @since 2024-12-03
     */
    public static class RedisKeysTabController extends DynamicTabController {

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisClient client;

        @Getter
        @Accessors(fluent = true, chain = false)
        private RedisConnectTreeItem treeItem;

        public void init(RedisConnectTreeItem treeItem) {
            this.treeItem = treeItem;
        }

        public void doSearch(ActionEvent actionEvent) {
        }

        public void sortAsc(MouseEvent event) {
        }

        public void sortDesc(MouseEvent event) {
        }

        public void positionNode(MouseEvent event) {
        }
    }
}
