package cn.oyzh.easyredis.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.store.RedisSearchHistoryStore;
import cn.oyzh.fx.plus.controls.FXListView;
import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.util.FontUtil;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Popup;

import java.util.Collections;
import java.util.List;

/**
 * redis搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class RedisSearchHistoryPopup extends SearchHistoryPopup {

    // /**
    //  * 类型
    //  */
    // private final int type;
    //
    // /**
    //  * 列表视图组件
    //  */
    // private FXListView<String> listView;

    /**
     * 搜索历史储存
     */
    private final RedisSearchHistoryStore historyStore = RedisSearchHistoryStore.INSTANCE;

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.getSearchKw();
        if (CollUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }

    // public RedisSearchHistoryPopup(int type) {
    //     this.type = type;
    // }
    //
    // @Override
    // public void show(Node node, double anchorX, double anchorY) {
    //     // 初始化内容
    //     this.initContent();
    //     // 展示弹窗，修正x、y值
    //     super.show(node, anchorX - 8, anchorY + 5);
    // }
    //
    // /**
    //  * 初始化内容
    //  */
    // private void initContent() {
    //     // 初始化视图列表，弹窗
    //     if (this.listView == null) {
    //         this.listView = new FXListView<>();
    //         this.listView.setFontSize(12);
    //         this.getContent().setAll(listView);
    //         this.setAutoHide(true);
    //         this.listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    //             if (newValue != null) {
    //                 if (this.type == 1) {
    //                     EventUtil.fire(RedisEventTypes.REDIS_SEARCH_HISTORY_SELECTED, newValue);
    //                 } else {
    //                     EventUtil.fire(RedisEventTypes.REDIS_REPLACE_HISTORY_SELECTED, newValue);
    //                 }
    //                 this.hide();
    //             }
    //         });
    //         this.focusedProperty().addListener((observable, oldValue, newValue) -> {
    //             if (!newValue) {
    //                 this.hide();
    //             }
    //         });
    //     }
    //     // 清除旧数据
    //     this.listView.getItems().clear();
    //     List<String> list = this.type == 1 ? this.historyStore.getSearchKw() : this.historyStore.getReplaceKw();
    //     // 无数据设置默认宽高
    //     if (list.isEmpty()) {
    //         this.listView.setRealWidth(50);
    //         this.listView.setRealHeight(120);
    //     } else {
    //         // 计算列表视图宽
    //         double width = 0;
    //         for (String s : list) {
    //             double w = FontUtil.stringWidth(s);
    //             if (w > width) {
    //                 width = w;
    //             }
    //         }
    //         if (width > 300) {
    //             width = 300;
    //         } else {
    //             width += 40;
    //         }
    //         // 计算列表视图高
    //         double fontHeight = FontUtil.calcFontHeight(12) + 8;
    //         double height = fontHeight * list.size();
    //         if (list.size() == 1) {
    //             height += 5;
    //         }
    //         if (height > 300) {
    //             height = 300;
    //         }
    //         // 反转数组，最新的数据排在前面
    //         Collections.reverse(list);
    //         // 初始化列表
    //         this.listView.getItems().setAll(list);
    //         this.listView.setRealWidth(width);
    //         this.listView.setRealHeight(height);
    //     }
    //     this.listView.setCursor(Cursor.HAND);
    // }
}
