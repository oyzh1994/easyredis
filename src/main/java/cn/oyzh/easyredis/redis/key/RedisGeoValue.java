// package cn.oyzh.easyredis.redis.key;
//
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
//
// import java.util.List;
//
// /**
//  * @author oyzh
//  * @since 2024-12-02
//  */
// public class RedisGeoValue implements RedisKeyValue<List<RedisGeoValue.RedisGeoRow>> {
//
//     @Getter
//     private List<RedisGeoRow> value;
//
//     public RedisGeoValue(List<RedisGeoRow> value) {
//         this.value = value;
//     }
//
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     public static class RedisGeoRow implements RedisKeyRow {
//
//         int index;
//
//         private String value;
//
//         private double latitude;
//
//         private double longitude;
//     }
// }
