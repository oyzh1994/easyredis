package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.trees.keys.RedisRowKeyTreeItem;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

import java.util.List;

/**
 * redis键tab内容组件，支持行显示
 *
 * @author oyzh
 * @since 2023/06/30
 */
public abstract class RedisRowKeyTabController<T extends RedisRowKeyTreeItem<R>, R extends RedisKeyRow> extends RedisKeyTabController<T> {

    /**
     * 分页数据
     */
    protected Paging<R> pageData;

    /**
     * 分页面板
     */
    @FXML
    protected PageBox<R> pagePane;

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
     * 数据操作面板
     */
    @FXML
    protected FlexHBox dataAction;

    @Override
    public boolean init(T treeItem) {
        if (super.init(treeItem)) {
            // 过滤处理
            this.filter.addTextChangeListener((t3, t2, t1) -> TaskManager.startDelay("redis:list:filter", this::firstPage, 50));
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
    public void reloadKey() {
        // 放弃保存
        if (this.treeItem.isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        try {
            // 刷新数据
            this.treeItem.refreshKeyValue();
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
    protected abstract void deleteRow();

    /**
     * 添加行
     */
    protected abstract void addRow();

    /**
     * 获取行列表
     *
     * @return 行列表
     */
    protected abstract List<R> getRows();

    /**
     * 初始化分页
     *
     * @param pageNo 页码
     */
    protected void initPage(long pageNo) {
        List<R> rows = this.getRows();
        this.pageData = new Paging<>(rows, 10);
        List<R> pageRows = this.pageData.page(pageNo);
        this.listTable.setItem(pageRows);
        this.pagePane.setPaging(this.pageData);
    }

    /**
     * 初始化列表控件
     */
    protected void initTable() {
        // 监听列表点击事件
        this.listTable.selectedItemChanged((observable, oldValue, newValue) -> this.initRow(newValue));
    }

    /**
     * 初始化行
     *
     * @param row 当前行
     */
    protected void initRow(R row) {
        this.treeItem.currentRow(row);
        this.treeItem.clearData();
        if (row == null) {
            this.clearRow();
        } else {
            this.firstShowData();
        }
        if (this.dataAction != null) {
            this.dataAction.setDisable(row == null);
        }
    }

    /**
     * 复制行
     */
    protected abstract void copyRow();

    /**
     * 清除行
     */
    protected abstract void clearRow();
}
