package cn.oyzh.easyredis.tabs.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisListRowAddController;
import cn.oyzh.easyredis.redis.row.RedisListRow;
import cn.oyzh.easyredis.trees.RedisListKeyTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * list键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
@Lazy
@Component
public class RedisListKeyTabContentController extends RedisRowKeyTabContentController<RedisListKeyTreeItem, RedisListRow> {

    /**
     * 数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisListRow, Integer> index;

    /**
     * 行号列
     */
    @FXML
    private TableColumn<RedisListRow, Integer> lineIndex;

    /**
     * 行值列
     */
    @FXML
    private TableColumn<RedisListRow, String> value;

    /**
     * 数据监听器
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearUnsavedNodeData();
        } else {
            this.treeItem.unsavedNodeData(newValue);
        }
    };

    @Override
    public boolean init(RedisListKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            this.treeItem.unsavedNodeDataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
            return true;
        }
        return false;
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 绑定属性
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
        this.lineIndex.setCellValueFactory(new PropertyValueFactory<>("lineIndex"));
    }

    /**
     * 刷新行
     */
    @FXML
    private void reloadRow() {
        // 放弃保存
        if (this.treeItem.hasUnsavedNodeData() && !MessageBox.confirm("放弃未保存的数据？")) {
            return;
        }
        try {
            // 刷新数据
            if (this.treeItem.reloadRow()) {
                this.initRow(this.treeItem.currentRow());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected List<RedisListRow> getRows() {
        List<RedisListRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void deleteRow() {
        if (MessageBox.confirm("确定删除此行？")) {
            try {
                this.treeItem.deleteRow();
                this.firstPage();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisListRowAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @FXML
    @Override
    protected void copyRow() {
        StringBuilder builder = new StringBuilder();
        builder.append("键名称：").append(this.treeItem.key())
                .append("成员：").append(this.treeItem.currentRow().getValue());
        if (FXUtil.clipboardCopy(builder.toString())) {
            MessageBox.okToast("已复制行信息到粘贴板");
        } else {
            MessageBox.warn("复制行信息到粘贴板失败");
        }
    }
}
