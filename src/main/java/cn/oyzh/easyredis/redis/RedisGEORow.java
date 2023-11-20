package cn.oyzh.easyredis.redis;// package cn.oyzh.easyredis.redis;
//
// import javafx.beans.property.SimpleDoubleProperty;
// import javafx.beans.property.SimpleStringProperty;
// import lombok.Getter;
// import lombok.Setter;
// import redis.clients.jedis.GeoCoordinate;
//
// /**
//  * @author oyzh
//  * @since 2023/6/16
//  */
// public class RedisGEORow {
//
//     /**
//      * 索引
//      */
//     @Setter
//     @Getter
//     private int index;
//
//     /**
//      * 值
//      */
//     private SimpleStringProperty valueProperty;
//
//     /**
//      * 纬度
//      */
//     private SimpleDoubleProperty latitudeProperty;
//
//     /**
//      * 经度
//      */
//     private SimpleDoubleProperty longitudeProperty;
//
//     public RedisGEORow(String value, GeoCoordinate coordinate) {
//         this.setValue(value);
//         this.setLongitude(coordinate.getLongitude());
//         this.setLatitude(coordinate.getLatitude());
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
//     public SimpleDoubleProperty longitudeProperty() {
//         if (this.longitudeProperty == null) {
//             this.longitudeProperty = new SimpleDoubleProperty();
//         }
//         return this.longitudeProperty;
//     }
//
//     public void setLongitude(double longitude) {
//         this.longitudeProperty().setValue(longitude);
//     }
//
//     public double getLongitude() {
//         return this.latitudeProperty == null ? Double.NaN : this.latitudeProperty.get();
//     }
//
//     public SimpleDoubleProperty latitudeProperty() {
//         if (this.latitudeProperty == null) {
//             this.latitudeProperty = new SimpleDoubleProperty();
//         }
//         return this.latitudeProperty;
//     }
//
//     public void setLatitude(double latitude) {
//         this.latitudeProperty().setValue(latitude);
//     }
//
//     public double getLatitude() {
//         return this.latitudeProperty == null ? Double.NaN : this.latitudeProperty.get();
//     }
// }
