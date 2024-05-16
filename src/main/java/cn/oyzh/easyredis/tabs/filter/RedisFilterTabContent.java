package cn.oyzh.easyredis.tabs.filter;

import cn.hutool.core.map.MapUtil;
import cn.oyzh.easyredis.controller.filter.RedisFilterAddController;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.dto.RedisFilterVO;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.event.RedisFilterAddedEvent;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.DeleteSVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.MatchToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * redis过滤列表tab内容组件
 *
 * @author oyzh
 * @since 2023/11/27
 */
@Lazy
@Component
public class RedisFilterTabContent extends DynamicTabController {

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
    private TableView<RedisFilter> listTable;

    /**
     * 数据索id列
     */
    @FXML
    private TableColumn<RedisFilterVO, String> index;

    /**
     * 关键词列
     */
    @FXML
    private TableColumn<RedisFilterVO, String> kw;

    /**
     * 数据状态列
     */
    @FXML
    private FlexTableColumn<RedisFilterVO, String> status;

    /**
     * 匹配模式列
     */
    @FXML
    private FlexTableColumn<RedisFilterVO, String> matchMode;

    /**
     * 数据操作列
     */
    @FXML
    private FlexTableColumn<RedisFilterVO, String> action;

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
        this.pageData = this.filterStore.getPage(20, MapUtil.of("searchKeyWord", this.searchKeyWord.getText()));
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(RedisFilterVO.convert(this.pageData.page(pageNo)));
        this.pagePane.setPaging(this.pageData);
    }

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // 操作栏初始化
        this.action.setCellFactory((cell) -> new FXTableCell<>() {
            private HBox hBox;

            @Override
            public Node initGraphic() {
                if (this.hBox == null) {
                    // 删除按钮
                    DeleteSVGGlyph del = new DeleteSVGGlyph("14");
                    del.setOnMousePrimaryClicked((event) -> deleteInfo(this.getTableItem()));
                    this.hBox = new HBox(del);
                    HBox.setMargin(del, new Insets(7, 0, 0, 5));
                }
                return hBox;
            }
        });

        // 状态栏初始化
        this.status.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public FXToggleSwitch initGraphic() {
                RedisFilterVO filterVO = this.getTableItem();
                if (filterVO != null) {
                    EnabledToggleSwitch toggleSwitch = new EnabledToggleSwitch();
                    toggleSwitch.setFontSize(11);
                    toggleSwitch.setSelected(filterVO.isEnable());
                    toggleSwitch.selectedChanged((abs, o, n) -> {
                        filterVO.setEnable(n);
                        if (filterStore.update(filterVO)) {
                            RedisEventUtil.treeChildFilter();
                        } else {
                            MessageBox.warn(I18nHelper.operationFail());
                        }
                    });
                    return toggleSwitch;
                }
                return null;
            }
        });

        // 匹配模式栏初始化
        this.matchMode.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public FXToggleSwitch initGraphic() {
                RedisFilterVO filterVO = this.getTableItem();
                if (filterVO != null) {
                    MatchToggleSwitch toggleSwitch = new MatchToggleSwitch();
                    toggleSwitch.fontSize(11);
                    toggleSwitch.setSelected(filterVO.isPartMatch());
                    toggleSwitch.selectedChanged((obs, o, n) -> {
                        filterVO.setPartMatch(n);
                        if (filterStore.update(filterVO)) {
                            RedisEventUtil.treeChildFilter();
                        } else if (filterVO.isEnable()) {
                            MessageBox.warn(I18nHelper.operationFail());
                        }
                    });
                    return toggleSwitch;
                }
                return null;
            }
        });
    }

    /**
     * 删除redis信息
     *
     * @param info redis信息
     */
    private void deleteInfo(RedisFilter info) {
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            if (this.filterStore.delete(info)) {
                RedisEventUtil.treeChildFilter();
                this.firstPage();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    /**
     * 添加redis信息
     */
    @FXML
    private void toAdd() {
        StageUtil.showStage(RedisFilterAddController.class);
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
    @Subscribe
    private void filterAdded(RedisFilterAddedEvent event) {
        this.initDataList(Integer.MAX_VALUE);
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url, resources);
        this.kw.setCellValueFactory(new PropertyValueFactory<>("kw"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
    }
}
