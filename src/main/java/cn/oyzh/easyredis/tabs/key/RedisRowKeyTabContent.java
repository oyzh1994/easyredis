package cn.oyzh.easyredis.tabs.key;

import cn.oyzh.easyredis.redis.RedisRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.FlexFlowPane;
import cn.oyzh.fx.plus.controls.PagePane;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;

/**
 * redis键tab内容组件，支持行显示
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisRowKeyTabContent<T extends RedisRowKeyTreeItem<?, ?, R>, R extends RedisRow> extends RedisKeyTabContent<T> {

    /**
     * 分页数据
     */
    protected Paging<R> pageData;

    /**
     * 分页面板
     */
    @FXML
    protected PagePane<R> pagePane;

    /**
     * 数据过滤组件
     */
    @FXML
    protected ClearableTextField filter;

    /**
     * 数据列表
     */
    @FXML
    protected FlexTableView<R> listTable;

    /**
     * 行操作列
     */
    @FXML
    protected TableColumn<R, String> action;

    /**
     * 数据操作面板
     */
    @FXML
    protected FlexFlowPane dataAction;

    @Override
    public boolean init(T treeItem) {
        if (super.init(treeItem)) {
            // 过滤处理
            this.filter.addTextChangeListener((observable, oldValue, newValue) -> TaskManager.startDelay("redis:list:filter", this::firstPage, 50));
            return true;
        }
        return false;
    }

    /**
     * 上一页
     */
    @FXML
    protected void prevPage() {
        this.initPage(this.pageData.currentPage() - 1);
    }

    /**
     * 下一页
     */
    @FXML
    protected void nextPage() {
        this.initPage(this.pageData.currentPage() + 1);
    }

    /**
     * 首页
     */
    @FXML
    public void firstPage() {
        this.initPage(0);
    }

    /**
     * 尾页
     */
    @FXML
    protected void lastPage() {
        this.initPage(Integer.MAX_VALUE);
    }

    @Override
    public void reloadNode() {
        // 放弃保存
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm("放弃未保存的数据？")) {
            return;
        }
        try {
            // 刷新数据
            this.treeItem.refreshNodeValue();
            // 跳转到首页
            this.firstPage();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 删除行
     */
    @FXML
    protected void deleteRow() {
        if (MessageBox.confirm("确定删除此数据？")) {
            if (this.treeItem.deleteRow()) {
                this.firstPage();
            }
        }
    }

    /**
     * 添加行
     */
    protected void addRow() {
    }

    /**
     * 获取数据行
     *
     * @return 数据行
     */
    protected List<R> getRows() {
        return Collections.emptyList();
    }

    /**
     * 初始化分页
     *
     * @param pageNo 页码
     */
    protected void initPage(int pageNo) {
        List<R> rows = this.getRows();
        this.pageData = new Paging<>(rows, 10);
        List<R> pageRows = this.pageData.page(pageNo);
        int index = 1;
        for (R row : pageRows) {
            row.setIndex(index++);
        }
        this.listTable.getItems().setAll(pageRows);
        this.pagePane.setPaging(this.pageData);
    }

    /**
     * 初始化列表控件
     */
    protected void initTable() {
        // 初始化操作栏
        this.action.setCellFactory((cell) -> new FXTableCell<>() {
            private HBox hBox;

            @Override
            public Node initGraphic() {
                if (this.hBox == null) {
                    // 删除按钮
                    SVGGlyph del = new SVGGlyph("/font/delete.svg", 14.d);
                    del.setTipText("删除");
                    del.setOnMousePrimaryClicked((event) -> deleteRow());

                    this.hBox = new HBox(del);
                    this.hBox.setSpacing(5);
                }
                return this.hBox;
            }
        });
        // 监听列表点击事件
        this.listTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.initRow(newValue));
    }

    /**
     * 初始化行
     *
     * @param row 当前行
     */
    protected void initRow(R row) {
        this.treeItem.currentRow(row);
        this.treeItem.data(null);
        if (row == null) {
            this.clearRawData();
            this.dataAction.disable();
        } else {
            this.setRawData(this.treeItem.rawValue());
            this.dataAction.enable();
        }
    }

    /**
     * 复制行
     */
    protected void copyRow() {
    }
}
