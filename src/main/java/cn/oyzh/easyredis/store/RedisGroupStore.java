// package cn.oyzh.easyredis.store;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.core.lang.UUID;
// import cn.hutool.core.util.StrUtil;
// import cn.hutool.json.JSONUtil;
// import cn.hutool.log.StaticLog;
// import cn.oyzh.easyredis.RedisConst;
// import cn.oyzh.easyredis.domain.RedisGroup;
// import cn.oyzh.fx.common.store.ArrayFileStore;
// import lombok.NonNull;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// /**
//  * redis分组存储
//  *
//  * @author oyzh
//  * @since 2023/6/22
//  */
// //@Slf4j
// public class RedisGroupStore extends ArrayFileStore<RedisGroup> {
//
//     /**
//      * 当前实例
//      */
//     public static final RedisGroupStore INSTANCE = new RedisGroupStore();
//
//     /**
//      * 已加载的redis键
//      */
//     private final List<RedisGroup> redisGroups;
//
//     {
//         this.filePath(RedisConst.STORE_PATH + "redis_group.json");
//         JulLog.info("RedisGroupStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//         this.redisGroups = this.load();
//     }
//
//     @Override
//     public synchronized List<RedisGroup> load() {
//         if (this.redisGroups == null) {
//             String text = FileUtil.readString(this.storeFile(), this.charset());
//             if (StrUtil.isBlank(text)) {
//                 return new ArrayList<>();
//             }
//             List<RedisGroup> redisGroups = JSONUtil.toList(text, RedisGroup.class);
//             if (CollUtil.isNotEmpty(redisGroups)) {
//                 redisGroups = redisGroups.parallelStream().sorted().collect(Collectors.toList());
//             }
//             return redisGroups;
//         }
//         return this.redisGroups;
//     }
//
//     /**
//      * 添加分组
//      *
//      * @param groupName 分组名称
//      * @return 结果
//      */
//     public synchronized RedisGroup add(@NonNull String groupName) {
//         RedisGroup group = new RedisGroup(UUID.fastUUID().toString(true), groupName, false);
//         if (this.add(group)) {
//             return group;
//         }
//         return null;
//     }
//
//     @Override
//     public synchronized boolean add(@NonNull RedisGroup redisGroup) {
//         try {
//             if (!this.redisGroups.contains(redisGroup)) {
//                 // 添加到集合
//                 this.redisGroups.add(redisGroup);
//                 // 更新数据
//                 return this.save(this.redisGroups);
//             }
//         } catch (Exception e) {
//             JulLog.warn("add error,err:{}", e.getMessage());
//         }
//         return false;
//     }
//
//     @Override
//     public synchronized boolean update(@NonNull RedisGroup redisGroup) {
//         try {
//             // 更新数据
//             if (this.redisGroups.contains(redisGroup)) {
//                 return this.save(this.redisGroups);
//             }
//         } catch (Exception e) {
//             JulLog.warn("update error,err:{}", e.getMessage());
//         }
//         return false;
//     }
//
//     @Override
//     public synchronized boolean delete(@NonNull RedisGroup redisGroup) {
//         try {
//             // 删除数据
//             if (this.redisGroups.remove(redisGroup)) {
//                 return this.save(this.redisGroups);
//             }
//         } catch (Exception e) {
//             JulLog.warn("delete error,err:{}", e.getMessage());
//             return false;
//         }
//         return true;
//     }
//
//     /**
//      * 是否存在此分组信息
//      *
//      * @param redisGroup 分组信息
//      * @return 结果
//      */
//     public boolean exist(RedisGroup redisGroup) {
//         if (redisGroup == null) {
//             return false;
//         }
//         for (RedisGroup group : this.redisGroups) {
//             if (Objects.equals(group.getName(), redisGroup.getName()) && group != redisGroup) {
//                 return true;
//             }
//         }
//         return false;
//     }
// }
