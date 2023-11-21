package cn.oyzh.easyredis.tabs.key.set;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.SetRowAddController;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.tabs.key.RowKeyTabContent;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
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
 * set键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
@Lazy
@Component
public class SetKeyTabContent extends RowKeyTabContent<RedisSetKeyTreeItem, RedisSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisSetRow, Integer> index;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisSetRow, String> value;

    /**
     * redis数据监听器
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
    };

    @Override
    public boolean init(RedisSetKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
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
    }

    @Override
    protected List<RedisSetRow> getRows() {
        List<RedisSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    /**
     * 删除行
     */
    @FXML
    @Override
    protected void deleteRow() {
        if (MessageBox.confirm("确定删除此成员？")) {
            try {
                if (this.treeItem.deleteRow()) {
                    this.firstPage();
                } else {
                    MessageBox.warn("删除此成员失败！");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    /**
     * 添加行
     */
    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(SetRowAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @FXML
    @Override
    protected void saveNodeData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn("此成员已存在！");
        } else if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(this.treeItem::saveNodeValue);
        }
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
