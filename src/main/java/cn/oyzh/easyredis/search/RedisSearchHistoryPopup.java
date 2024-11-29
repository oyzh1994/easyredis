// package cn.oyzh.easyredis.search;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.oyzh.easyredis.store.RedisSearchHistoryStore;
// import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;
//
// import java.util.List;
//
// /**
//  * redis搜索历史弹窗
//  *
//  * @author oyzh
//  * @since 2023/4/24
//  */
// public class RedisSearchHistoryPopup extends SearchHistoryPopup {
//
//     /**
//      * 搜索历史储存
//      */
//     private final RedisSearchHistoryStore historyStore = RedisSearchHistoryStore.INSTANCE;
//
//     @Override
//     public List<String> getHistories() {
//         List<String> list = this.historyStore.getSearchKw();
//         if (CollUtil.isNotEmpty(list)) {
//             return list.reversed();
//         }
//         return list;
//     }
// }
