// package cn.oyzh.easyredis.tabs.key.geo;
//
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.easyredis.controller.row.RedisGEOCoordinateAddController;
// import cn.oyzh.easyredis.event.RedisEventTypes;
// import cn.oyzh.easyredis.redis.row.RedisZSetRow;
// import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
// import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
// import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
// import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
// import cn.oyzh.fx.plus.event.EventUtil;
// import cn.oyzh.fx.plus.information.MessageBox;
// import cn.oyzh.fx.plus.stage.StageUtil;
// import cn.oyzh.fx.plus.stage.StageWrapper;
// import cn.oyzh.fx.plus.util.FXUtil;
// import javafx.beans.value.ChangeListener;
// import javafx.fxml.FXML;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.cell.PropertyValueFactory;
// import lombok.AccessLevel;
// import lombok.Getter;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// /**
//  * geo键tab内容组件
//  *
//  * @author oyzh
//  * @since 2023/06/21
//  */
// @Deprecated
// @Lazy
// @Component
// public class RedisGEOKeyTabContent extends RedisRowKeyTabContent<RedisZSetKeyTreeItem, RedisZSetRow> {
//
//     /**
//      * redis数据保存按钮
//      */
//     @FXML
//     private SVGGlyph saveNodeData;
//
//     /**
//      * 经度值
//      */
//     @FXML
//     private DecimalTextField longitudeVal;
//
//     /**
//      * 纬度值
//      */
//     @FXML
//     private DecimalTextField latitudeVal;
//
//     /**
//      * 编号列
//      */
//     @FXML
//     private TableColumn<RedisZSetRow, Integer> index;
//
//     /**
//      * 经度列
//      */
//     @FXML
//     private TableColumn<RedisZSetRow, Double> longitude;
//
//     /**
//      * 纬度列
//      */
//     @FXML
//     private TableColumn<RedisZSetRow, Double> latitude;
//
//     /**
//      * 值列
//      */
//     @FXML
//     private TableColumn<RedisZSetRow, String> value;
//
//     /**
//      * redis数据监听器
//      */
//     @Getter(value = AccessLevel.PROTECTED)
//     private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
//         if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
//             this.treeItem.data(null);
//         } else {
//             this.treeItem.data(newValue);
//         }
//         this.saveNodeData.setDisable(!this.treeItem.isChanged());
//     };
//
//     /**
//      * 经度值监听器
//      */
//     private final ChangeListener<String> longitudeValListener = (observable, oldValue, newValue) -> {
//         Number value = this.longitudeVal.getValue();
//         if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLongitude())) {
//             this.treeItem.currentLongitude(null);
//         } else {
//             this.treeItem.currentLongitude(value.doubleValue());
//         }
//         this.saveNodeData.setDisable(!this.treeItem.isChanged());
//     };
//
//     /**
//      * 纬度值监听器
//      */
//     private final ChangeListener<String> latitudeValListener = (observable, oldValue, newValue) -> {
//         Number value = this.latitudeVal.getValue();
//         if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLatitude())) {
//             this.treeItem.currentLatitude(null);
//         } else {
//             this.treeItem.currentLatitude(value.doubleValue());
//         }
//         this.saveNodeData.setDisable(!this.treeItem.isChanged());
//     };
//
//     @Override
//     public boolean init(RedisZSetKeyTreeItem treeItem) {
//         this.pageData = null;
//         return super.init(treeItem);
//     }
//
//     @Override
//     protected void initNode() {
//         // 初始化表单
//         this.initTable();
//         // 显示首页
//         this.firstPage();
//         // 绑定属性
//         this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
//         this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
//         this.latitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
//         this.longitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
//     }
//
//     @Override
//     protected List<RedisZSetRow> getRows() {
//         List<RedisZSetRow> rows = this.treeItem.nodeValue();
//         String filterKW = this.filter.getText();
//         if (StrUtil.isNotEmpty(filterKW)) {
//             rows = rows.parallelStream()
//                     .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
//                             StrUtil.containsIgnoreCase(String.valueOf(r.getLatitude()), filterKW) ||
//                             StrUtil.containsIgnoreCase(String.valueOf(r.getLongitude()), filterKW))
//                     .collect(Collectors.toList());
//         }
//         return rows;
//     }
//
//     @FXML
//     @Override
//     protected void deleteRow() {
//         if (MessageBox.confirm("确定删除此坐标？")) {
//             try {
//                 if (this.treeItem.deleteRow()) {
//                     this.firstPage();
//                 } else {
//                     MessageBox.warn("删除坐标失败！");
//                 }
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 MessageBox.exception(ex);
//             }
//         }
//     }
//
//     @FXML
//     @Override
//     protected void addRow() {
//         StageWrapper fxView = StageUtil.parseStage(RedisGEOCoordinateAddController.class);
//         fxView.setProp("treeItem", this.treeItem);
//         fxView.display();
//     }
//
//     @Override
//     protected void initRow(RedisZSetRow row) {
//        super.initRow(row);
//         if (row == null) {
//             this.latitudeVal.removeTextChangeListener(this.latitudeValListener);
//             this.latitudeVal.clear();
//             this.longitudeVal.removeTextChangeListener(this.longitudeValListener);
//             this.longitudeVal.clear();
//             this.latitudeVal.disable();
//             this.longitudeVal.disable();
//         } else {
//             this.latitudeVal.disable();
//             this.latitudeVal.removeTextChangeListener(this.latitudeValListener);
//             this.latitudeVal.setValue(row.getLatitude());
//             this.latitudeVal.enable();
//             this.latitudeVal.addTextChangeListener(this.latitudeValListener);
//             this.longitudeVal.disable();
//             this.longitudeVal.removeTextChangeListener(this.longitudeValListener);
//             this.longitudeVal.setValue(row.getLongitude());
//             this.longitudeVal.enable();
//             this.longitudeVal.addTextChangeListener(this.longitudeValListener);
//         }
//     }
//
//     @Override
//     protected boolean beforeNodeDataSave() {
//         if (this.treeItem.isChanged()) {
//             if (this.treeItem.checkExists()) {
//                 MessageBox.warn("此成员已存在！");
//                 return false;
//             }
//         }
//         return true;
//     }
//
//     @Override
//     protected void afterNodeDataSaved() {
//         super.afterNodeDataSaved();
//         this.saveNodeData.disable();
//     }
//
//     /**
//      * 显示为有序集合
//      */
//     @FXML
//     private void showZSET() {
//         // 放弃保存
//         if (this.treeItem.data() != null && !MessageBox.confirm("放弃未保存的数据？")) {
//             return;
//         }
//         this.treeItem.reverseView();
//         EventUtil.fire(RedisEventTypes.REDIS_CHANGE_ZSET_SHOW_TYPE, this.treeItem);
//     }
//
//     @FXML
//     @Override
//     protected void copyRow() {
//         StringBuilder builder = new StringBuilder();
//             builder.append("键名称：").append(this.treeItem.key())
//                     .append("经度：").append(this.treeItem.currentRow().getLongitude())
//                     .append("纬度：").append(this.treeItem.currentRow().getLatitude())
//                     .append("坐标：").append(this.treeItem.currentRow().getValue());
//         if (FXUtil.clipboardCopy(builder.toString())) {
//             MessageBox.okToast("已复制行信息到粘贴板");
//         } else {
//             MessageBox.warn("复制行信息到粘贴板失败");
//         }
//     }
// }
