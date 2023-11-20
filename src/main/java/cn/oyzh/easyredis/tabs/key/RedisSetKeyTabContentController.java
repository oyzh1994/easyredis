package cn.oyzh.easyredis.tabs.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisSetMemberAddController;
import cn.oyzh.easyredis.trees.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.redis.RedisSetRow;
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
public class RedisSetKeyTabContentController extends RedisRowKeyTabContentController<RedisSetKeyTreeItem, RedisSetRow> {

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
            this.treeItem.unsavedNodeData(null);
        } else {
            this.treeItem.unsavedNodeData(newValue);
        }
    };

    @Override
    public boolean init(RedisSetKeyTreeItem treeItem) {
        if (super.init(treeItem)) {
            this.pageData = null;
            this.treeItem.unsavedNodeDataProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    this.saveNodeData.disable();
                } else {
                    this.saveNodeData.enable();
                }
            });
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
        StageWrapper fxView = StageUtil.parseStage(RedisSetMemberAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected boolean beforeNodeDataSave() {
        if (this.treeItem.unsavedNodeData() == null) {
            return false;
        }
        if (this.treeItem.checkExists()) {
            MessageBox.warn("此成员已存在！");
            return false;
        }
        return super.beforeNodeDataSave();
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
