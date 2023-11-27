package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.fx.RedisKeyFilterHistoryPopup;
import cn.oyzh.easyredis.store.RedisKeyFilterHistoryStore;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * redis键过滤业务
 *
 * @author oyzh
 * @since 2023/07/19
 */
//@Slf4j
@StageAttribute(
        title = "Redis键过滤",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = RedisStyle.COMMON,
        resizeable = false,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyFilter.fxml"
)
public class RedisKeyFilterController extends Controller {

    /**
     * 过滤模式
     */
    @FXML
    private ClearableTextField pattern;

    /**
     * 树键
     */
    private RedisDBTreeItem treeItem;

    /**
     * 过滤历史
     */
    @FXML
    private SVGGlyph filterHistory;

    /**
     * 搜索历史弹窗
     */
    private RedisKeyFilterHistoryPopup filterHistoryPopup;

    /**
     * 过滤历史储存
     */
    private final RedisKeyFilterHistoryStore historyStore = RedisKeyFilterHistoryStore.INSTANCE;

    @Override
    public void onStageShown(WindowEvent event) {
        EventUtil.register(this);
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.treeItem = this.getStageProp("treeItem");
        String pattern = this.getStageProp("pattern");
        if (!StrUtil.isBlank(pattern)) {
            this.pattern.setText(pattern);
        }
        this.pattern.requestFocus();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        EventUtil.unregister(this);
        super.onStageHidden(event);
    }

    /**
     * 键过滤
     */
    @FXML
    private void keyFilter() {
        String pattern = this.pattern.getText();
        if (StrUtil.isNotBlank(pattern) && !"*".equals(pattern)) {
            this.historyStore.addHistory(pattern);
        }
        this.treeItem.doKeyFilter(pattern);
        this.closeStage();
    }

    /**
     * 过滤历史
     *
     * @param event 鼠标事件
     */
    @FXML
    private void filterHistory(MouseEvent event) {
        if (this.filterHistoryPopup == null) {
            this.filterHistoryPopup = new RedisKeyFilterHistoryPopup();
        }
        this.filterHistoryPopup.show(this.filterHistory, event.getScreenX(), event.getScreenY());
    }

    /**
     * 过滤历史点击事件
     *
     * @param kw 点击关键词
     */
    @EventReceiver(RedisEventTypes.REDIS_FILTER_HISTORY_SELECTED)
    private void filterHistorySelected(String kw) {
        if (!this.pattern.getTextTrim().equals(kw)) {
            this.pattern.setText(kw);
        }
    }

    /**
     * 键过滤按键事件
     *
     * @param e 按键事件
     */
    @FXML
    private void onPatternKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.keyFilter();
        } else if (e.getCode() == KeyCode.UP) {
            String currKW = this.pattern.getTextTrim();
            List<String> list = this.historyStore.getPatterns();
            String historyKW = this.getHistoryKW(currKW, list, true);
            if (historyKW != null) {
                this.filterHistorySelected(historyKW);
            }
        } else if (e.getCode() == KeyCode.DOWN) {
            String currKW = this.pattern.getTextTrim();
            List<String> list = this.historyStore.getPatterns();
            String historyKW = this.getHistoryKW(currKW, list, false);
            if (historyKW != null) {
                this.filterHistorySelected(historyKW);
            }
        }
    }

    /**
     * 获取历史词汇
     *
     * @param currKW 当前词汇
     * @param list   词汇列表
     * @param isUp   是否向上查找
     * @return 搜索词
     */
    private String getHistoryKW(String currKW, List<String> list, boolean isUp) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String kw;
        if (isUp) {
            // 获取首个
            if (currKW == null) {
                kw = CollUtil.getFirst(list);
            } else {
                int index = list.indexOf(currKW) + 1;
                // 获取最后一个
                if (index >= list.size()) {
                    kw = CollUtil.getLast(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        } else {
            // 获取最后一个
            if (currKW == null) {
                kw = CollUtil.getLast(list);
            } else {
                int index = list.indexOf(currKW) - 1;
                // 获取首个
                if (index <= 0) {
                    kw = CollUtil.getFirst(list);
                } else {// 获取目标索引数据
                    kw = list.get(index);
                }
            }
        }
        return kw;
    }
}
