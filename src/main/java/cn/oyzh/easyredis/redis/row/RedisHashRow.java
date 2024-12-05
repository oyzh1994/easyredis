// package cn.oyzh.easyredis.redis;
//
// import javafx.beans.property.SimpleStringProperty;
//
// /**
//  * redis hash行
//  *
//  * @author oyzh
//  * @since 2023/6/16
//  */
// public class RedisHashRow extends RedisRow {
//
//     /**
//      * 字段
//      */
//     private SimpleStringProperty fieldProperty;
//
//     /**
//      * 值
//      */
//     private SimpleStringProperty valueProperty;
//
//     public RedisHashRow(String field, String value) {
//         this.setField(field);
//         this.setValue(value);
//     }
//
//     public SimpleStringProperty valueProperty() {
//         if (this.valueProperty == null) {
//             this.valueProperty = new SimpleStringProperty();
//         }
//         return valueProperty;
//     }
//
//     public void setValue(String value) {
//         this.valueProperty().setValue(value);
//     }
//
//     public String getValue() {
//         return this.valueProperty == null ? null : this.valueProperty.get();
//     }
//
//     public SimpleStringProperty fieldProperty() {
//         if (this.fieldProperty == null) {
//             this.fieldProperty = new SimpleStringProperty();
//         }
//         return fieldProperty;
//     }
//
//     public void setField(String field) {
//         this.fieldProperty().setValue(field);
//     }
//
//     public String getField() {
//         return this.fieldProperty == null ? null : this.fieldProperty.get();
//     }
// }
