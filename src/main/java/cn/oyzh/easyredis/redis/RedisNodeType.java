// package cn.oyzh.easyredis.redis;
//
// import cn.oyzh.common.util.StringUtil;
//
// /**
//  * redis节点类型
//  *
//  * @author oyzh
//  * @since 2023/6/1
//  */
// public enum RedisNodeType {
//     STRING,
//     SET,
//     ZSET,
//     LIST,
//     HASH,
//     HYPERLOGLOG,
//     STREAM;
//
//     public static RedisNodeType valueOfType(String type) {
//         if (StringUtil.isNotBlank(type)) {
//             return switch (type.toLowerCase()) {
//                 case "string" -> STRING;
//                 case "set" -> SET;
//                 case "zset" -> ZSET;
//                 case "list" -> LIST;
//                 case "hash" -> HASH;
//                 case "hyperloglog" -> HYPERLOGLOG;
//                 case "stream" -> STREAM;
//                 default -> null;
//             };
//         }
//         return null;
//     }
// }
