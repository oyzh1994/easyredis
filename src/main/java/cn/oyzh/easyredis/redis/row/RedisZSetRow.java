// package cn.oyzh.easyredis.redis.row;
//
// import cn.oyzh.easyredis.redis.RedisRow;
// import cn.oyzh.fx.plus.property.DigitalDecimalProperty;
// import javafx.beans.property.SimpleStringProperty;
// import redis.clients.jedis.GeoCoordinate;
//
// /**
//  * redis zset行
//  *
//  * @author oyzh
//  * @since 2023/6/16
//  */
// public class RedisZSetRow extends RedisRow {
//
//     /**
//      * 值
//      */
//     private SimpleStringProperty valueProperty;
//
//     /**
//      * 分数
//      */
//     private DigitalDecimalProperty scoreProperty;
//
//     /**
//      * 纬度
//      */
//     private DigitalDecimalProperty latitudeProperty;
//
//     /**
//      * 经度
//      */
//     private DigitalDecimalProperty longitudeProperty;
//
//     public RedisZSetRow(String value, double score) {
//         this.setValue(value);
//         this.setScore(score);
//     }
//
//     public RedisZSetRow(String value, GeoCoordinate coordinate) {
//         this.setValue(value);
//         this.setGeoCoordinate(coordinate);
//     }
//
//     public void setGeoCoordinate(GeoCoordinate coordinate) {
//         this.setLatitude(coordinate.getLatitude());
//         this.setLongitude(coordinate.getLongitude());
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
//     public DigitalDecimalProperty scoreProperty() {
//         if (this.scoreProperty == null) {
//             this.scoreProperty = new DigitalDecimalProperty(5);
//         }
//         return scoreProperty;
//     }
//
//     public void setScore(double score) {
//         this.scoreProperty().setValue(score);
//     }
//
//     public double getScore() {
//         return this.scoreProperty == null ? Double.NaN : this.scoreProperty.getDouble();
//     }
//
//     public DigitalDecimalProperty longitudeProperty() {
//         if (this.longitudeProperty == null) {
//             this.longitudeProperty = new DigitalDecimalProperty(5);
//         }
//         return this.longitudeProperty;
//     }
//
//     public void setLongitude(double longitude) {
//         this.longitudeProperty().setValue(longitude);
//     }
//
//     public double getLongitude() {
//         return this.longitudeProperty == null ? Double.NaN : this.longitudeProperty.getDouble();
//     }
//
//     public DigitalDecimalProperty latitudeProperty() {
//         if (this.latitudeProperty == null) {
//             this.latitudeProperty = new DigitalDecimalProperty(5);
//         }
//         return this.latitudeProperty;
//     }
//
//     public void setLatitude(double latitude) {
//         this.latitudeProperty().setValue(latitude);
//     }
//
//     public double getLatitude() {
//         return this.latitudeProperty == null ? Double.NaN : this.latitudeProperty.getDouble();
//     }
// }
