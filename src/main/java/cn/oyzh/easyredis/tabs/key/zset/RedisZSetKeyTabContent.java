package cn.oyzh.easyredis.tabs.key.zset;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetCoordinateAddController;
import cn.oyzh.easyredis.controller.row.RedisZSetMemberAddController;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.event.msg.RedisZSetCoordinateAddedMsg;
import cn.oyzh.easyredis.event.msg.RedisZSetMemberAddedMsg;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.pane.FlexTitledPane;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.property.ScaleDoublePropertyValueFactory;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * zset键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisZSetKeyTabContent extends RedisRowKeyTabContent<RedisZSetKeyTreeItem, RedisZSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 数据组件
     */
    @FXML
    protected FlexTitledPane dataBox;

    /**
     * 分数组件
     */
    @FXML
    protected FlexTitledPane scoreBox;

    /**
     * 地理坐标组件
     */
    @FXML
    protected FlexTitledPane longitudeBox;

    /**
     * 地理坐标组件
     */
    @FXML
    protected FlexTitledPane latitudeBox;

    /**
     * 反转视图
     */
    @FXML
    protected FXToggleSwitch reverseView;

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
            this.treeItem.data(null);
        } else {
            this.treeItem.data(newValue);
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    /**
     * 分数监听器
     */
    private final ChangeListener<String> scoreValListener = (observable, oldValue, newValue) -> {
        Number scoreVal = this.scoreVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(scoreVal.doubleValue(), this.treeItem.currentRow().getScore())) {
            this.treeItem.score(null);
        } else {
            this.treeItem.score(scoreVal.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    /**
     * 经度值监听器
     */
    private final ChangeListener<String> longitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.longitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLongitude())) {
            this.treeItem.longitude(null);
        } else {
            this.treeItem.longitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    /**
     * 纬度值监听器
     */
    private final ChangeListener<String> latitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.latitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLatitude())) {
            this.treeItem.latitude(null);
        } else {
            this.treeItem.latitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    @Override
    public boolean init(RedisZSetKeyTreeItem treeItem) {
        this.pageData = null;
        this.scoreBox.managedBindVisible();
        this.reverseView.managedBindVisible();
        this.latitudeBox.managedBindVisible();
        this.longitudeBox.managedBindVisible();
        return super.init(treeItem);
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 显示切换按钮
        this.reverseView.setVisible(this.isSupportGEO());
        // 绑定属性
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
        // 判断geo视图是否支持
        if (this.isSupportGEO() && this.isGEOView()) {
            this.latitude.setCellValueFactory(new ScaleDoublePropertyValueFactory<>("latitude", 10));
            this.longitude.setCellValueFactory(new ScaleDoublePropertyValueFactory<>("longitude", 10));
            this.value.setText("坐标名称");
            this.value.setFlexWidth("26%");
            this.latitudeVal.addTextChangeListener(this.latitudeValListener);
            this.longitudeVal.addTextChangeListener(this.longitudeValListener);
            this.score.setVisible(false);
            this.latitude.setVisible(true);
            this.longitude.setVisible(true);
            this.reverseView.setSelected(true);
            this.dataBox.setText("坐标");
            this.dataBox.setFlexHeight("100% - 500");
            this.latitudeBox.display();
            this.longitudeBox.display();
            this.scoreBox.disappear();
        } else {
            this.score.setCellValueFactory(new ScaleDoublePropertyValueFactory<>("score", 10));
            this.value.setText("成员名称");
            this.value.setFlexWidth("46%");
            this.scoreVal.addTextChangeListener(this.scoreValListener);
            this.score.setVisible(true);
            this.latitude.setVisible(false);
            this.longitude.setVisible(false);
            this.reverseView.setSelected(false);
            this.dataBox.setText("成员");
            this.dataBox.setFlexHeight("100% - 450");
            this.latitudeBox.disappear();
            this.longitudeBox.disappear();
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
                        .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getLatitude()), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getLongitude()), filterKW))
                        .collect(Collectors.toList());
            } else {
                rows = rows.parallelStream()
                        .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getScore()), filterKW))
                        .collect(Collectors.toList());
            }
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView;
        if (this.isGEOView()) {
            fxView = StageUtil.parseStage(RedisZSetCoordinateAddController.class);
        } else {
            fxView = StageUtil.parseStage(RedisZSetMemberAddController.class);
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
        } else if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.saveNodeData.disable();
                }
            });
        }
    }

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
        ClipboardUtil.setStringAndTip(builder.toString(), "成员信息");
    }

    /**
     * 是否地理坐标视图
     *
     * @return 结果
     */
    private boolean isGEOView() {
        return this.treeItem.isGEOView();
    }

    /**
     * 是否支持地理坐标
     *
     * @return 结果
     */
    private boolean isSupportGEO() {
        return this.treeItem.isSupportGEO();
    }

    @FXML
    private void reverseView() {
        this.treeItem.reverseView();
        this.initNode();
    }

    /**
     * zset坐标添加事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_ZSET_COORDINATE_ADDED, verbose = true, async = true)
    private void onZSetCoordinateAdded(RedisZSetCoordinateAddedMsg msg) {
        if (this.treeItem == msg.item()) {
            this.firstPage();
        }
    }

    /**
     * zset成员添加事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = RedisEventTypes.REDIS_ZSET_MEMBER_ADDED, verbose = true, async = true)
    private void onZSetMemberAdded(RedisZSetMemberAddedMsg msg) {
        if (this.treeItem == msg.item()) {
            this.firstPage();
        }
    }

    @Override
    public void onTabInit() {
        EventUtil.register(this);
    }

    @Override
    public void onTabClose(Event event) {
        EventUtil.unregister(this);
    }
}
