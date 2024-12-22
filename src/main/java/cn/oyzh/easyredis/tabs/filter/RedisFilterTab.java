package cn.oyzh.easyredis.tabs.filter;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.easyredis.controller.filter.RedisFilterAddController;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.dto.RedisFilterVO;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.event.RedisFilterAddedEvent;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis过滤列表tab
 *
 * @author oyzh
 * @since 2023/11/27
 */
public class RedisFilterTab extends DynamicTab {

    public RedisFilterTab() {
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        FilterSVGGlyph glyph = (FilterSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new FilterSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/filter/redisFilterTab.fxml";
    }

    @Override
    public String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.filter.main");
    }

    /**
     * redis过滤列表tab内容组件
     *
     * @author oyzh
     * @since 2023/11/27
     */
    public static class RedisFilterTabController extends DynamicTabController {

        /**
         * 分页组件
         */
        @FXML
        private PageBox<RedisFilter> pagePane;

        /**
         * 搜索词汇
         */
        @FXML
        private ClearableTextField searchKeyWord;

        /**
         * 数据列表
         */
        @FXML
        private FXTableView<RedisFilterVO> listTable;

        /**
         * 分页数据
         */
        private Paging<RedisFilter> pageData;

        /**
         * redis过滤配置储存
         */
        private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

        /**
         * 初始化数据列表
         *
         * @param pageNo 数据页码
         */
        private void initDataList(long pageNo) {
            if (this.pageData != null) {
                pageNo = this.pageData.fixPageNo(pageNo);
            }
            this.pageData = this.filterStore.getPage(pageNo, 20, this.searchKeyWord.getText());
            this.listTable.setItem(RedisFilterVO.convert(this.pageData.dataList()));
            this.pagePane.setPaging(this.pageData);
        }

        /**
         * 添加过滤
         */
        @FXML
        private void add() {
            StageManager.showStage(RedisFilterAddController.class);
        }

        /**
         * 删除过滤
         */
        @FXML
        private void delete() {
            RedisFilter filter = this.listTable.getSelectedItem();
            if (filter == null) {
                return;
            }
            if (MessageBox.confirm(I18nHelper.deleteData())) {
                if (this.filterStore.delete(filter.getKw())) {
                    RedisEventUtil.treeChildFilter();
                    this.listTable.removeItem(filter);
                    if (this.listTable.isItemEmpty()) {
                        this.firstPage();
                    }
                } else {
                    MessageBox.warn(I18nHelper.operationFail());
                }
            }
        }

        /**
         * 首页
         */
        private void firstPage() {
            this.initDataList(0);
        }

        /**
         * 上一页
         */
        @FXML
        private void prevPage() {
            this.initDataList(this.pageData.currentPage() - 1);
        }

        /**
         * 下一页
         */
        @FXML
        private void nextPage() {
            this.initDataList(this.pageData.currentPage() + 1);
        }

        /**
         * 过滤新增事件
         */
        @EventSubscribe
        private void filterAdded(RedisFilterAddedEvent event) {
            this.initDataList(Integer.MAX_VALUE);
        }

        @Override
        protected void bindListeners() {
            super.bindListeners();
            this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        }

        @Override
        public void initialize(URL url, ResourceBundle resources) {
            super.initialize(url, resources);
            // 显示首页
            this.firstPage();
        }
    }
}
