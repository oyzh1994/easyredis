// package cn.oyzh.easyredis.redis;
//
// import lombok.Getter;
// import lombok.experimental.Accessors;
//
// import java.util.List;
// import java.util.Map;
//
// /**
//  * redis节点，支持行数据
//  *
//  * @author oyzh
//  * @since 2023/6/16
//  */
// public abstract class RedisRowNode<R extends RedisRow> extends RedisNode {
//
//     /**
//      * 行数据
//      */
//     @Getter
//     @Accessors(chain = true, fluent = true)
//     protected List<R> value;
//
//     public abstract List<Map<String, Object>> getSerializableValue();
// }
