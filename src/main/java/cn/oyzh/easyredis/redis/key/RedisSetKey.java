// package cn.oyzh.easyredis.redis.key;
//
// import cn.oyzh.common.util.CollectionUtil;
// import cn.oyzh.easyredis.redis.RedisRowKey;
// import cn.oyzh.easyredis.redis.row.RedisSetRow;
//
// import java.util.ArrayList;
// import java.util.Set;
//
// /**
//  * redis set键
//  *
//  * @author oyzh
//  * @since 2023/6/16
//  */
// public class RedisSetKey extends RedisRowKey<RedisSetRow> {
//
//     /**
//      * 设置键数据
//      *
//      * @param value 键数据
//      */
//     public void value(Set<String> value) {
//         this.value = new ArrayList<>();
//         if (CollectionUtil.isNotEmpty(value)) {
//             for (String s : value) {
//                 this.value.add(new RedisSetRow(s));
//             }
//         }
//     }
// }
