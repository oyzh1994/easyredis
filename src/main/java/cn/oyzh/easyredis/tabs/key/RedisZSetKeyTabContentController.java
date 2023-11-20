package cn.oyzh.easyredis.tabs.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetMemberAddController;
import cn.oyzh.easyredis.trees.RedisZSetKeyTreeItem;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisZSetRow;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.event.EventUtil;
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
 * zset键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Lazy
@Component
public class RedisZSetKeyTabContentController extends RedisRowKeyTabContentController<RedisZSetKeyTreeItem, RedisZSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 分数值
     */
    @FXML
    private DecimalTextField scoreVal;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisZSetRow, Integer> index;

    /**
     * 分数列
     */
    @FXML
    private TableColumn<RedisZSetRow, Double> score;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisZSetRow, String> value;

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
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    /**
     * 分数监听器
     */
    private final ChangeListener<String> scoreValListener = (observable, oldValue, newValue) -> {
        Number scoreVal = this.scoreVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(scoreVal.doubleValue(), this.treeItem.currentRow().getScore())) {
            this.treeItem.clearUnsavedNodeData();
        } else {
            this.treeItem.currentScore(scoreVal.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    @Override
    public boolean init(RedisZSetKeyTreeItem treeItem) {
        this.pageData = null;
        return super.init(treeItem);
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 绑定属性
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.score.setCellValueFactory(new PropertyValueFactory<>("score"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @Override
    protected List<RedisZSetRow> getRows() {
        List<RedisZSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
                            StrUtil.containsIgnoreCase(String.valueOf(r.getScore()), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void deleteRow() {
        if (MessageBox.confirm("确定删除此成员？")) {
            try {
                if (this.treeItem.deleteRow()) {
                    this.firstPage();
                } else {
                    MessageBox.warn("删除成员失败！");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisZSetMemberAddController.class);
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected void initRow(RedisZSetRow row) {
       super.initRow(row);
        if (row == null) {
            this.scoreVal.removeTextChangeListener(this.scoreValListener);
            this.scoreVal.clear();
            this.scoreVal.disable();
        } else {
            this.scoreVal.removeTextChangeListener(this.scoreValListener);
            this.scoreVal.setValue(row.getScore());
            this.scoreVal.addTextChangeListener(this.scoreValListener);
            this.scoreVal.enable();
        }
    }

    @Override
    protected boolean beforeNodeDataSave() {
        if (this.treeItem.isChanged()) {
            if (this.treeItem.checkExists()) {
                MessageBox.warn("此成员已存在！");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void afterNodeDataSaved() {
        super.afterNodeDataSaved();
        this.saveNodeData.disable();
    }

    /**
     * 显示为地理坐标
     */
    @FXML
    private void showGEO() {
        // 放弃保存
        if (this.treeItem.unsavedNodeData() != null && !MessageBox.confirm("放弃未保存的数据？")) {
            return;
        }
        this.treeItem.reverseView();
        EventUtil.fire(RedisEventTypes.REDIS_CHANGE_ZSET_SHOW_TYPE, this.treeItem);
    }

    @FXML
    @Override
    protected void copyRow() {
        StringBuilder builder = new StringBuilder();
        builder.append("键名称：").append(this.treeItem.key())
                .append("成员：").append(this.treeItem.currentRow().getValue())
                .append("分数：").append(this.treeItem.currentRow().getScore());
        if (FXUtil.clipboardCopy(builder.toString())) {
            MessageBox.okToast("已复制行信息到粘贴板");
        } else {
            MessageBox.warn("复制行信息到粘贴板失败");
        }
    }
}
