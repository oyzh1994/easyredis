package cn.oyzh.easyredis.tabs.key.zset;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisGEORowAddController;
import cn.oyzh.easyredis.controller.row.RedisZSetRowAddController;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.ToggleSwitch;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
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
public class RedisZSetKeyTabContent extends RedisRowKeyTabContent<RedisZSetKeyTreeItem, RedisZSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 分数组件
     */
    @FXML
    protected FlexHBox scoreBox;

    /**
     * 地理坐标组件
     */
    @FXML
    protected FlexHBox geoBox;

    /**
     * 反转视图
     */
    @FXML
    protected ToggleSwitch reverseView;

    /**
     * 分数值
     */
    @FXML
    private DecimalTextField scoreVal;

    /**
     * 经度值
     */
    @FXML
    private DecimalTextField longitudeVal;

    /**
     * 纬度值
     */
    @FXML
    private DecimalTextField latitudeVal;

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
     * 经度列
     */
    @FXML
    private TableColumn<RedisZSetRow, Double> longitude;

    /**
     * 纬度列
     */
    @FXML
    private TableColumn<RedisZSetRow, Double> latitude;

    /**
     * 值列
     */
    @FXML
    private FlexTableColumn<RedisZSetRow, String> value;

    /**
     * 数据监听器
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    /**
     * 分数监听器
     */
    private final ChangeListener<String> scoreValListener = (observable, oldValue, newValue) -> {
        Number scoreVal = this.scoreVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(scoreVal.doubleValue(), this.treeItem.currentRow().getScore())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.currentScore(scoreVal.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    /**
     * 经度值监听器
     */
    private final ChangeListener<String> longitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.longitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLongitude())) {
            this.treeItem.currentLongitude(null);
        } else {
            this.treeItem.currentLongitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    /**
     * 纬度值监听器
     */
    private final ChangeListener<String> latitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.latitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLatitude())) {
            this.treeItem.currentLatitude(null);
        } else {
            this.treeItem.currentLatitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.isChanged());
    };

    @Override
    public boolean init(RedisZSetKeyTreeItem treeItem) {
        this.pageData = null;
        this.geoBox.managedBindVisible();
        this.scoreBox.managedBindVisible();
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
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
        if (this.isGEOView()) {
            this.value.setText("坐标名称");
            this.value.setFlexWidth("30%");
            this.latitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
            this.longitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
            this.latitudeVal.addTextChangeListener(this.latitudeValListener);
            this.longitudeVal.addTextChangeListener(this.longitudeValListener);
            this.score.setVisible(false);
            this.latitude.setVisible(true);
            this.longitude.setVisible(true);
            this.reverseView.setSelected(true);
            this.geoBox.display();
            this.scoreBox.disappear();
        } else {
            this.value.setText("成员名称");
            this.value.setFlexWidth("50%");
            this.score.setCellValueFactory(new PropertyValueFactory<>("score"));
            this.scoreVal.addTextChangeListener(this.scoreValListener);
            this.score.setVisible(true);
            this.latitude.setVisible(false);
            this.longitude.setVisible(false);
            this.reverseView.setSelected(false);
            this.geoBox.disappear();
            this.scoreBox.display();
        }
    }

    @Override
    protected List<RedisZSetRow> getRows() {
        List<RedisZSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            if (this.isGEOView()) {
                rows = rows.parallelStream()
                        .filter(r ->
                                StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
                                        StrUtil.containsIgnoreCase(String.valueOf(r.getLatitude()), filterKW) ||
                                        StrUtil.containsIgnoreCase(String.valueOf(r.getLongitude()), filterKW)
                        ).collect(Collectors.toList());
            } else {
                rows = rows.parallelStream()
                        .filter(r ->
                                StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
                                        StrUtil.containsIgnoreCase(String.valueOf(r.getScore()), filterKW)
                        ).collect(Collectors.toList());
            }
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView;
        if (this.isGEOView()) {
            fxView = StageUtil.parseStage(RedisGEORowAddController.class);
        } else {
            fxView = StageUtil.parseStage(RedisZSetRowAddController.class);
        }
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected void initRow(RedisZSetRow row) {
        super.initRow(row);
        if (row == null) {
            this.scoreVal.disable();
            this.latitudeVal.disable();
            this.longitudeVal.disable();
        } else {
            if (this.isGEOView()) {
                this.longitudeVal.setValue(row.getLongitude());
                this.longitudeVal.enable();
                this.latitudeVal.setValue(row.getLatitude());
                this.latitudeVal.enable();
            } else {
                this.scoreVal.setValue(row.getScore());
                this.scoreVal.enable();
            }
            this.saveNodeData.disable();
            this.treeItem.clearData();
        }
    }

    @FXML
    @Override
    protected void saveNodeData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn("此成员或坐标已存在！");
        } else if (this.treeItem.isChanged()) {
            ThreadUtil.startVirtual(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.saveNodeData.disable();
                }
            });
        }
    }

    // /**
    //  * 显示为地理坐标
    //  */
    // @FXML
    // private void showGEO() {
    // // 放弃保存
    // if (this.treeItem.data() != null && !MessageBox.confirm("放弃未保存的数据？")) {
    //     return;
    // }
    // this.treeItem.reverseView();
    // EventUtil.fire(RedisEventTypes.REDIS_CHANGE_ZSET_SHOW_TYPE, this.treeItem);
    // }

    @FXML
    @Override
    protected void copyRow() {
        StringBuilder builder = new StringBuilder();
        if (this.isGEOView()) {
            builder.append("键名称：").append(this.treeItem.key()).append(System.lineSeparator())
                    .append("坐标：").append(this.treeItem.currentRow().getValue()).append(System.lineSeparator())
                    .append("经度：").append(this.treeItem.currentRow().getLongitude()).append(System.lineSeparator())
                    .append("纬度：").append(this.treeItem.currentRow().getLatitude());
        } else {
            builder.append("键名称：").append(this.treeItem.key()).append(System.lineSeparator())
                    .append("成员：").append(this.treeItem.currentRow().getValue()).append(System.lineSeparator())
                    .append("分数：").append(this.treeItem.currentRow().getScore());
        }
        if (FXUtil.clipboardCopy(builder.toString())) {
            MessageBox.okToast("已复制行信息到粘贴板");
        } else {
            MessageBox.warn("复制行信息到粘贴板失败");
        }
    }

    private boolean isGEOView() {
        return this.treeItem.isGEOView();
    }

    @FXML
    private void reverseView() {
        this.treeItem.reverseView();
        this.initNode();
    }

}
